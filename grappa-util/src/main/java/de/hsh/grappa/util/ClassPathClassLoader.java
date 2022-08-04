package de.hsh.grappa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

/**
 * The old code using "URLClassLoader webappClassLoader = (URLClassLoader) contextClassLoader"
 * stopped working with Java 9+. The new ClassLoader extends from URLClassLoader, making sure
 * that method "addURL" does in fact exists.
 * <p>
 * This classloader is used to load files into the classpath at runtime and intantiate
 * objects using that same classloader.
 *
 * @param <C> base class
 */
public class ClassPathClassLoader<C> extends URLClassLoader {
    private static Logger logger = LoggerFactory
        .getLogger(ClassPathClassLoader.class);

    private Class<C> baseClass;
    private String name;

    private static int lastId = (int) (Math.random() * Integer.MAX_VALUE);
    private String id = String.format("id=%d", ++lastId);

    public static class Classpath {
        private String[] pathes;
        private String[] extensions;

        public static Classpath of(String[] pathes, String[] extensions) {
            Classpath cp = new Classpath();
            cp.pathes = pathes;
            cp.extensions = extensions;
            return cp;
        }

        /**
         * @param pathes     semicolon separated pathes
         * @param extensions semicolon separated extensions
         */
        public static Classpath of(String pathes, String extensions) {
            String[] classpathParts = pathes.split(";");
            String[] extensionsParts = extensions.split(";");
            return of(classpathParts, extensionsParts);
        }

        public String[] getPathes() {
            return pathes;
        }

        public String[] getExtensions() {
            return extensions;
        }
    }


    /**
     * @param baseClass
     * @param name      A displayable name to be printed in log entries.
     */
    public ClassPathClassLoader(Class<C> baseClass, String name) {
        super(new URL[0], ClassPathClassLoader.class.getClassLoader());
        this.baseClass = baseClass;
        this.name = name;
    }

    public void configure(Classpath cp) throws Exception {
        logger.debug("Current classpathes: {}", Arrays.toString(cp.getPathes()));
        logger.debug("Current extensions: {}", Arrays.toString(cp.getExtensions()));
        logger.trace("Starting ClassPathClassLoader");
        for (String string : cp.pathes) {
            logger.trace("Path-Element Iterator: {}", string);
            this.findAndAdd(string, cp.extensions);
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

    /**
     * Before instantiating a class, you should call {@link #configure(Classpath)}.
     *
     * @param className specifies the subclass to be loaded and instantiated.
     * @return
     * @throws Exception
     */
    public C instantiateClass(String className) throws Exception {
        logger.trace("instantiateClass ({})", className);
        Class<?> clazz = Class.forName(className, true, this);
        Class<? extends C> newClass = clazz.asSubclass(baseClass);
        Constructor<? extends C> constructor = newClass.getConstructor();
        logger.info("{} '{}' loaded successfully.", className, name);
        return constructor.newInstance();
    }

    private void addToClasspath(File file) throws Exception {
        URL url = file.toURI().toURL();
        this.addURL(url);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        logger.trace("loadClass[{}] ({}, {})", id, name, resolve);
        Class<?> clazz = super.loadClass(name, resolve);
        logger.trace("loaded class {} from {}", clazz.getName(), clazz.getProtectionDomain().getCodeSource());
        return clazz;
    }

}
