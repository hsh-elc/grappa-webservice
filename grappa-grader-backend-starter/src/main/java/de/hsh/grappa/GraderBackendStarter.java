package de.hsh.grappa;

import de.hsh.grappa.plugin.BackendPlugin;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.utils.BackendPluginLoadingHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This class bootstraps and runs a BackendPlugin within a Docker
 * container. It's basically the second player to the
 * grappa-backend-plugin-docker-proxy module.
 *
 * The Docker Proxy module deposits the submission resource file
 * within the Docker container in a specific directory and starts
 * the GraderBackendStarter, which in turn loads up the actual
 * grader BackendPlugin and passes that submission resource file
 * on to the plugin.
 *
 * Once the grading process has finished one way or another, the
 * GraderBackendStarter stores the result in a specific directory
 * for the Docker Proxy module to retrieve.
 * In case of a grading success, it'll be the resulting Proforma
 * response resource file. In case of failure, it'll be the grader's
 * error stack traces caught by the grader BackendPlugin, if any.
 */
public class GraderBackendStarter {
    private static final Logger log = LoggerFactory.getLogger(GraderBackendStarter.class);

    private static final String CONFIG_CLASS_PATHES = "grappa.plugin.grader.classpathes";
    private static final String CONFIG_CLASS_NAME = "grappa.plugin.grader.class";
    private static final String CONFIG_GRADER_CONFIG_PATH = "grappa.plugin.grader.config";
    private static final String CONFIG_FILE_EXTENSIONS = "grappa.plugin.grader.fileextensions";
    private static final String CONFIG_FILE_NAME = "grappa-grader-backend-starter.properties";
    private static final String TMP_INPUT_PATH = "/var/grb_starter/tmp";
//    private static final String GRADER_EXCEPTION_MESSAGE_FILE_PATH =
//        TMP_INPUT_PATH.concat("/grader_exception_message");
    private static final String GRADER_EXCEPTION_STACKTRACE_FILE_PATH =
        TMP_INPUT_PATH.concat("/grader_exception_stacktrace");
    private static final String RESULT_RESPONSE_FILE_PATH_WITHOUT_EXTENSION = "/var/grb_starter/tmp/response";

    private static final String WORKING_DIR_PATH = "/opt/grader/starter";

    public static void main(String[] args) {
        try {
            //new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            Path configPath = Paths.get(WORKING_DIR_PATH, CONFIG_FILE_NAME);
            Properties bpStarterConfig = new Properties();

            log.info("Loading config file: {}", configPath);
            try (InputStream input = new FileInputStream(configPath.toFile().getAbsolutePath())) {
                bpStarterConfig.load(input);
            } catch(Exception e) {
                log.error("Failed to load config file for backend starter: {}", configPath);
                throw e;
            }

            BackendPlugin bp = null;
            try {
//                String classPathes = bpStarterConfig.getProperty(CONFIG_CLASS_PATHES);
//                String fileExts = bpStarterConfig.getProperty(CONFIG_FILE_EXTENSIONS);
//                String className = bpStarterConfig.getProperty(CONFIG_CLASS_NAME);
//
//                String[] classpathParts = classPathes.split(";");
//                String[] extensionParts = fileExts.split(";");
//
//                ImprovedClassLoader<BackendPlugin> classLoader = new ImprovedClassLoader<>(classpathParts,
//                    extensionParts);
//
//                log.info("Loading backend plugin class pathes '{}'...", classPathes);
//                bp = classLoader.instantiateClass(className, BackendPlugin.class);

                BackendPluginLoadingHelper.loadClasspathLibsFromProperties(bpStarterConfig);
                bp = BackendPluginLoadingHelper.loadGraderPluginFromProperties(bpStarterConfig);
            } catch (Exception e) {
                log.error("Failed to load backend plugin.");
                log.error(ExceptionUtils.getStackTrace(e));
                System.exit(-1);
            }

            SubmissionResource submissionResource = null;
            try {
                log.info("Loading submission file...");
                submissionResource = loadProformaSubmission();
            } catch (Exception e) {
                log.error("Failed to load submission file.");
                log.error(ExceptionUtils.getStackTrace(e));
                System.exit(-1);
            }

            String graderConfigPath = bpStarterConfig.getProperty(CONFIG_GRADER_CONFIG_PATH);
            Properties graderConfig = new Properties();
            try (InputStream input = new FileInputStream(graderConfigPath)) {
                graderConfig.load(input);
            } catch(Exception e) {
                log.error("Failed to load grader config file: {}", graderConfigPath);
                log.error(ExceptionUtils.getStackTrace(e));
                System.exit(-1);
            }

            ResponseResource responseResource = null;
            try {
                log.info("Initializing grader backend...");
                bp.init(graderConfig);
                log.info("Starting grading process...");
                responseResource = bp.grade(submissionResource);
                log.info("Grading finished.");
            } catch (Exception e) {
                log.error("Grading process failed with the following message and stacktrace:");
                log.error(e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));

                try(FileOutputStream fos = new FileOutputStream(GRADER_EXCEPTION_STACKTRACE_FILE_PATH);
                    ByteArrayInputStream baos = new ByteArrayInputStream
                        (ExceptionUtils.getStackTrace(e).getBytes())) {
                    IOUtils.copy(baos, fos);
                } catch (Exception e2) {
                    log.error(e2.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e2));
                }

                System.exit(-1);
            }

