package de.hsh.grappa.backendplugin.dockerproxy;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import de.hsh.grappa.util.FileUtils;
import org.junit.Test;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;

import java.io.File;

public class DockerProxyTest {
    private final String submissionFilePath = "path/to/submission";

    @Test
    public void testCopySubmToContainer() throws Exception {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("tcp://192.168.1.57:2376")
            .withDockerTlsVerify(false)
            .build();

        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
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
