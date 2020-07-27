package de.hsh.grappa;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.LogContainerCmd;
import com.github.dockerjava.api.model.Frame;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DockerController { // TODO: refactor to ctor(dockerClient, containerId)
    public static void copyFile(byte[] fileBytes, String destinationDirPath, String destinationFileName,
                                DockerClient client, String containerId, boolean overwrite) throws Exception {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             TarArchiveOutputStream tar = new TarArchiveOutputStream(bos)) {
            TarArchiveEntry entry = new TarArchiveEntry(destinationFileName);
            entry.setSize(fileBytes.length);
            entry.setMode(0700);
            tar.putArchiveEntry(entry);
            tar.write(fileBytes);
            tar.closeArchiveEntry();
            tar.close();
            try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
                client.copyArchiveToContainerCmd(containerId)
                    .withTarInputStream(is)
                    .withRemotePath(destinationDirPath)
                    .withNoOverwriteDirNonDir(!overwrite)
                    .exec();
            }
        }
    }

    public static String createContainer(DockerClient client, String imageId) throws Exception {
        String id = client.createContainerCmd(imageId).exec().getId();
        return id;
    }

    public static void startContainer(DockerClient client, String containerId) throws Exception {
        client.startContainerCmd(containerId).exec();
    }

    public static void stopContainer(DockerClient client, String containerId) throws Exception {
        client.stopContainerCmd(containerId).exec();
    }

    public static void removeContainer(DockerClient client, String containerId) throws Exception {
        client.removeContainerCmd(containerId).exec();
    }

    public static InputStream fetchFile(DockerClient client, String containerId,
                                        String containerFilePath) throws Exception {
        // Copy file from container
        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(
            client.copyArchiveFromContainerCmd(containerId, containerFilePath).exec())) {

            // https://github.com/docker-java/docker-java/issues/991#issuecomment-366185304
            // DockerClient's copyArchiveFromContainerCmd wraps the file within
            // a tar archive. Either way, we can expect just a single tar entry within this
            // tar archive.
            TarArchiveEntry tarEntry = tarStream.getNextTarEntry();
//            if (null != tarEntry) {
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    IOUtils.copy(tarStream, baos);
                    return new ByteArrayInputStream(baos.toByteArray());
//                }
//            } else {
//                throw new Exception("file has no content.");
            }
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
        }).awaitCompletion(1500, TimeUnit.MILLISECONDS);
        return logs;
    }

//    public static long getContainerExitCode(DockerClient client, String containerId) throws Exception {
//        InspectContainerResponse.ContainerState state = client.inspectContainerCmd(containerId).exec().getState();
//        return state.getExitCodeLong();
//    }
}
