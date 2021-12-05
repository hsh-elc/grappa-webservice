package de.hsh.grappa.test;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Test;

import de.hsh.grappa.test.utils.TestConfig;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.Base64;

public class GrappaResourceTest {
    private javax.ws.rs.client.Client client;

    private String basicAuth = "test:test"; // lmsId:passwd

    @Before
    public void loadConfig() throws Exception {
    }

    @Before
    public void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Test
    public void testGetStatus() throws Exception {
        try {
            WebTarget target = client.target(TestConfig.getServer()).path("/");
            try (Response response = target
                .request()
                .header("Authorization", "basic "
                    + Base64.getEncoder().encodeToString(basicAuth.getBytes()))
                .get()) {
                System.out.println(response.getStatus());
                System.out.println(response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }
}