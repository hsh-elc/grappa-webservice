package de.hsh.grappa.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderHelper<C> {
    private static Logger log = LoggerFactory.getLogger(de.hsh.grappa.utils.ClassLoaderHelper.class);

    public C LoadClass(String jarPath, String classpath, Class<C> parentClass) throws Exception {
        File jar = new File(jarPath);
        try {
            ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{jar.toURI().toURL()},
                getClass().getClassLoader()
            );
            Class<?> clazz = Class.forName(classpath, true, loader);
            Class<? extends C> newClass = clazz.asSubclass(parentClass);
            Constructor<? extends C> constructor = newClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            log.error("Jar '{}' with classpath '{}' could not be loaded.", jarPath, classpath);
            log.error(e.getMessage());
            throw e;
        }
    }
}
