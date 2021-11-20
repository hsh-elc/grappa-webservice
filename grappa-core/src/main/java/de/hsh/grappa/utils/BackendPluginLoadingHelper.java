package de.hsh.grappa.utils;

import de.hsh.grappa.plugin.BackendPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BackendPluginLoadingHelper {
    private static final String CONFIG_CLASS_PATHES = "grappa.plugin.grader.classpathes";
    private static final String CONFIG_CLASS_NAME = "grappa.plugin.grader.class";
    private static final String CONFIG_GRADER_CONFIG_PATH = "grappa.plugin.grader.config";
    private static final String CONFIG_FILE_EXTENSIONS = "grappa.plugin.grader.fileextensions";
    private static final Logger log = LoggerFactory.getLogger(BackendPluginLoadingHelper.class);
    private static Map<String, ClassPathClassLoader<BackendPlugin>> classLoader= new HashMap<>();
    private static final String DEFAULT_GRADER_KEY = "defaultGraderKey";
    
    /**
     * This method is only meant to be called from the BackendPluginStarter, where there are no
     * potentially conflicting classes from different graders.
     * @param config
     * @throws Exception
     */
    public static void loadClasspathLibsFromProperties(Properties config) throws Exception{
        String classpathes = config.getProperty(CONFIG_CLASS_PATHES);
        String extensions = config.getProperty(CONFIG_FILE_EXTENSIONS);
        loadClasspathLibs(DEFAULT_GRADER_KEY, classpathes, extensions);
    }

    public static void loadClasspathLibs(String graderKey, String classpath, String fileExtensions) throws Exception {
        log.debug("Current classpathes: {}", classpath);
        String[] classpathParts = classpath.split(";");
        log.debug("Current extensions: {}", fileExtensions);
        String[] extensionsParts = fileExtensions.split(";");
        synchronized (classLoader) {
            ClassPathClassLoader<BackendPlugin> cl= new ClassPathClassLoader<>(classpathParts, extensionsParts);
            classLoader.put(graderKey, cl);
        }
    }

    /**
     * This method is only meant to be called from the BackendPluginStarter, where there are no
     * potentially conflicting classes from different graders.
     * @param config
     * @return
     * @throws Exception
     */
    public static BackendPlugin loadBackendPluginFromPropertiesAndInit(Properties config) throws Exception {
        String className = config.getProperty(CONFIG_CLASS_NAME);
        String graderConfPath = config.getProperty(CONFIG_GRADER_CONFIG_PATH);
        BackendPlugin bp = loadBackendPlugin(DEFAULT_GRADER_KEY, className);
        Properties bpConfig = loadBackendPluginConfig(graderConfPath);
        bp.init(bpConfig);
        return bp;
    }

    public static Properties loadBackendPluginConfig(String backendPluginConfigPath) throws Exception {
        Properties pluginConfig = new Properties();
        if (backendPluginConfigPath != null && !backendPluginConfigPath.isEmpty()) {
            try (InputStream is = new FileInputStream(new File(backendPluginConfigPath))) {
                pluginConfig.load(is);
            } catch (IOException e) {
                log.error("Error while loading grader config file: {}", backendPluginConfigPath);
                throw e;
            }
        } else {
            log.warn("BackendPlugin config file is not configured.");
        }
        return pluginConfig;
    }

    public static BackendPlugin loadBackendPlugin(String graderKey, String className) throws Exception {
        ClassPathClassLoader<BackendPlugin> cl= classLoader.get(graderKey);
        if (cl == null) {
            log.error("Class loader of backend plugin '{}' not found", graderKey);
            throw new Exception("Class loader of backend plugin '" + graderKey + "' not found");
        }
        BackendPlugin bp = (BackendPlugin) cl.instantiateClass(className, BackendPlugin.class);
        log.info("Grader plugin loaded successfully.");
        return bp;
    }

//    public static BackendPlugin loadGraderPlugin(Properties config) throws Exception {
//        BackendPlugin graderPlugin = null;
//
//        Properties backendConf = new Properties();
//        String graderConfPath = config.getProperty(CONFIG_GRADER_CONFIG_PATH);
//        if (graderConfPath != null && !graderConfPath.isEmpty()) {
//            try {
//                backendConf.load(new FileInputStream(graderConfPath));
//            } catch (IOException e) {
//                log.error("Error while loading grader config file: {}", graderConfPath);
//                log.error(e.getMessage());
//                log.error(ExceptionUtils.getStackTrace(e));
//                throw e;
//            }
//        } else {
//            log.warn("BackendPlugin config file is not configured.");
//        }
//
//        String className = config.getProperty(CONFIG_CLASS_NAME);
//        graderPlugin = (BackendPlugin) classLoader.instantiateClass(className, BackendPlugin.class);
//
//        // tmp comment: do this as a separate commit: call graderPlugin.init even if no config file
//        // exists or if its empty
//        if (backendConf == null) {
//            // It does not matter if the BackendPlugin's config file doesn't exist, or even if it's empty.
//            // BackendPlugin.init() still needs to be called.
//            log.info("BackendPlugin config file does not exist... initializing BackendPlugin with an " +
//                "empty config.");
//            backendConf = new Properties();
//        }
//
//        try {
//            graderPlugin.init(backendConf);
//        } catch (Exception e) {
//            log.error("Error while initializing the grader plugin with config file: {}", graderConfPath);
//            log.error(e.getMessage());
//            log.error(ExceptionUtils.getStackTrace(e));
//            throw e;
//        }
//
//        log.info("Grader plugin loaded successfully.");
//        return graderPlugin;
//    }
}
