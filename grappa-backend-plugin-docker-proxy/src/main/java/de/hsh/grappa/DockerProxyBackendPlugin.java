package de.hsh.grappa;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.google.common.base.Strings;
import de.hsh.grappa.plugin.BackendPlugin;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

/**
 * This class acts like a grader Backend Plugin to the Grappa
 * web service while setting up a Docker container and passing
 * on the submission to the grappa-grader-backend-starter module.
 *
 * This class delivers the submission resource file in a specific
 * directory for the GraderBackendStarter module to retrieve and
 * and start grading. After the grading process has finished, it
 * retrieves the results (a Proforma response resource in case of
 * success, or a grader error stack trace in case of failure),
 * and returns it back to the Grappa web service.
 */
public class DockerProxyBackendPlugin implements BackendPlugin {
    private static final Logger log = LoggerFactory.getLogger(DockerProxyBackendPlugin.class);

    private static final String GRADER_EXCEPTION_STACKTRACE_FILE_PATH =
        "/var/grb_starter/tmp/grader_exception_stacktrace";

    private String graderId = "N/A";
    private String gradeProcId = "N/A";
    private String dockerContainerImage;
    private String dockerHost;
    private String copySubmissionToDirectoryPath;
    private String responseResultDirectoryPath;

    private static final String GRAPPA_CONTEXT_GRADER_ID = "Grappa.Context.GraderId";
    private static final String GRAPPA_CONTEXT_GRADE_PROCESS_ID = "Grappa.Context.GraderProcessId";

    @Override
    public void init(Properties props) throws Exception {
        log.debug("Entering DockerProxyBackendPlugin.init()...");
        try {
            graderId = props.get(GRAPPA_CONTEXT_GRADER_ID).toString();
            gradeProcId = props.get(GRAPPA_CONTEXT_GRADE_PROCESS_ID).toString();
        } catch (Exception e) {
            log.warn("No context logging IDs available.");
        }
        dockerContainerImage = props.get("dockerproxybackendplugin.container_image").toString();
        dockerHost = props.get("dockerproxybackendplugin.docker_host").toString();
        copySubmissionToDirectoryPath =
            props.get("dockerproxybackendplugin.copy_submission_to_directory_path").toString();
        responseResultDirectoryPath = props.get("dockerproxybackendplugin.response_result_directory_path").toString();
    }

