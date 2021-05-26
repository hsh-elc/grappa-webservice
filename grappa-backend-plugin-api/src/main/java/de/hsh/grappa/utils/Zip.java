package de.hsh.grappa.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;

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
        Path filePath = Paths.get(fileName);
        //System.out.println("Looking for " + filePath + " in ZIP...");
        boolean fileNotFound = true;

        try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(zipStream))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ZipEntry ze;

            ZipArchiveEntry entry = null;
            while (null != (entry = zip.getNextZipEntry())) {
                if (entry == null) {
                    break;
                }

                Path zePath = Paths.get(entry.getName());
                //System.out.println("Checking file/derectory in ZIP: " + zePath);
                if (zePath.equals(filePath)) {
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

            if (fileNotFound)
                throw new FileNotFoundException(String.format("File '%s' not found in ZIP archive.", filePath));
            return out.toByteArray();
        } catch (Throwable e) {
            System.err.println(e);
            throw e;
        }
    }

// This code (Java's native ZipInputStream) works just fine.
// However its error info on exceptions is pretty much non-existent
// when it comes to handling invalid ZIP files.
//
//    public static byte[] getFileFromZip(InputStream zipStream, String fileName) throws Exception {
//        System.out.println("Entering getFileFromZip");
//        //fileName = fileName.replace('\\', '/');
//        Path filePath = Paths.get(fileName);
//        System.out.println("Looking for " + filePath + " in ZIP...");
//        boolean fileNotFound = true;
//        try (ZipInputStream zip = new ZipInputStream(zipStream)) {
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            ZipEntry ze;
//            ze = zip.getNextEntry();
//            //while (null != (ze = zip.getNextEntry())) {
//            do {
//                if(ze == null) {
//                    System.out.println("ze null.. checking next entry...");
//                    ze = zip.getNextEntry();
//                    System.out.println("and: " + ze);
//                    break;
//                }
//
//                Path zePath = Paths.get(ze.getName());
//                //System.out.println("Checking file/derectory in ZIP: " + zePath);
//                System.out.println(String.format("'%s'=='%s' -> %s", filePath, zePath, zePath.equals(filePath)));
//                if(zePath.equals(filePath)) {
//                    fileNotFound = false;
//                    byte[] buffer = new byte[10000000]; // 10Mb file
//                    int len;
//                    while (-1 != (len = zip.read(buffer))) {
//                        out.write(buffer, 0, len);
//                    }
//                    out.close();
//                    break;
//                }
//            } while (null != (ze = zip.getNextEntry()));
//            //zip.close(); // TODO is auto closed, actually
//            if(fileNotFound)
//                throw new FileNotFoundException(String.format("File '%s' not found in ZIP archive.", filePath));
//            return out.toByteArray();
//
//        } catch (Throwable e) {
//            System.err.println(e);
//            throw e;
//        }
//    }
}
