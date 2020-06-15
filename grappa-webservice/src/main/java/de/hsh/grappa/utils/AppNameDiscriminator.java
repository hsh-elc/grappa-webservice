package de.hsh.grappa.utils;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;

public class AppNameDiscriminator implements Discriminator<ILoggingEvent> {
    private static final String KEY = "appName";
    private boolean started;

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        // TODO return GrappaServlet.getGrappaInstanceName();
        return "";
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
}
