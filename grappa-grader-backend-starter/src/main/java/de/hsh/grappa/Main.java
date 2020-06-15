package de.hsh.grappa;

import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaSubmission;
import de.hsh.grappa.utils.BackendPluginLoadingHelper;
import de.hsh.grappa.utils.ClassLoaderHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String CONFIG_CLASS_PATHES = "grappa.plugin.grader.classpathes";
    private static final String CONFIG_CLASS_NAME = "grappa.plugin.grader.class";
    private static final String CONFIG_GRADER_CONFIG_PATH = "grappa.plugin.grader.config";
    private static final String CONFIG_FILE_EXTENSIONS = "grappa.plugin.grader.fileextensions";
    private static final String CONFIG_FILE_NAME = "grappa-grader-backend-starter.properties";
    private static final String TMP_INPUT_PATH = "/var/grb_starter/tmp";
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
                log.info("Config file loaded.");
            } catch(Exception e) {
                log.error("Failed to load config file for backend starter: {}", configPath);
                throw e;
            }

            BackendPlugin bp = null;
            try {
                String classPath = bpStarterConfig.getProperty(CONFIG_CLASS_PATHES);
                String className = bpStarterConfig.getProperty(CONFIG_CLASS_NAME);
                log.info("Loading backend plugin '{}'...", classPath);
                bp = loadBackendPlugin(classPath, className);
                //BackendPlugin bp = loadBackendPluginAlt(bpStarterConfig);
                log.info("{} loaded.", bp);
            } catch (Exception e) {
                log.error("Failed to load backend plugin.");
                log.error(ExceptionUtils.getStackTrace(e));
                System.exit(-1);
            }

            ProformaSubmission proformaSubmission = null;
            try {
                log.info("Loading submission file...");
                proformaSubmission = loadProformaSubmission();
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

            ProformaResponse proformaResponse = null;
            try {
                log.info("Initializing grader backend...");
                bp.init(graderConfig);
                log.info("Starting grading process...");
                proformaResponse = bp.grade(proformaSubmission);
                log.info("Grading finished.");
            } catch (Exception e) {
                log.error("Grading process failed.");
                log.error(ExceptionUtils.getStackTrace(e));
                System.exit(-1);
            }

            try {
                Path responseFilePath = null;
                if(proformaResponse.getMimeType().equals(MimeType.XML))
                    responseFilePath = Paths.get(RESULT_RESPONSE_FILE_PATH_WITHOUT_EXTENSION + ".xml");
                else
                    responseFilePath = Paths.get(RESULT_RESPONSE_FILE_PATH_WITHOUT_EXTENSION + ".zip");
                log.info("Writing response file to: {}", responseFilePath);
                try (OutputStream outputStream = new FileOutputStream(responseFilePath.toString());
                    ByteArrayInputStream responseStream = new ByteArrayInputStream(proformaResponse.getContent())) {
                    IOUtils.copy(responseStream, outputStream);
                }
                log.info("Grading backend starter finished successfully.");
            } catch (Exception e) {

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            // status indicates that the grading starter finished abnormally
            System.exit(-1);
        }
    }

    private static ProformaSubmission loadProformaSubmission() throws Exception {
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
        log.info("Loading submission file: {}", submToLoadPath);
        byte[] submBytes = Files.readAllBytes(submToLoadPath);
        return new ProformaSubmission(submBytes, mimeType);
    }

    private static BackendPlugin loadBackendPluginAlt(Properties props) throws Exception {
        return BackendPluginLoadingHelper.loadGraderPlugin(props, log);
    }

    private static BackendPlugin loadBackendPlugin(String jarPath, String classPath) throws Exception {
        return new ClassLoaderHelper<BackendPlugin>().LoadClass(jarPath, classPath, BackendPlugin.class);
    }
}
