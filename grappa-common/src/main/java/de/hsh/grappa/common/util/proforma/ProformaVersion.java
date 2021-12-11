package de.hsh.grappa.common.util.proforma;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsh.grappa.common.util.proforma.impl.ProformaResponseHelper;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionHelper;
import de.hsh.grappa.common.util.proforma.impl.ProformaTaskHelper;

/**
 * TODO: replace this class by a generic handling of different ProFormA versions at the same time.
 * 
 * Currently the ProFormA version is nailed down to 2.1
 *
 */
public abstract class ProformaVersion {
	
	private static final Logger log = LoggerFactory.getLogger(ProformaVersion.class);

	private static final String defaultVersionNumber = "2.1";

	public static String getDefaultVersionNumber() {
		return defaultVersionNumber;
	}
	public static ProformaVersion getDefault() {
		return getInstance(getDefaultVersionNumber());
	}
	
	public abstract String getVersionNumber();
	public abstract ProformaTaskHelper getTaskHelper();
	public abstract ProformaSubmissionHelper getSubmissionHelper();
	public abstract ProformaResponseHelper getResponseHelper();	
	
	@Override
	public String toString() {
		return getVersionNumber();
	}
	
	
	static {
		instances= new HashMap<>();
		tryRegister("2.1", "de.hsh.grappa.proforma21.Proforma21Version");
	}
    
    
	private static Map<String, ProformaVersion> instances;
	
	
	private static void tryRegister(String proformaVersion, String className) {
		synchronized (instances) {
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
				pv = ch.newInstance();
			} catch (Exception e) {
				log.warn("Unable to register {} -> {} ({}, {})", proformaVersion, className, e.getClass(), e.getMessage());
				return;
			}
	   		instances.put(proformaVersion, pv);
		}
	}
	
    public static ProformaVersion getInstance(String proformaVersion) {
		synchronized (instances) {
	    	if (instances.containsKey(proformaVersion)) {
	    		return instances.get(proformaVersion);
	    	} 
	   		throw new UnsupportedOperationException("Unsupported ProFormA version "+proformaVersion);
		}
    }



}
