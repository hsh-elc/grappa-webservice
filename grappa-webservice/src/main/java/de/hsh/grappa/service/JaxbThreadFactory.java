package de.hsh.grappa.service;

import java.util.concurrent.ThreadFactory;


// Context, reason, and source of this class:
// https://stackoverflow.com/questions/51518781/jaxb-not-available-on-tomcat-9-and-java-9-10
// modified to ThreadFactory
public class JaxbThreadFactory implements ThreadFactory {
    private final ClassLoader classLoader;

    public JaxbThreadFactory() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setContextClassLoader(classLoader);
        return thread;
    }
}
