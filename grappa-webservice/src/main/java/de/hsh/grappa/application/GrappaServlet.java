package de.hsh.grappa.application;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.GrappaConfig;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.service.GradingEnvironmentSetup;
import de.hsh.grappa.util.ClassLoaderHelper;
import proforma.util.ProformaVersion;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class GrappaServlet implements ServletContextListener {
    public static GrappaConfig CONFIG;

    private static final Logger log = LoggerFactory.getLogger(GrappaServlet.class);
    public static final String CONFIG_FILENAME_PATH = "/etc/grappa/grappa-config.yaml";

    private Thread graderPoolManagerThread;

    private static String grappaInstanceName = "grappa-webservice";

    @Override
    public void contextInitialized(ServletContextEvent ctxEvent) {
        try {
            try {
                // This will supposedly throw a NullPointerException when the
                // container is configured to expand the war file in memory
                // instead of in disk.
                // If that happens, grappa won't be able to distinguish between
                // multiple running grappa instances that are configured to run
                // with different grader jars.
                grappaInstanceName = new File(ctxEvent.getServletContext()
                    .getContextPath()).getName();
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }
            log.info("Running grappa web service instance: '{}'.", grappaInstanceName);
            readConfigFile();
            ProformaVersion.getDefaultVersionNumber(); // fail fast, if the required grappa-proforma-N-M.jar is missing
            setupRedisConnection();
            //loadGradingEnvironmentSetups();
            GraderPoolManager.getInstance().init(CONFIG.getGraders());
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
            setLoggingLevel(Level.toLevel(CONFIG.getService().getLogging_level()));
            log.info("Config file loaded: {}", CONFIG.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void setupRedisConnection() throws Exception {
        //redis = new RedisController(CONFIG.getCache());
        //redis.init();
        RedisController.getInstance().init(CONFIG.getCache());
        log.info("Testing redis connection...");
        if (RedisController.getInstance().ping()) {
            log.info("Redis connection established");
        } else {
            log.error("Redis connection could not be established.");
            // TODO: System.exit(-1) shut down service
            // but then again, the connection could be established at
            // a later time... like when someone does systemctl start redis
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

    /**
     * @return the name of the grappa instance depending on what grader this
     * instance is running with.
     */
    public static String getGrappaInstanceName() {
        return grappaInstanceName;
    }
}