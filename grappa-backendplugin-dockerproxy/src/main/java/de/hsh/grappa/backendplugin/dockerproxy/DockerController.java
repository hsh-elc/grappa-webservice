package de.hsh.grappa.backendplugin.dockerproxy;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import de.hsh.grappa.util.Tar;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A convenient wrapper for the com.github.dockerjava API.
 */
class DockerController { // TODO: refactor to ctor(dockerClient, containerId)
    public static void copyFile(byte[] fileBytes, String destinationDirPath, String destinationFileName,
                                DockerClient client, String containerId, boolean overwrite) throws Exception {
        byte[] tarBytes = Tar.tar(fileBytes, destinationFileName);
        try (InputStream is = new ByteArrayInputStream(tarBytes)) {
            client.copyArchiveToContainerCmd(containerId)
                .withTarInputStream(is)
                .withRemotePath(destinationDirPath)
                .withNoOverwriteDirNonDir(!overwrite)
                .exec();
        }
    }

    /**
     * Create a docker container with the given parameters.
     *
     * @param client
     * @param imageId
     * @param environment
     * @param network The network in which the container should be started.
     *                Can be null if the container should not be started in a specific docker network.
     *                The docker network will be automatically created if it does not exist already.
     * @return
     * @throws Exception
     */
    public static String createContainer(DockerClient client, String imageId, List<String> environment, String network) throws Exception {
        CreateContainerCmd cmd = client.createContainerCmd(imageId).withEnv(environment);

        if (network != null && !network.equals("")) {
            Collection<Network> networks = client.listNetworksCmd().withNameFilter(network).exec();
            if (networks.isEmpty()) { //Network does not exist, create it
                client.createNetworkCmd().withName(network).exec();
            }
            cmd = cmd.withHostConfig(HostConfig.newHostConfig().withNetworkMode(network));
        }

        return cmd.exec().getId();
    }

    public static void startContainer(DockerClient client, String containerId) throws Exception {
        client.startContainerCmd(containerId).exec();
    }

    public static void stopContainer(DockerClient client, String containerId) throws Exception {
        client.stopContainerCmd(containerId).exec();
    }

    public static void removeContainer(DockerClient client, String containerId, boolean forceRemove) throws Exception {
        client.removeContainerCmd(containerId).withForce(forceRemove).exec();
    }

    public static InputStream fetchFile(DockerClient client, String containerId,
                                        String containerFilePath) throws Exception {
        // Copy file from container
        try (InputStream input = client.copyArchiveFromContainerCmd(containerId, containerFilePath).exec()) {
            // https://github.com/docker-java/docker-java/issues/991#issuecomment-366185304
            // DockerClient's copyArchiveFromContainerCmd wraps the file within
            // a tar archive. Either way, we can expect just a single tar entry within this
            // tar archive.
            byte[] b = Tar.untarSingleFile(input);
            return new ByteArrayInputStream(b);
        }
    }


    public static List<String> getContainerLog(DockerClient client, String containerId) throws Exception {
        List<String> logs = new ArrayList<>();
        LogContainerCmd logContainerCmd = client.logContainerCmd(containerId);
        logContainerCmd.withStdOut(true).withStdErr(true);
        logContainerCmd.exec(new ResultCallback.Adapter<>() {
            @Override
            public void onNext(Frame item) {
                logs.add(item.toString());
            }
        }).awaitCompletion(3000, TimeUnit.MILLISECONDS);
        return logs;
    }

//    public static long getContainerExitCode(DockerClient client, String containerId) throws Exception {
//        InspectContainerResponse.ContainerState state = client.inspectContainerCmd(containerId).exec().getState();
//        return state.getExitCodeLong();
//    }
}
