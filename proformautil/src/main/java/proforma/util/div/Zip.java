package proforma.util.div;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Zip {


    /**
     * An in-memory representation of a zip file's entry.
     */
    public static class ZipContentElement {
        private String path;
        private byte[] bytes;
        private long time;

        public ZipContentElement(String path, byte[] bytes, long time) {
            super();
            this.path = path;
            this.bytes = bytes;
            this.time = time;
        }

        /**
         * @return the path, where directories end with a trailing '/'
         */
        public String getPath() {
            return path;
        }

        /**
         * @return the content as a byte array
         */
        public byte[] getBytes() {
            return bytes;
        }

        /**
         * @return the size in bytes
         */
        public long getSize() {
            return bytes.length;
        }

        /**
         * @return The last modification time of the entry in milliseconds since the epoch
         */
        public long getTime() {
            return time;
        }

        /**
         * @return true, if the path ends with '/'
         */
        public boolean isDirectory() {
            return path.endsWith("/");
        }

        /**
         * @param path the path, where directories end with a trailing '/'
         */
        public void setPath(String path) {
            this.path = path;
        }

        /**
         * @param bytes the content as a byte array
         */
        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        /**
         * @param time The last modification time of the entry in milliseconds since the epoch
         */
        public void setTime(long time) {
            this.time = time;
        }
    }

    public static class ZipContent extends TreeMap<String, ZipContentElement> {
        private static final long serialVersionUID = 2877158655266768531L;

        public ZipContent() {
            super();
        }
    }


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
            ZipArchiveEntry entry = null;
            while (null != (entry = zip.getNextZipEntry())) {
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

    /**
     * Read a zip file from a stream and return a map of all contents in memory.
     * All paths will be normalized to the '/' dir separator.
     *
     * @param zipStream source
     * @return the content of the zip stream as a map pointing paths to elements
     * @throws IOException
     */
    public static ZipContent readZipFileToMap(InputStream zipStream) throws IOException {
        ZipContent result = new ZipContent();
        try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new BufferedInputStream(zipStream))) {
            ZipArchiveEntry entry = null;
            byte[] buffer = new byte[10000000]; // 10Mb file
            while (null != (entry = zip.getNextZipEntry())) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int len;
                    while (-1 != (len = zip.read(buffer))) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    String p = entry.getName();
                    p = p.replace('\\', '/');
                    long time = entry.getTime();
                    ZipContentElement elem = new ZipContentElement(p, out.toByteArray(), time);
                    result.put(p, elem);
                }
            }
            return result;
        }
    }

    /**
     * Write in memory elements to a zip file output stream.
     *
     * @param content The in memory elements to be written
     * @param os      the target stream
     * @throws IOException
     */
    public static void writeMapToZipFile(Map<String, ZipContentElement> content, OutputStream os) throws IOException {
        try (ZipArchiveOutputStream zip = new ZipArchiveOutputStream(new BufferedOutputStream(os))) {
            for (String path : content.keySet()) {
                ZipContentElement elem = content.get(path);
                ZipArchiveEntry entry = new ZipArchiveEntry(path);
                entry.setSize(elem.getSize());
                entry.setTime(elem.getTime());

                zip.putArchiveEntry(entry);
                if (!elem.isDirectory()) {
                    zip.write(elem.bytes);
                }
                zip.closeArchiveEntry();
            }
            zip.finish();
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


    public static boolean isZip(byte[] bytes) {
        return bytes.length > 1 && bytes[0] == (byte) 'P' && bytes[1] == (byte) 'K';
    }


    /**
     * If the given bytes represent a zip file with a single entry, this method returns that entry.
     * If the bytes do not represent a zip file or it is a zip file with more or less than one file in it,
     * this method returns null.
     *
     * @param bytes
     * @return
     */
    public static ZipContentElement unzipSingleOrNull(byte[] bytes) {
        if (!isZip(bytes)) return null;
        try {
            ZipContent map = readZipFileToMap(new ByteArrayInputStream(bytes));
            if (map.size() != 1) return null;
            return map.values().iterator().next();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Wrap a single file into a zip file containing only that file
     * @param content The file content to pack into the zip
     * @param filename The name of the file to put into the zip
     * @return The zip as byte array
     * @throws IOException
     */
    public static byte[] wrapSingleFileIntoZip(byte[] content, String filename) throws IOException {
        Zip.ZipContent zip = new Zip.ZipContent();
        zip.put(filename, new Zip.ZipContentElement(filename, content, System.currentTimeMillis()));

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Zip.writeMapToZipFile(zip, out);
            return out.toByteArray();
        }
    }
}
