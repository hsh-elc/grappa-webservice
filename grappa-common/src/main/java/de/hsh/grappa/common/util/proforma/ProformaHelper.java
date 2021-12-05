package de.hsh.grappa.common.util.proforma;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class ProformaHelper {

	
	private static final Logger log = LoggerFactory.getLogger(ProformaHelper.class);
	
	private static Map<String, Map<Class<?>, ProformaHelper>> instances= new HashMap<>();
	
	
	protected static <H extends ProformaHelper> void tryRegister(String proformaVersion, Class<H> clazz, String className) {
		synchronized (instances) {
			log.info("Register ProFormA version {} -> {} -> {}", proformaVersion, clazz.getName(), className);
	    	if (!instances.containsKey(proformaVersion)) {
	    		instances.put(proformaVersion, new HashMap<>());
	    	}
	    	Map<Class<?>, ProformaHelper> map = instances.get(proformaVersion);
	    	Class<?> c;
	    	try {
				c = Class.forName(className);
			} catch (Exception e) {
				log.warn("Unable to register {} -> {} -> {} ({}, {})", proformaVersion, clazz.getName(), className, e.getClass(), e.getMessage());
				return;
			}
	    	Class<? extends H> ch = c.asSubclass(clazz);
	    	H helper;
	    	try {
				helper = ch.newInstance();
			} catch (Exception e) {
				log.warn("Unable to register {} -> {} -> {} ({}, {})", proformaVersion, clazz.getName(), className, e.getClass(), e.getMessage());
				return;
			}
	   		map.put(clazz, helper);
		}
	}
	
    protected static <H extends ProformaHelper> H getInstance(String proformaVersion, Class<H> clazz) {
		synchronized (instances) {
	    	if (instances.containsKey(proformaVersion)) {
	        	Map<Class<?>, ProformaHelper> map = instances.get(proformaVersion);
	        	if (map.containsKey(clazz)) {
	        		return clazz.cast(map.get(clazz));
	        	} 
	    	} 
	   		throw new UnsupportedOperationException("Unsupported ProFormA version "+proformaVersion);
		}
    }
    
}
