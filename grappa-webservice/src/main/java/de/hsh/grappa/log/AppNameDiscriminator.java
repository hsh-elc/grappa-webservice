package de.hsh.grappa.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.sift.Discriminator;
import de.hsh.grappa.application.GrappaServlet;

public class AppNameDiscriminator implements Discriminator<ILoggingEvent> {
    private static final String KEY = "appName";
    private boolean started;

    @Override
    public String getDiscriminatingValue(ILoggingEvent iLoggingEvent) {
        return GrappaServlet.getGrappaInstanceName();
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
