package de.hsh.grappa.rest;

import de.hsh.grappa.util.Json;
import org.apache.commons.lang.exception.ExceptionUtils;

class Util {

	public static String createJsonExceptionMessage(Throwable e) {
	    return Json.createJsonKeyValueAsString("error", ExceptionUtils.getMessage(e));
	}

}
