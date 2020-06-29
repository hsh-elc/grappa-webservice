package de.hsh.grappa.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;

public class ClassLoaderHelper<C> {
    private static Logger log = LoggerFactory.getLogger(ClassLoaderHelper.class);

    public C LoadClass(String classPath, String className, Class<C> parentClass) throws Exception {
        File jar = new File(classPath);
        try {
            ClassLoader loader = URLClassLoader.newInstance(
                new URL[]{jar.toURI().toURL()},
                getClass().getClassLoader()
            );
            Class<?> clazz = Class.forName(className, true, loader);
            Class<? extends C> newClass = clazz.asSubclass(parentClass);
            Constructor<? extends C> constructor = newClass.getConstructor();
            return constructor.newInstance();
        } catch (Exception e) {
            throw new Exception(String.format("Class with path '%s' and class name  '%s' could not be loaded.",
                classPath, className), e);
        }
    }
}
