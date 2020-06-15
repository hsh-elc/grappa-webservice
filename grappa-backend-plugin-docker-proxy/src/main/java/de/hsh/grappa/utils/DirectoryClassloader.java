/**
 * 
 */
package de.hsh.grappa.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryClassloader {
    private static Logger logger = LoggerFactory
            .getLogger(DirectoryClassloader.class);

    public DirectoryClassloader(String[] pathes, String[] extensions)
            throws Exception {
        logger.debug("Starting DirectoryClassloader");
        for (String string : pathes) {
            logger.debug("Path-Element Iterator: {}", string);
            this.findAndAdd(string, extensions);
        }
    }

    protected void findAndAdd(String start, String[] extensions)
            throws Exception {
        final ArrayList<File> files = new ArrayList<File>(1024);
        final Stack<File> dirs = new Stack<File>();
        final File startdir = new File(start);
        if (startdir.isDirectory()) {
            logger.debug("Found Path ({})", startdir);
            dirs.push(startdir);
        } else if (match(startdir.getName(), extensions)) {
            logger.debug("Found File ({})", startdir);
            files.add(startdir);
        }
        while (dirs.size() > 0) {
            for (File file : dirs.pop().listFiles()) {
                if (file.isDirectory()) {
                    logger.debug("Found Path ({})", startdir);
                    dirs.push(file);
                } else if (match(file.getName(), extensions)) {
                    logger.debug("Found File ({})", startdir);
                    files.add(file);
                }
            }
        }
        for (File e : files) {
            logger.debug("Adding ({}) to Classpath", e.getPath());
            this.addToClasspath(e);
        }
    }

    private boolean match(String s, String[] suffixes) {
        logger.debug(
                "Checking File-Extension of ({}) against valid extensions ({})",
                s, suffixes);
        for (String suffix : suffixes)
            if (s.length() >= suffix.length()
                    && s.substring(s.length() - suffix.length(), s.length())
                            .equalsIgnoreCase(suffix))
                return true;
        return false;
    }

    private void addToClasspath(File file) throws Exception {
        Class<?>[] parameters = new Class[] { URL.class };

        // IMPORTANT: MUST use the webapp classloader - so derived extension
        // classes can resolve their base classes
        ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();

        // cast to a URL class loader so we can additional File(s) to the search
        // path
        URLClassLoader webappClassLoader = (URLClassLoader) contextClassLoader;

        Class<?> sysclass = URLClassLoader.class;

        try {
            URL jarUrl = file.toURI().toURL();

            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(webappClassLoader, new Object[] { jarUrl });
        } catch (Throwable t) {
            throw new IOException(
                    "Error, could not add URL to system classloader");
        }
    }

}
