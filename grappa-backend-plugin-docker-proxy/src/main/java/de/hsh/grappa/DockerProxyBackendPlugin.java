package de.hsh.grappa;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.jaxrs.JerseyDockerCmdExecFactory;
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

    private String dockerContainerImage;
    private String dockerHost;
    private String copySubmissionToDirecotryPath;
    private String responseResultDirectoryPath;

    @Override
    public void init(Properties props) throws Exception {
        log.debug("Entering DockerProxyBackendPlugin.init()...");
        dockerContainerImage = props.get("dockerproxybackendplugin.container_image").toString();
        dockerHost = props.get("dockerproxybackendplugin.docker_host").toString();
        copySubmissionToDirecotryPath =
            props.get("dockerproxybackendplugin.copy_submission_to_directory_path").toString();
        responseResultDirectoryPath = props.get("dockerproxybackendplugin.response_result_directory_path").toString();
    }

    @Override
    public ProformaResponse grade(ProformaSubmission proformaSubmission) throws Exception {
        log.debug("Entering DockerProxyBackendPlugin.grade()...");
        log.info("Setting up docker connection to: {}", dockerHost);

        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost(dockerHost)
            .withDockerTlsVerify(false)
            .build();

        try (DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory();
             DockerClient dockerClient = DockerClientBuilder.getInstance(config)
                 .withDockerCmdExecFactory(dockerCmdExecFactory)
                 .build()) {

            log.info("Pinging docker daemon...");
            dockerClient.pingCmd().exec();
            log.info("Ping successful.");

            log.info("Creating container from image '{}'...", dockerContainerImage);
            String containerId = DockerController.createContainer(dockerClient, dockerContainerImage);
            log.info("Container with id '{}' created", containerId);

            copySubmissionToContainer(dockerClient, containerId, proformaSubmission);

            log.info("Starting container...");
            // Starts container and subsequentlly the grading process
            DockerController.startContainer(dockerClient, containerId);

            try {
                waitForContainerToFinishGrading(dockerClient, containerId);
            } catch (InterruptedException e) {
                log.info("Thread interrupted while waiting for the grading result, proceeding to deleting docker " +
                    "container...");
                Thread.currentThread().interrupt(); // preserve interrupt flag
            }

            ProformaResponse proformaResponse = null;
            // Thread interruption: Do not expect (nor care about) any result or container log
            // with the running container and grading process about to be stopped and removed
            if (!Thread.currentThread().isInterrupted()) {
                try {
                    proformaResponse = fetchProformaResponseFile(dockerClient, containerId);

                } catch (InterruptedException e) {
                    log.info("Thread interruption during fetching response result file.");
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Failed to fetch response file from container.");
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }

            if (!Thread.currentThread().isInterrupted()) {
                log.info("Fetching container log...");
                try {
                    log.info("=====================================================");
                    List<String> logs = DockerController.getContainerLog(dockerClient, containerId);
                    for (String s : logs)
                        log.info("[CONTAINER LOG] {}", s);
                    log.info("=====================================================");
                } catch (InterruptedException e) {
                    log.info("Thread interruption during fetching response result file.");
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    log.error("Fetching container log failed.");
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }

            try {
                log.debug("[ContainerId: '{}'] Stopping container...", containerId);
                DockerController.stopContainer(dockerClient, containerId);
                log.debug("[ContainerId: '{}'] Container stopped", containerId);
            } catch (Exception e) {
                log.warn("[ContainerId: '{}'] Failed to stop container (it may already be stopped).", containerId);
            }

            try {
                log.debug("[ContainerId: '{}'] Removing container...", containerId);
                DockerController.removeContainer(dockerClient, containerId);
                log.debug("[ContainerId: '{}'] Container removed.", containerId);
            } catch (Exception e) {
                log.error("[ContainerId: '{}'] Failed to remove Container.", containerId);
                log.error(e.getMessage());
            }

            log.info("DockerProxyBackendPlugin finished.");
            return proformaResponse;
        }
    }

    private void copySubmissionToContainer(DockerClient dockerClient, String containerId,
                                           ProformaSubmission subm) throws Exception {
        byte[] proformaSubmBytes = SerializationUtils.serialize(subm);
        String submDestFileName = subm.getMimeType()
            .equals(MimeType.XML) ? "submission.xml" : "submission.zip";
        log.info("Copying submission file to docker container: '{}'...",
            FilenameUtils.separatorsToUnix(Paths.get(copySubmissionToDirecotryPath, submDestFileName).toString()));
        DockerController.copyFile(proformaSubmBytes, copySubmissionToDirecotryPath,
            submDestFileName, dockerClient, containerId, true);
    }

    private void waitForContainerToFinishGrading(DockerClient dockerClient, String containerId)
        throws InterruptedException {
        InspectContainerResponse.ContainerState state = dockerClient.inspectContainerCmd(containerId).exec().getState();
        for (int i = 3; !Thread.currentThread().isInterrupted()
            && state.getStatus().toUpperCase().equals("RUNNING"); ++i) {
            if (i % 4 == 0) // Don't spam the log with waiting messages
                log.debug("Waiting for the grading process to finish...");
            Thread.sleep(1000);
            state = dockerClient.inspectContainerCmd(containerId).exec().getState();
        }
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
        log.info("Fetching response file: {}", respFilePath);
        InputStream resp = null;
        try {
            resp = DockerController.fetchResponse(dockerClient, containerId,
                respFilePath.toString());
        } catch (com.github.dockerjava.api.exception.NotFoundException e) {
            log.info("Response file '{}' does not exist.", respFilePath);
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
            if (null != resp) {
                byte[] respBytes = IOUtils.toByteArray(resp);
                proformaResponse = new ProformaResponse(respBytes, MimeType.XML);
            } else
                throw new FileNotFoundException(String.format("Neither '%s' nor '%s' could be " +
                    "retrieved from the container.", responseZipPath, responseXmlPath));
        }

        if (null == resp) // try for the other one
            resp = tryFetchResponseFile(dockerClient, containerId, responseZipPath);
        if (null != resp) {
            byte[] respBytes = IOUtils.toByteArray(resp);
        }
        return proformaResponse;
    }
}