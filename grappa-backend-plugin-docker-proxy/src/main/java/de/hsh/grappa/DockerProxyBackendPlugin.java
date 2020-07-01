package de.hsh.grappa;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
import com.google.common.base.Strings;
import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaSubmission;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class DockerProxyBackendPlugin implements BackendPlugin {
    private static Logger log = LoggerFactory.getLogger(DockerProxyBackendPlugin.class);

    private static final String GRADER_EXCEPTION_STACKTRACE_FILE_PATH =
        "/var/grb_starter/tmp/grader_exception_stacktrace";

    private String lmsId = "N/A";
    private String graderId = "N/A";
    private String gradeProcId = "N/A";
    private String dockerContainerImage;
    private String dockerHost;
    private String copySubmissionToDirectoryPath;
    private String responseResultDirectoryPath;

    public void setLmsId(String lmsId) {
        this.lmsId = lmsId;
    }

    public void setGraderId(String graderId) {
        this.graderId = graderId;
    }

    public void setGradeProcId(String gradeProcId) {
        this.gradeProcId = gradeProcId;
    }

    @Override
    public void init(Properties props) throws Exception {
        log.debug("Entering DockerProxyBackendPlugin.init()...");
        dockerContainerImage = props.get("dockerproxybackendplugin.container_image").toString();
        dockerHost = props.get("dockerproxybackendplugin.docker_host").toString();
        copySubmissionToDirectoryPath =
            props.get("dockerproxybackendplugin.copy_submission_to_directory_path").toString();
        responseResultDirectoryPath = props.get("dockerproxybackendplugin.response_result_directory_path").toString();
    }

    @Override
    public ProformaResponse grade(ProformaSubmission proformaSubmission) throws Exception {
        log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Entering DockerProxyBackendPlugin.grade()...",
            lmsId, graderId, gradeProcId);
        log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Setting up docker connection to: {}",
            lmsId, graderId, gradeProcId, dockerHost);

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(dockerHost)
            .withDockerTlsVerify(false)
            .build();

        try (DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory();
             DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                 .withDockerCmdExecFactory(dockerCmdExecFactory)
                 .build()) {

            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Pinging docker daemon...",
                lmsId, graderId, gradeProcId);
            dockerClient.pingCmd().exec();
            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Ping successful.",
                lmsId, graderId, gradeProcId);

            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Creating container from image '{}'...",
                lmsId, graderId, gradeProcId, dockerContainerImage);
            String containerId = DockerController.createContainer(dockerClient, dockerContainerImage);
            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Container with id '{}' created",
                lmsId, graderId, gradeProcId, containerId);

            copySubmissionToContainer(dockerClient, containerId, proformaSubmission);

            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Starting container...",
                lmsId, graderId, gradeProcId);
            // Starts container and subsequentlly the grading process
            DockerController.startContainer(dockerClient, containerId);

            long exitCode = -1;
            try {
                exitCode = waitForContainerToFinishGrading(dockerClient, containerId);
                log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Container finished with exit code {}",
                    lmsId, graderId, gradeProcId, exitCode);
            } catch (InterruptedException e) {
                log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Thread interrupted while waiting for the grading result, proceeding to deleting docker " +
                    "container...", lmsId, graderId, gradeProcId);
                Thread.currentThread().interrupt(); // preserve interrupt flag
            }

            ProformaResponse proformaResponse = null;
            String graderStackTrace = null;
            if (0 != exitCode) {
                log.error("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Grader finished abnormally with exit " +
                    "code {}", lmsId, graderId, gradeProcId, exitCode);
                log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Fetching grader stack trace file: {}",
                    lmsId, graderId, gradeProcId, GRADER_EXCEPTION_STACKTRACE_FILE_PATH);
                try (InputStream is = DockerController.fetchFile(dockerClient, containerId,
                    GRADER_EXCEPTION_STACKTRACE_FILE_PATH)) {
                    graderStackTrace = IOUtils.toString(is, "utf8");
                } catch (Exception e) {
                    log.error("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Could not load grader stack trace file '{}'.",
                        lmsId, graderId, gradeProcId, GRADER_EXCEPTION_STACKTRACE_FILE_PATH);
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else {
                // Thread interruption: Do not expect (nor care about) any result or container log
                // with the running container and grading process about to be stopped and removed
                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        proformaResponse = fetchProformaResponseFile(dockerClient, containerId);
                    } catch (InterruptedException e) {
                        log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Thread interruption during fetching response result file.",
                            lmsId, graderId, gradeProcId);
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Failed to fetch response file from container.",
                            lmsId, graderId, gradeProcId);
                        log.error(e.getMessage());
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            if (!Thread.currentThread().isInterrupted()) {
                log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Fetching container log...",
                    lmsId, graderId, gradeProcId);
                try {
                    List<String> logs = DockerController.getContainerLog(dockerClient, containerId);
                    for (String s : logs)
                        log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: [CONTAINER LOG] {}",
                            lmsId, graderId, gradeProcId, s);
                } catch (InterruptedException e) {
                    log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Thread interruption during fetching response result file.",
                        lmsId, graderId, gradeProcId);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Fetching container log failed.",
                        lmsId, graderId, gradeProcId);
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }

            try {
                log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Stopping container '{}'...",
                    lmsId, graderId, gradeProcId, containerId);
                DockerController.stopContainer(dockerClient, containerId);
                log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Container stopped: '{}'",
                    lmsId, graderId, gradeProcId, containerId);
            } catch (Exception e) {
                log.warn("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Failed to stop container (it may already " +
                    "have stopped): '{}'", lmsId, graderId, gradeProcId, containerId);
            }

            try {
                log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Removing container '{}'...",
                    lmsId, graderId, gradeProcId, containerId);
                DockerController.removeContainer(dockerClient, containerId);
                log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Container removed: '{}'",
                    lmsId, graderId, gradeProcId, containerId);
            } catch (Exception e) {
                log.error("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Failed to remove container '{}'",
                    lmsId, graderId, gradeProcId, containerId);
                log.error(e.getMessage());
            }

            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: DockerProxyBackendPlugin finished.",
                lmsId, graderId, gradeProcId);
            if (!Strings.isNullOrEmpty(graderStackTrace)) {
                log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Re-throwing grader exception stack trace.",
                    lmsId, graderId, gradeProcId);
                throw new GraderException(graderStackTrace);
            }

            // if the grader was interrupted, but shut down gracefully, it should still
            // return a null proforma response, and that's what we will return
            return proformaResponse;
        }
    }

    private void copySubmissionToContainer(DockerClient dockerClient, String containerId,
                                           ProformaSubmission subm) throws Exception {
        byte[] proformaSubmBytes = SerializationUtils.serialize(subm);
        String submDestFileName = subm.getMimeType()
            .equals(MimeType.XML) ? "submission.xml" : "submission.zip";
        log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Copying submission file '{}' to docker container.",
            lmsId, graderId, gradeProcId, FilenameUtils.separatorsToUnix
                (Paths.get(copySubmissionToDirectoryPath, submDestFileName).toString()));
        DockerController.copyFile(proformaSubmBytes, copySubmissionToDirectoryPath,
            submDestFileName, dockerClient, containerId, true);
    }

    private long waitForContainerToFinishGrading(DockerClient dockerClient, String containerId)
        throws InterruptedException {
        InspectContainerResponse.ContainerState state = dockerClient.inspectContainerCmd(containerId).exec().getState();
        for (int i = 3; !Thread.currentThread().isInterrupted()
            && state.getStatus().toUpperCase().equals("RUNNING"); ++i) {
            if (i % 4 == 0) // Don't spam the log with waiting messages
                log.debug("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Waiting for the grading process to finish...",
                    lmsId, graderId, gradeProcId);
            Thread.sleep(1000);
            state = dockerClient.inspectContainerCmd(containerId).exec().getState();
        }

        return state.getExitCodeLong();
    }

    /**
     *
     * @param dockerClient
     * @param containerId
     * @param respFilePath
     * @return the file stream if found, null otherwise
     * @throws Exception
     */
    private InputStream tryFetchResponseFile(DockerClient dockerClient, String containerId,
                                             String respFilePath) throws Exception {
        log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Fetching response file: {}",
            lmsId, graderId, gradeProcId, respFilePath);
        InputStream resp = null;
        try {
            resp = DockerController.fetchFile(dockerClient, containerId,
                respFilePath);
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            log.info("[LmsId: '{}', GraderId: '{}', GradeProcId: '{}']: Response file '{}' does not exist.",
                lmsId, graderId, gradeProcId, respFilePath);
            return null;
        }
        return resp;
    }

    private ProformaResponse fetchProformaResponseFile(DockerClient dockerClient, String containerId) throws Exception {
        // We don't know which response mimetype the grader will supply, so we test for both
        // TODO: Grappa should dynamically supply this backend plugin's properties object
        // with a property indicating which mimetype the grader will likely produce based
        // on what's specified in the submission's result-spec element
        String responseXmlPath = Paths.get(responseResultDirectoryPath, "response.xml").toString();
        responseXmlPath = FilenameUtils.separatorsToUnix(responseXmlPath);
        String responseZipPath = Paths.get(responseResultDirectoryPath, "response.zip").toString();
        responseZipPath = FilenameUtils.separatorsToUnix(responseZipPath);

        ProformaResponse proformaResponse = null;
        InputStream resp = tryFetchResponseFile(dockerClient, containerId, responseZipPath);
        if (null != resp) {
            byte[] respBytes = IOUtils.toByteArray(resp);
            proformaResponse = new ProformaResponse(respBytes, MimeType.ZIP);
        } else {
            resp = tryFetchResponseFile(dockerClient, containerId, responseXmlPath);
            if (null != resp) { // try for the other one
                byte[] respBytes = IOUtils.toByteArray(resp);
                proformaResponse = new ProformaResponse(respBytes, MimeType.XML);
            } else
                throw new FileNotFoundException(String.format("Neither '%s' nor '%s' could be " +
                    "retrieved from the container.", responseZipPath, responseXmlPath));
        }

        return proformaResponse;
    }
}