package de.hsh.grappa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Stack;

/**
 * The old code using "URLClassLoader webappClassLoader = (URLClassLoader) contextClassLoader"
 * stopped working with Java 9+. The new ClassLoader extends from URLClassLoader, making sure
 * that method "addURL" does in fact exists.
 *
 * This classloader is used to load files into the classpath at runtime and intantiate
 * BackendPlugin plugins using that same classloader.
 *
 * @param <C>
 */
public class ClassPathClassLoader<C> extends URLClassLoader {
    private static Logger logger = LoggerFactory
        .getLogger(ClassPathClassLoader.class);

    public ClassPathClassLoader(String[] pathes, String[] extensions)
        throws Exception {
        super(new URL[0], ClassPathClassLoader.class.getClassLoader());
        logger.trace("Starting DirectoryClassloader");
        for (String string : pathes) {
            logger.trace("Path-Element Iterator: {}", string);
            this.findAndAdd(string, extensions);
        }
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

    protected void findAndAdd(String start, String[] extensions)
        throws Exception {
        final ArrayList<File> files = new ArrayList<File>();
        final Stack<File> dirs = new Stack<File>();
        final File startdir = new File(start);
        if (startdir.isDirectory()) {
            logger.trace("Found Path ({})", startdir);
            dirs.push(startdir);
        } else if (match(startdir.getName(), extensions)) {
            logger.trace("Found File ({})", startdir);
            files.add(startdir);
        }
        while (dirs.size() > 0) {
            for (File file : dirs.pop().listFiles()) {
                if (file.isDirectory()) {
                    logger.trace("Found Path ({})", startdir);
                    dirs.push(file);
                } else if (match(file.getName(), extensions)) {
                    logger.trace("Found File ({})", startdir);
                    files.add(file);
                }
            }
        }
        for (File e : files) {
            logger.trace("Adding ({}) to Classpath", e.getPath());
            this.addToClasspath(e);
        }
    }

    private boolean match(String s, String[] suffixes) {
        logger.trace("Checking File-Extension of ({}) against valid extensions ({})", s, suffixes);
        for (String suffix : suffixes)
            if (s.length() >= suffix.length()
                && s.substring(s.length() - suffix.length(), s.length())
                .equalsIgnoreCase(suffix))
                return true;
        return false;
    }

    public C instantiateClass(String className, Class<C> parentClass) throws Exception {
        Class<?> clazz = Class.forName(className, true, this);
        Class<? extends C> newClass = clazz.asSubclass(parentClass);
        Constructor<? extends C> constructor = newClass.getConstructor();
        return constructor.newInstance();
    }

    private void addToClasspath(File file) throws Exception {
        URL url = file.toURI().toURL();
        this.addURL(url);
    }

}
