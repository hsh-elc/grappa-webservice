package de.hsh.grappa.util;

public class Strings {
	/**
	 * Taken from com.google.common.base.Strings
	 * @parama string reference to check
     * @return {@code true} if the string is null or is the empty string
	 */
	public static boolean isNullOrEmpty(String string) {
		return string == null || string.isEmpty();
	}
}
