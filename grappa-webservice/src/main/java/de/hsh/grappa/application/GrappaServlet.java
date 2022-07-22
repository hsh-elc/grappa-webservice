package de.hsh.grappa.application;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.GrappaConfig;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.service.GradingEnvironmentSetup;
import de.hsh.grappa.util.ClassLoaderHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proforma.util.ProformaVersion;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.util.Properties;
import java.util.jar.Manifest;

@WebListener
public class GrappaServlet implements ServletContextListener {
    public static GrappaConfig CONFIG;

    private static final Logger log = LoggerFactory.getLogger(GrappaServlet.class);
    public static final String CONFIG_FILENAME_PATH = "/etc/grappa/grappa-config.yaml";

    private Thread graderPoolManagerThread;

    @Override
    public void contextInitialized(ServletContextEvent ctxEvent) {
        try {
            log.info("Running {} version {}.", getGrappaInstanceName(), getImplVersion(ctxEvent));
            readConfigFile();
            ProformaVersion.getDefaultVersionNumber(); // fail fast, if the required grappa-proforma-N-M.jar is missing
            setupRedisConnection();
            //loadGradingEnvironmentSetups();
            GraderPoolManager.getInstance().init(CONFIG.getGraders());
            // set logging level after the initial and interesting infos have been logged
            setLoggingLevel(Level.toLevel(CONFIG.getService().getLogging_level()));
            graderPoolManagerThread = new Thread(GraderPoolManager.getInstance());
            graderPoolManagerThread.start();
        } catch (Exception e) {
            log.error("Error during webservice initialization.");
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            //throw e; // make the webservice shutdown
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent ctxEvent) {
        try {
            GraderPoolManager.getInstance().shutdown();
            graderPoolManagerThread.interrupt();
            RedisController.getInstance().shutdown();
        } catch (Exception e) {
            log.error("Error during webservice deinitialization.");
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void setLoggingLevel(Level level) {
        log.info("Setting logging level to {}", level);
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger("de.hsh.grappa");
        root.setLevel(level);
        root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory
            .getLogger("root");
        root.setLevel(level);
    }

    private void readConfigFile() {
        log.info("Loading config file '{}'...", CONFIG_FILENAME_PATH);
        try {
            var mapper = new ObjectMapper(new YAMLFactory());
            var configFile = new File(CONFIG_FILENAME_PATH);
            CONFIG = mapper.readValue(configFile, GrappaConfig.class);
            CONFIG.propagateLoggingLevelToGraders();
            log.info("Config file loaded: {}", CONFIG.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void setupRedisConnection() throws Exception {
        RedisController.getInstance().init(CONFIG.getCache());
        log.info("Testing redis connection...");
        if (RedisController.getInstance().ping()) {
            log.info("Redis connection established");
        } else {
            log.error("Redis connection could not be established. Is the service down?");
        }
    }

    private void loadGradingEnvironmentSetups() {
        GradingEnvironmentSetup grdEnv = null;
        try {
            grdEnv =
                new ClassLoaderHelper<GradingEnvironmentSetup>().LoadClass(CONFIG.getService().getDefault_grading_environment_setup_class_path(), CONFIG.getService().getDefault_grading_environment_setup_class_name(), GradingEnvironmentSetup.class);
        } catch (Exception e) {
            log.error("Failed to load jar file");
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

//        grdEnv.init(null);
//        try {
//            grdEnv.setup();
//        } catch(Exception e) {
//          log.error(e.getMessage());
//          log.error(ExceptionUtils.getStackTrace(e));
//        }
    }

    public static String getGrappaInstanceName() {
        return "grappa-webservice-2";
    }

    public static String getImplVersion(ServletContextEvent e) {
        String version  = GrappaServlet.class.getPackage().getImplementationVersion();
        if (null == version) {
            Properties prop = new Properties();
            try {
                // note that if the webapp is deployed as an exploded war (eg during development in IDE),
                // the manifest.mf file is not generated, hence the implementation version will be missing
                // this generally not a problem though
                Manifest m = new Manifest(e.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
                var attr = m.getAttributes("de.hsh.grappa");
                version = attr.getValue("Implementation-Version");
            } catch (Exception ex) {
                log.warn("Could not read Implementation-Version from MANIFEST.MF file, service is probably running " +
                    "as exploded war");
                if(!(ex instanceof NullPointerException))
                    log.warn(ex.getMessage());
                version = "N/A";
            }
        }
        return version;
    }
}