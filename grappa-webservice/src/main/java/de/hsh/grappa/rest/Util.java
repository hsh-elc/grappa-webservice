package de.hsh.grappa.rest;

import de.hsh.grappa.util.Json;

class Util {

	public static String createJsonExceptionMessage(Throwable e) {
	    return Json.createJsonKeyValueAsString("error", e.getMessage());
	}

}
