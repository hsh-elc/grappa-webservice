package de.hsh.grappa.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;

import de.hsh.grappa.plugins.backendplugin.BackendPlugin;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;




public class BackendPluginLoadingHelper {
	
	public static void loadClasspathLibs(Properties config , Logger logger) throws Exception{
		String classpathes = config
                .getProperty("grappa.plugin.grader.classpathes");
        String[] classpathParts = classpathes.split(";");
        logger.debug("Current Classpathes: {}", classpathes);
        String extensions = config
                .getProperty("grappa.plugin.grader.fileextensions");
        String[] extensionsParts = extensions.split(";");
        logger.debug("Current extensions: {}", extensions);
        @SuppressWarnings("unused")
        DirectoryClassloader dc = new DirectoryClassloader(classpathParts, extensionsParts);
	}
	
    public static BackendPlugin loadGraderPlugin(Properties config , Logger logger) throws Exception {
    	BackendPlugin graderPlugin = null;
    	
        Properties backendConf = new Properties();
        String graderConfPath = config
                .getProperty("grappa.plugin.grader.config");
        if (graderConfPath != null && !graderConfPath.isEmpty()) {

            try {
                backendConf.load(new FileInputStream(graderConfPath));
            } catch (IOException e2) {
                logger.error("Error while loading grader config file: {}",
                        graderConfPath);
                logger.error(e2.getMessage());
                logger.error(ExceptionUtils.getStackTrace(e2));
                throw e2;
            }
        } else {
            logger.warn("Grader config file not configured.");
        }

// find BackendPlugin
        Class<?> clazz = null;
        ClassLoader contextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        clazz = contextClassLoader.loadClass(config
            .getProperty("grappa.plugin.grader.class"));
        //graderPlugin = (BackendPlugin) clazz.newInstance();
        Class<? extends BackendPlugin> newClass = clazz.asSubclass(BackendPlugin.class);
        Constructor<? extends BackendPlugin> constructor = newClass.getConstructor();
        graderPlugin = (BackendPlugin) constructor.newInstance();

        if (!backendConf.isEmpty()) {
            try {
            	graderPlugin.init(backendConf);
            } catch (Exception e) {
                logger.error(
                        "Error while initializing the grader plugin with config file: {}",
                        graderConfPath);
                logger.error(e.getMessage());
                logger.error(ExceptionUtils.getStackTrace(e));
                throw e;
            }
        }

        logger.info("Grader plugin loaded successfully");
        
        return graderPlugin;
    }
}