            try {
                Path responseFilePath = null;
                if(responseResource.getMimeType().equals(MimeType.XML))
                    responseFilePath = Paths.get(RESULT_RESPONSE_FILE_PATH_WITHOUT_EXTENSION + ".xml");
                else
                    responseFilePath = Paths.get(RESULT_RESPONSE_FILE_PATH_WITHOUT_EXTENSION + ".zip");
                log.info("Writing response file to: {}", responseFilePath);
                try (OutputStream outputStream = new FileOutputStream(responseFilePath.toString());
                    ByteArrayInputStream responseStream = new ByteArrayInputStream(responseResource.getContent())) {
                    IOUtils.copy(responseStream, outputStream);
                }
                log.info("Grading backend starter finished successfully.");
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        } catch (Exception e) {
            System.err.println(ExceptionUtils.getStackTrace(e));
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            // status indicates that the grading starter finished abnormally
            System.exit(-1);
        }
    }

    private static SubmissionResource loadProformaSubmission() throws Exception {
        Path submZipPath = Paths.get(TMP_INPUT_PATH, "submission.zip");
        Path submXmlPath = Paths.get(TMP_INPUT_PATH, "submission.xml");
        Path submToLoadPath = null;
        MimeType mimeType = null;
        if(Files.exists(submZipPath) && Files.isRegularFile(submZipPath)) {
            submToLoadPath = submZipPath;
            mimeType = MimeType.ZIP;
        } else if (Files.exists(submXmlPath) && Files.isRegularFile(submXmlPath)) {
            submToLoadPath = submXmlPath;
            mimeType = MimeType.XML;
        } else {
            throw new FileNotFoundException(String.format("Neither '%s' nor '%s' exist.",
                submZipPath, submZipPath));
        }
        byte[] submBytes = Files.readAllBytes(submToLoadPath);
        return new SubmissionResource(submBytes, mimeType);
    }

//    private static BackendPlugin loadBackendPluginAlt(Properties props) throws Exception {
//        return BackendPluginLoadingHelper.loadGraderPlugin(props/*, log*/);
//    }

//    private static BackendPlugin loadBackendPlugin(String jarPath, String fileExt, String classPath) throws Exception {
//        return new ClassLoaderHelper<BackendPlugin>().LoadClass(jarPath, classPath, BackendPlugin.class);
//    }
}
