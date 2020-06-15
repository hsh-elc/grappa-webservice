package de.hsh.grappa.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GrappaConfig;
import de.hsh.grappa.proforma.ProformaV201ResponseGenerator;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.utils.TestConfig;
import de.hsh.grappa.utils.Zip;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private javax.ws.rs.client.Client client;
    private GrappaConfig config;

    private String proformaSubmissionFilePath = "...";

    @Before
    public void loadConfig() throws Exception {
        var mapper = new ObjectMapper(new YAMLFactory());
        var configFile = new File(GrappaServlet.CONFIG_FILENAME_PATH);
        config = mapper.readValue(configFile, GrappaConfig.class);
        //System.out.println("Config file loaded: " + config.toString());
    }

    @Before
    public void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Ignore
    @Test
    public void testCreateGraderWorkersManager() throws Exception {
        GraderPoolManager.getInstance().init(config.getGraders());
    }

    @Ignore
    @Test
    public void pingRedis() throws Exception {
        var redisURI =
            RedisURI.Builder.redis(config.getCache().getRedis().getHost(),
                config.getCache().getRedis().getPort())
                .withPassword(config.getCache().getRedis().getPassword()).build();
        System.out.print("redis uri " + redisURI.toString());
        ClientResources sharedRedis = DefaultClientResources.create();
        RedisClient redisClient = RedisClient.create(sharedRedis, redisURI);
        try (StatefulRedisConnection<String, String> conn = redisClient.connect()) {
            assert conn.sync().ping().equals("PONG") : "Could not establish connection to redis";
            System.out.println(conn.sync().ping());
        }
        redisClient.shutdown();
    }

    @Ignore
    @Test
    public void getResponseAndPrint() throws Exception {
        try {
            String gradeProcId = "cb9c1de5-31d6-4c9a-a789-2e8908b0d3de";
            WebTarget target = client.target(TestConfig.getServer())
                .path("test").path("gradeprocesses").path(gradeProcId);
            try (Response response = target.request()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                //.accept(MediaType.MULTIPART_FORM_DATA)
                .get()) {
                System.out.println(response.getStatus());
                byte[] resp = response.readEntity(byte[].class);
                resp = Zip.getFileFromZip(resp, "response.xml");
                String respXml = IOUtils.toString(resp, "utf8");
                System.out.println(respXml);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testProformaResponseGenerator() throws Exception {
        var resp = ProformaV201ResponseGenerator.createInternalErrorResponse("some error");
        String xmlString = IOUtils.toString(resp.getContent(), "utf8");
        System.out.println(xmlString);
    }

    //@Ignore
    @Test
    public void testStressTestSubmissionSubmitting() throws Exception {
        AtomicInteger cancelCounter = new AtomicInteger(0);
        var exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 200; i++) {
            CompletableFuture.supplyAsync(() -> {
                return testSubmitSubmissionAndReturnGradeProcId();
            }, exec).thenAcceptAsync((gradeProcId) -> {
                int rand = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                if (rand % 4 == 0) {
                    try {
                        Thread.sleep(500);
                        cancelCounter.incrementAndGet();
                        testCancelSubmission(gradeProcId);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }); // don't run thenAcceptAsync on the same executor
        }
        exec.shutdown();
        exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println("cancelCounter: " + cancelCounter.get());
    }

    @Ignore
    @Test
    public void testSubmitSubmission() {
        testSubmitSubmissionAndReturnGradeProcId();
    }

    public String testSubmitSubmissionAndReturnGradeProcId() {
        try {
            WebTarget target = client.target(TestConfig.getServer()).path("/test/gradeprocesses");
            ClassLoader classLoader = getClass().getClassLoader();
            File file = new File(proformaSubmissionFilePath);
            try (Response response = target.queryParam("async", true)
                .queryParam("graderId", "DummyGrader")
                .request()
                .post(Entity.entity(file,
                    /*MediaType.MULTIPART_FORM_DATA*/ MediaType.APPLICATION_OCTET_STREAM))) {
                String json = response.readEntity(String.class);
                JsonParser p = new JsonParser();
                JsonElement e = p.parse(json);
                String gradeProcId = e.getAsJsonObject().get("gradeProcessId").toString();
                gradeProcId = gradeProcId.substring(1, gradeProcId.length() - 1);
                System.out.println(String.format("Submitting... Status: %s, gradeProcessId: %s",
                    response.getStatus(), gradeProcId));
                return gradeProcId;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Ignore
    @Test
    public void testCancelSubmission() throws Exception {
        testCancelSubmission("9bac8990-cc05-41e9-bf15-c0aae3d67b6e");
    }

    public void testCancelSubmission(String gradeProcessId) throws Exception {
        try {
            WebTarget target = client.target(TestConfig.getServer())
                .path("test").path("gradeprocesses").path(gradeProcessId);
//            System.out.println("Testing " + target.getUri());
            try (Response response = target.queryParam("gradeProcessId", gradeProcessId)
                .request().delete()) {
                System.out.println(response.getStatus());
                System.out.println(response.readEntity(String.class));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void testIfTaskIsCached() throws Exception {
        try {
            WebTarget target = client.target(TestConfig.getServer()).path("/tasks/c09c338e-a87d-4fc1-a455-fd84ba3d9650");
            Response response = target.request().head();
            System.out.println(response.getStatus());
            response.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}