package de.hsh.grappa.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {
    public static String getTextFileContentFromZip(byte[] zipBytes, String fileName, Charset charset) throws Exception {
        try (ByteArrayInputStream baos = new ByteArrayInputStream(zipBytes)) {
            return getTextFileContentFromZip(baos, fileName, charset);
        }
    }

    public static String getTextFileContentFromZip(InputStream zipStream, String fileName, Charset charset) throws Exception {
        byte[] content = Zip.getFileFromZip(zipStream, fileName);
        return new String(content, charset);
    }

    public static byte[] getFileFromZip(byte[] zipBytes, String fileName) throws Exception {
        try (ByteArrayInputStream is = new ByteArrayInputStream(zipBytes)) {
            return getFileFromZip(is, fileName);
        }
    }

    public static byte[] getFileFromZip(InputStream zipStream, String fileName) throws Exception {
        //fileName = fileName.replace('\\', '/');
        Path filePath = Paths.get(fileName);
        //System.out.println("Looking for " + fileName + "in ZIP...");
        boolean fileNotFound = true;
        try (ZipInputStream zip = new ZipInputStream(zipStream)) {
            var out = new ByteArrayOutputStream();
            ZipEntry ze;
            while (null != (ze = zip.getNextEntry())) {
                Path zePath = Paths.get(ze.getName());
                //System.out.println("Checking zip file: " + zePath);
                if(zePath.equals(filePath)) {
                    fileNotFound = false;
                    byte[] buffer = new byte[10000000]; // 10Mb file
                    int len;
                    while (-1 != (len = zip.read(buffer))) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    break;
                }
            }
            zip.close(); // TODO is auto closed, actually
            if(fileNotFound)
                throw new FileNotFoundException(String.format("File '%s' not found in ZIP archive.", filePath));
            return out.toByteArray();

        } catch (Exception e) {
            throw e;
        }
    }
}
