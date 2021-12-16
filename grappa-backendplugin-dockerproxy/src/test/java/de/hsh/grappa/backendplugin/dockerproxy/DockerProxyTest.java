package de.hsh.grappa.backendplugin.dockerproxy;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;

import de.hsh.grappa.util.FileUtils;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;

import org.junit.Test;

import java.io.File;

public class DockerProxyTest {
    private final String submissionFilePath = "C:/Users/nudroid/Desktop/Grappa/tmp/subm.zip";

    @Test
    public void testCopySubmToContainer() throws Exception {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://192.168.1.57:2376")
            .withDockerTlsVerify(false)
            .build();

        JerseyDockerHttpClient httpClient = new JerseyDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();

        try (DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient)) {

            dockerClient.pingCmd().exec();

            byte[] b = FileUtils.readFileToByteArray(new File(submissionFilePath));
            System.out.println("Actual byte length: " + b.length);
            SubmissionResource submission = new SubmissionResource(b, MimeType.ZIP);
            copySubmissionToContainer(dockerClient, "/var/grb_starter/tmp", b);
        }
    }

    private void copySubmissionToContainer(DockerClient dockerClient, String containerId,
                                           byte[] submission) throws Exception {
        DockerController.copyFile(submission, containerId,
            "testSubmission.zip", dockerClient, "eager_lumiere", true);
    }

}
