package de.hsh.grappa.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Json {
    private Json() {
    }

    public static String createJsonKeyValueAsString(String[][] keyVals) {
        JsonObject j = new JsonObject();
        for (int i = 0; i < keyVals.length; ++i) {
            String key = keyVals[i][0];
            String val = keyVals[i][1];
            j.addProperty(key, val);
        }
        return new Gson().toJson(j);
    }

    public static String createJsonKeyValueAsString(String key, Object value) {
        JsonObject j = new JsonObject();
        j.addProperty(key, value.toString());
        return new Gson().toJson(j);
    }

    public static String createJsonExceptionMessage(Throwable e) {
        return createJsonKeyValueAsString("error", e.getMessage());
    }
}
