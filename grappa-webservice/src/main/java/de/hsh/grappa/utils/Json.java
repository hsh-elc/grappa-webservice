package de.hsh.grappa.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Json {
    private Json() {
    }

    public static String createJsonKeyValueAsString(String key, Object value) {
        JsonObject j = new JsonObject();
        j.addProperty(key, value.toString());
        return new Gson().toJson(j);
    }

    public static String createJsonExceptionMessage(Throwable e) {
        return createJsonKeyValueAsString("errorMessage", e.getMessage());
    }
}
