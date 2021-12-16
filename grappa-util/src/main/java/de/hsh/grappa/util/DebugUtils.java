package de.hsh.grappa.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class DebugUtils{

	/**
	 * Prints stack trace of Exception e into the message of new thrown Exception.
	 * This method _always_ throws a new Exception.
	 * Use it e.g. within catch-block in case you want to share all Exception-info (plus additional info) where you usually only see the message. 
	 * @param e Exception which stack trace will be printed .
	 * @param additionalInfo Some info you can share _when_ and _why_ Exception e occurred. 
	 * @throws Exception New Exception containing the stack trace of e and additionalInfo in message. 
	 */
	public static void throwNewExceptionWithStackTraceForDebugPurpose(Exception e, String additionalInfo) throws Exception{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		String stackTrace=sw.toString();
		throw new Exception(
				"startExceptionStackDump"+System.lineSeparator()+
				"exMessage: "+e.getMessage()+System.lineSeparator()+
				"exStackTrace: "+stackTrace+System.lineSeparator()+
				"additionalInfo: "+additionalInfo+System.lineSeparator()+
				"endExceptionStackDump"+System.lineSeparator());
	}
}