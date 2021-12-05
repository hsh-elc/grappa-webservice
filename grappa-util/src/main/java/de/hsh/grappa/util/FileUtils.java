package de.hsh.grappa.util;

import java.io.File;
import java.io.IOException;

/**
 * Wrapper around org.apache.commons.io.FileUtils.
 */
public class FileUtils {

	public static byte[] readFileToByteArray(final File file) throws IOException {
		return org.apache.commons.io.FileUtils.readFileToByteArray(file);
	}
}
