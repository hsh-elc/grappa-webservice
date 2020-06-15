package de.hsh.grappa.utils;

import java.net.URI;
import java.net.URISyntaxException;

public class TestConfig {

    public static final String SERVERIP = "http://localhost";
    public static final String SERVERPORT = "8080";
    public static final String DEPLOYNAME = "grappa-webservice";
    public static final String RESTPATH = "rest";
    public static final String PATH_GRADE = "gradeprocesses";

    public static URI getQualifiedUri() throws URISyntaxException {

        return new URI(SERVERIP + ":" + SERVERPORT + "/" + DEPLOYNAME + "/"
            + RESTPATH);

    }

    public static String getServer() {
        return new String(SERVERIP + ":" + SERVERPORT + "/" + DEPLOYNAME + "/" + RESTPATH);
    }
}