    @Override
    public ResponseResource grade(SubmissionResource submission) throws Exception {
        log.debug("[GraderId: '{}', GradeProcId: '{}']: Entering DockerProxyBackendPlugin.grade()...",
            graderId, gradeProcId);
        log.info("[GraderId: '{}', GradeProcId: '{}']: Setting up docker connection to: {}",
            graderId, gradeProcId, dockerHost);

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(dockerHost)
            .withDockerTlsVerify(false)
            .build();

        try (JerseyDockerHttpClient httpClient = new JerseyDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build(); DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient)) {

            log.info("[GraderId: '{}', GradeProcId: '{}']: Pinging docker daemon...",
                graderId, gradeProcId);
            dockerClient.pingCmd().exec();
            log.info("[GraderId: '{}', GradeProcId: '{}']: Ping successful.",
                graderId, gradeProcId);

             log.info("[GraderId: '{}', GradeProcId: '{}']: Creating container from image '{}'...",
                graderId, gradeProcId, dockerContainerImage);
             String containerId = DockerController.createContainer(dockerClient, dockerContainerImage);
             log.info("[GraderId: '{}', GradeProcId: '{}']: Container with id '{}' created",
                graderId, gradeProcId, containerId);

             copySubmissionToContainer(dockerClient, containerId, submission);

             log.info("[GraderId: '{}', GradeProcId: '{}']: Starting container...",
                graderId, gradeProcId);
             // Starts container and subsequentlly the grading process
             DockerController.startContainer(dockerClient, containerId);

             long exitCode = -1;
            try {
                exitCode = waitForContainerToFinishGrading(dockerClient, containerId);
                log.info("[GraderId: '{}', GradeProcId: '{}']: Container finished with exit code {}",
                    graderId, gradeProcId, exitCode);
            } catch (InterruptedException e) {
                log.info("[GraderId: '{}', GradeProcId: '{}']: Thread interrupted while waiting for the grading result, proceeding to deleting docker " +
                    "container...", graderId, gradeProcId);
                Thread.currentThread().interrupt(); // preserve interrupt flag
            }

            ResponseResource responseResource = null;
            String graderStackTrace = null;
            if (0 != exitCode) {
                log.error("[GraderId: '{}', GradeProcId: '{}']: Grader finished abnormally with exit " +
                    "code {}", graderId, gradeProcId, exitCode);
                log.info("[GraderId: '{}', GradeProcId: '{}']: Fetching grader stack trace file: {}",
                    graderId, gradeProcId, GRADER_EXCEPTION_STACKTRACE_FILE_PATH);
                try (InputStream is = DockerController.fetchFile(dockerClient, containerId,
                    GRADER_EXCEPTION_STACKTRACE_FILE_PATH)) {
                    graderStackTrace = IOUtils.toString(is, "utf8");
                } catch (Exception e) {
                    log.error("[GraderId: '{}', GradeProcId: '{}']: Could not load grader stack trace file '{}'.",
                        graderId, gradeProcId, GRADER_EXCEPTION_STACKTRACE_FILE_PATH);
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            } else {
                // Thread interruption: Do not expect (nor care about) any result or container log
                // with the running container and grading process about to be stopped and removed
                if (!Thread.currentThread().isInterrupted()) {
                    try {
                        responseResource = fetchProformaResponseFile(dockerClient, containerId);
                    } catch (InterruptedException e) {
                        log.info("[GraderId: '{}', GradeProcId: '{}']: Thread interruption during fetching response result file.",
                            graderId, gradeProcId);
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("[GraderId: '{}', GradeProcId: '{}']: Failed to fetch response file from container.",
                            graderId, gradeProcId);
                        log.error(e.getMessage());
                        log.error(ExceptionUtils.getStackTrace(e));
                    }
                }
            }

            if (!Thread.currentThread().isInterrupted()) {
                log.info("[GraderId: '{}', GradeProcId: '{}']: Fetching container log...",
                    graderId, gradeProcId);
                try {
                    List<String> logs = DockerController.getContainerLog(dockerClient, containerId);
                    // display the logs as a single transactional text block so the
                    // lines don't get mixed up with other concurrent logging events
                    StringBuilder sb = new StringBuilder("[START] ======================================================");
                    sb.append(System.getProperty("line.separator"));
                    //logs.forEach(sb::append);
                    for(String s : logs) {
                        sb.append("\t" + s);
                        sb.append(System.getProperty("line.separator"));
                    }
                    sb.append("[END] ======================================================");
                    log.info("[GraderId: '{}', GradeProcId: '{}']: [CONTAINER LOG]:{}{}",
                        graderId, gradeProcId, System.getProperty("line.separator"), sb.toString());
                } catch (InterruptedException e) {
                    log.info("[GraderId: '{}', GradeProcId: '{}']: Thread interruption during fetching response result file.",
                        graderId, gradeProcId);
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("[GraderId: '{}', GradeProcId: '{}']: Fetching container log failed.",
                        graderId, gradeProcId);
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }

            try {
                log.debug("[GraderId: '{}', GradeProcId: '{}']: Stopping container '{}'...",
                    graderId, gradeProcId, containerId);
                DockerController.stopContainer(dockerClient, containerId);
                log.debug("[GraderId: '{}', GradeProcId: '{}']: Container stopped: '{}'",
                    graderId, gradeProcId, containerId);
            } catch (Exception e) {
                log.warn("[GraderId: '{}', GradeProcId: '{}']: Failed to stop container (it may already " +
                    "have stopped): '{}'", graderId, gradeProcId, containerId);
            }

            try {
                log.debug("[GraderId: '{}', GradeProcId: '{}']: Removing container '{}'...",
                    graderId, gradeProcId, containerId);
                DockerController.removeContainer(dockerClient, containerId);
                log.debug("[GraderId: '{}', GradeProcId: '{}']: Container removed: '{}'",
                    graderId, gradeProcId, containerId);
            } catch (Exception e) {
                log.error("[GraderId: '{}', GradeProcId: '{}']: Failed to remove container '{}'",
                    graderId, gradeProcId, containerId);
                log.error(e.getMessage());
            }

            log.info("[GraderId: '{}', GradeProcId: '{}']: DockerProxyBackendPlugin finished.",
                graderId, gradeProcId);
            if (!Strings.isNullOrEmpty(graderStackTrace)) {
                log.info("[GraderId: '{}', GradeProcId: '{}']: Re-throwing grader exception stack trace.",
                    graderId, gradeProcId);
                throw new GraderException(graderStackTrace);
            }

            // if the grader was interrupted, but shut down gracefully, it should still
            // return a null proforma response, and that's what we will return
            return responseResource;
        }
    }

    private void copySubmissionToContainer(DockerClient dockerClient, String containerId,
                                           SubmissionResource subm) throws Exception {
        String submDestFileName = subm.getMimeType()
            .equals(MimeType.XML) ? "submission.xml" : "submission.zip";
        log.info("[GraderId: '{}', GradeProcId: '{}']: Copying submission file '{}' to docker container.",
            graderId, gradeProcId, FilenameUtils.separatorsToUnix
                (Paths.get(copySubmissionToDirectoryPath, submDestFileName).toString()));
        DockerController.copyFile(subm.getContent(), copySubmissionToDirectoryPath,
            submDestFileName, dockerClient, containerId, true);
    }

    private long waitForContainerToFinishGrading(DockerClient dockerClient, String containerId)
        throws InterruptedException {
        InspectContainerResponse.ContainerState state = dockerClient.inspectContainerCmd(containerId).exec().getState();
        for (int i = 3; !Thread.currentThread().isInterrupted()
            && state.getStatus().toUpperCase().equals("RUNNING"); ++i) {
            if (i % 4 == 0) // Don't spam the log with waiting messages
                log.debug("[GraderId: '{}', GradeProcId: '{}']: Waiting for the grading process to finish...",
                    graderId, gradeProcId);
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
        log.info("[GraderId: '{}', GradeProcId: '{}']: Fetching response file: {}",
            graderId, gradeProcId, respFilePath);
        InputStream resp = null;
        try {
            resp = DockerController.fetchFile(dockerClient, containerId,
                respFilePath);
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            log.info("[GraderId: '{}', GradeProcId: '{}']: Response file '{}' does not exist.",
                graderId, gradeProcId, respFilePath);
            return null;
        }
        return resp;
    }

    private ResponseResource fetchProformaResponseFile(DockerClient dockerClient, String containerId) throws Exception {
        // We don't know which response mimetype the grader will supply, so we test for both
        // TODO: Grappa should dynamically supply this backend plugin's properties object
        // with a property indicating which mimetype the grader will likely produce based
        // on what's specified in the submission's result-spec element
        String responseXmlPath = Paths.get(responseResultDirectoryPath, "response.xml").toString();
        responseXmlPath = FilenameUtils.separatorsToUnix(responseXmlPath);
        String responseZipPath = Paths.get(responseResultDirectoryPath, "response.zip").toString();
        responseZipPath = FilenameUtils.separatorsToUnix(responseZipPath);

        ResponseResource responseResource = null;
        InputStream resp = tryFetchResponseFile(dockerClient, containerId, responseZipPath);
        if (null != resp) {
            byte[] respBytes = IOUtils.toByteArray(resp);
            responseResource = new ResponseResource(respBytes, MimeType.ZIP);
        } else {
            resp = tryFetchResponseFile(dockerClient, containerId, responseXmlPath);
            if (null != resp) { // try for the other one
                byte[] respBytes = IOUtils.toByteArray(resp);
                responseResource = new ResponseResource(respBytes, MimeType.XML);
            } else
                throw new FileNotFoundException(String.format("Neither '%s' nor '%s' could be " +
                    "retrieved from the container.", responseZipPath, responseXmlPath));
        }

        return responseResource;
    }
}