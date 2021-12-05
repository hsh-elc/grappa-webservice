package de.hsh.grappa.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;






public class Tar {
    

    /**
     * Create a byte array representing a tar file containg a single file.
     * @param fileBytes The single file to be wrapped in a tar
     * @param destinationFileName the name of the file to be written to the tar
     * @return a byte array representing the resulting tar
     * @throws IOException
     */
    public static byte[] tar(byte[] fileBytes, String destinationFileName) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                TarArchiveOutputStream tar = new TarArchiveOutputStream(bos)) {
            TarArchiveEntry entry = new TarArchiveEntry(destinationFileName);
            entry.setSize(fileBytes.length);
            entry.setMode(0700);
            tar.putArchiveEntry(entry);
            tar.write(fileBytes);
            tar.closeArchiveEntry();
            tar.close();
            return bos.toByteArray();
        }
    }
    
    /**
     * Reads a file from a one file tar.
     * @param input a stream of bytes representing a tar file with exactly one file in it.
     * @return The bytes of the contained file
     * @throws IOException
     */
    public static byte[] untarSingleFile(InputStream input) throws IOException {
        try (TarArchiveInputStream tarStream = new TarArchiveInputStream(input)) {
            @SuppressWarnings("unused")
            TarArchiveEntry tarEntry = tarStream.getNextTarEntry();
    //      if (null != tarEntry) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                IOUtils.copy(tarStream, baos);
                return baos.toByteArray();
    //          }
    //      } else {
    //          throw new Exception("file has no content.");
            }
        }
    }
}
