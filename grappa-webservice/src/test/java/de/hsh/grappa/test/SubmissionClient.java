package de.hsh.grappa.test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

public class SubmissionClient {
    Client client;
    WebTarget target;

    String lmsid;
    String auth;
}
