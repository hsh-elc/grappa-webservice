package de.hsh.grappa.service;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

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
