package de.hsh.grappa.util;

import java.util.UUID;

public class ObjectId {
    private ObjectId() {}

    /**
     * Creates a unique UUID string, 23 chars in length.
     *
     * @return
     */
    public static String createObjectId() {
        return UUID.randomUUID().toString();
    }
}
