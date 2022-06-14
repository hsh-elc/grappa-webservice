package de.hsh.grappa.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public class LogbackHelper {
    public static Level getEffectiveLogLevel(String packageName) {
        return getLogger(packageName).getEffectiveLevel();
    }

    public static void setLogLevel(String packageName, Level logLevel) {
        getLogger(packageName).setLevel(logLevel);
    }

    public static void setLogLevel(String packageName, String logLevel) {
        setLogLevel(packageName, Level.toLevel(logLevel));
    }

    public static Logger getLogger(String packageName) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        return loggerContext.getLogger(packageName);
    }
}
