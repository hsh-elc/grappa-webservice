package de.hsh.grappa.test;

import de.hsh.grappa.utils.TestConfig;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class GrappaResourceTest {
    private javax.ws.rs.client.Client client;

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
                .request().get()) {
                System.out.println(response.getStatus());
                System.out.println(response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}