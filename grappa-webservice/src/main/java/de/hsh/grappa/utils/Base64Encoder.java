package de.hsh.grappa.utils;

import org.apache.commons.codec.binary.Base64;

import javax.xml.bind.DatatypeConverter;
import java.nio.ByteBuffer;
import java.util.UUID;

public class Base64Encoder {

    /**
     * Decode the basic auth and convert it to array login/password
     * 
     * @param auth
     *            The string encoded authentification
     * @return The login (case 0), the password (case 1)
     */
    public static String[] decodeAuth(String auth) {
        // Replacing "Basic THE_BASE_64" to "THE_BASE_64" directly
        auth = auth.replaceFirst("[B|b]asic ", "");
        // Decode the Base64 into byte[]
        byte[] decodedBytes = DatatypeConverter.parseBase64Binary(auth);

        // If the decode fails in any case
        if (decodedBytes == null || decodedBytes.length == 0) {
            return null;
        }

        // Now we can convert the byte[] into a splitted array :
        // - the first one is login,
        // - the second one password
        return new String(decodedBytes).split(":", 2);
    }

    public static String uuidToBase64(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Base64.encodeBase64URLSafeString(bb.array());
    }

    public static UUID uuidFromBase64(String str) {
        byte[] bytes = Base64.decodeBase64(str);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
    }
}
