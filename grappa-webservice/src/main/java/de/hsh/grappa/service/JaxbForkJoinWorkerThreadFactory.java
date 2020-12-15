package de.hsh.grappa.service;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

// Context, reason, and source of this class:
// https://stackoverflow.com/questions/51518781/jaxb-not-available-on-tomcat-9-and-java-9-10
public class JaxbForkJoinWorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
    private final ClassLoader classLoader;

    public JaxbForkJoinWorkerThreadFactory() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Override
    public final ForkJoinWorkerThread newThread(ForkJoinPool pool) {
        ForkJoinWorkerThread thread = new JaxbForkJoinWorkerThread(pool);
        thread.setContextClassLoader(classLoader);
        return thread;
    }

    private static class JaxbForkJoinWorkerThread extends ForkJoinWorkerThread {
        private JaxbForkJoinWorkerThread(ForkJoinPool pool) {
            super(pool);
        }
    }
}
