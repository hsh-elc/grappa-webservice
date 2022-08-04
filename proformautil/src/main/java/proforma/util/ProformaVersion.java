package proforma.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * This class can handle different ProFormA versions at the same time.
 * <p>
 * Currently the default ProFormA version is 2.1
 */
public abstract class ProformaVersion {

    private static final Logger log = LoggerFactory.getLogger(ProformaVersion.class);

    private static final String defaultVersionNumber = "2.1";

    public static String getDefaultVersionNumber() {
        return defaultVersionNumber;
    }

    public static ProformaVersion getDefault() {
        return getInstanceByVersionNumber(getDefaultVersionNumber());
    }

    public abstract String getVersionNumber();

    public abstract String getXmlNamespaceUri();

    public abstract ProformaTaskHelper getTaskHelper();

    public abstract ProformaSubmissionHelper getSubmissionHelper();

    public abstract ProformaResponseHelper getResponseHelper();

    @Override
    public String toString() {
        return getVersionNumber();
    }


    static {
        instancesByVersionNumber = new HashMap<>();
        instancesByNamespaceUri = new HashMap<>();
        lock = new Object();
        tryRegister("2.1", "proforma.util21.Proforma21Version");
    }

    private static Object lock;
    private static Map<String, ProformaVersion> instancesByVersionNumber;
    private static Map<String, ProformaVersion> instancesByNamespaceUri;


    private static void tryRegister(String proformaVersion, String className) {
        synchronized (lock) {
            log.info("Register ProFormA version {} -> {}", proformaVersion, className);
            Class<?> c;
            try {
                c = Class.forName(className);
            } catch (Exception e) {
                log.warn("Unable to register {} -> {} ({}, {})", proformaVersion, className, e.getClass(), e.getMessage());
                return;
            }
            Class<? extends ProformaVersion> ch;
            try {
                ch = c.asSubclass(ProformaVersion.class);
            } catch (Exception e) {
                log.warn("Unable to register {} -> {} ({}, {})", proformaVersion, className, e.getClass(), e.getMessage());
                return;
            }
            ProformaVersion pv;
            try {
                pv = ch.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                log.warn("Unable to register {} -> {} ({}, {})", proformaVersion, className, e.getClass(), e.getMessage());
                return;
            }
            instancesByVersionNumber.put(proformaVersion, pv);
            instancesByNamespaceUri.put(pv.getXmlNamespaceUri(), pv);
        }
    }

    public static ProformaVersion getInstanceByVersionNumber(String proformaVersion) {
        synchronized (lock) {
            if (instancesByVersionNumber.containsKey(proformaVersion)) {
                return instancesByVersionNumber.get(proformaVersion);
            }
            throw new UnsupportedOperationException("Unsupported ProFormA version " + proformaVersion);
        }
    }


    public static ProformaVersion getInstanceByNamespaceUri(String xmlNamespaceUri) {
        synchronized (lock) {
            if (instancesByNamespaceUri.containsKey(xmlNamespaceUri)) {
                return instancesByNamespaceUri.get(xmlNamespaceUri);
            }
            throw new UnsupportedOperationException("Unsupported XML namespace " + xmlNamespaceUri);
        }
    }
}
