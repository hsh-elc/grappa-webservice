package de.hsh.grappa;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaSubmission;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.Properties;

public class Main {

//    public static void main(String[] args) {
//        //Path p = Paths.get("/opt/grader/bootstrap_grader-backend.sh");
//        Path p = Paths.get("a", "b");
//
//        System.out.println(p.toAbsolutePath());
//    }

    public static void main(String[] args) {
//        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
//            .withDockerHost("tcp://172.17.93.197:2375")
//            //.withDockerHost("tcp://localhost:2375")
//            .withDockerTlsVerify(false)//(true)
//            //.withDockerCertPath("/home/user/.docker")
////            .withRegistryUsername(registryUser)
////            .withRegistryPassword(registryPass)
////            .withRegistryEmail(registryMail)
////            .withRegistryUrl(registryUrl)
//            .build();
//
//
//        final DockerCmdExecFactory dockerCmdExecFactory = new JerseyDockerCmdExecFactory();
//        DockerClient dockerClient = DockerClientBuilder.getInstance(config)
//            .withDockerCmdExecFactory(dockerCmdExecFactory)
//            .build();
////        // Check if client was successfully created
//        final AuthResponse response = dockerClient.authCmd().exec();
//        if (!response.getStatus().equalsIgnoreCase("Login Succeeded")) {
//            System.out.println("Could not create DockerClient");
//        }

        //File lol = new File("C:\\tmp\\lol.zip");
        File submFile = new File("C:\\data_utc-bn9-u1\\Documents-Local\\Grappa\\Roberts ProFormA 2.0 " +
            "Aufgaben\\submission-separate.zip");

        byte[] submBytes = null;
        try {
            submBytes = Files.readAllBytes(submFile.toPath());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Properties props = new Properties();
        try (FileInputStream fs = new FileInputStream("C:/etc/grappa/docker-proxy-backend-plugin.properties")) {
            props.load(fs);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        ProformaSubmission proformaSubmission = new ProformaSubmission(submBytes, MimeType.ZIP);
        ProformaResponse proformaResponse = null;
        try {
            var bp = new DockerProxyBackendPlugin();
            bp.init(props);
            proformaResponse = bp.grade(proformaSubmission);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Grader supplied response: " + proformaResponse);

//        try {
//            dockerClient.close();
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }

//        try //(InputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(lol)) {
//            (
//             ByteArrayOutputStream bos =
//            new ByteArrayOutputStream();
//             TarArchiveOutputStream tar = new TarArchiveOutputStream(bos)) {
//
//            byte[] lolBytes = FileUtils.readFileToByteArray(lol);
//            TarArchiveEntry entry = new TarArchiveEntry("subm111.zip");
//            entry.setSize(lolBytes.length);
//            entry.setMode(0700);
//            tar.putArchiveEntry(entry);
//            tar.write(lolBytes);
//            tar.closeArchiveEntry();
//            tar.close();
//            try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
//                dockerClient.copyArchiveToContainerCmd(containerId)
//                    .withTarInputStream(is)
//                    .withRemotePath("/opt/grappa")
//                    .withNoOverwriteDirNonDir(false)
//                    .exec();
//            }
//        } catch (Exception e) {
//            //log.error(e.getMessage());
//            System.out.println(e.getMessage());
//        }
    }


}
