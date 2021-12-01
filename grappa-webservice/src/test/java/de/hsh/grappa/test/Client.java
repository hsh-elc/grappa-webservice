package de.hsh.grappa.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.GrappaConfig;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.utils.TestConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Client {
    private javax.ws.rs.client.Client client;
    private GrappaConfig config;
    private final String basicAuth = "test:test"; // lmsId:password

    private final String submissionFilePath = "path/to/example/submission-separate.zip";

    @Before
    public void loadConfig() throws Exception {
        var mapper = new ObjectMapper(new YAMLFactory());
        var configFile = new File(GrappaServlet.CONFIG_FILENAME_PATH);
        config = mapper.readValue(configFile, GrappaConfig.class);
    }

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @Ignore
    @Test
    public void testCreateGraderWorkersManager() {
        GraderPoolManager.getInstance().init(config.getGraders());
    }

    @Ignore
    @Test
    public void pingRedis() {
        var jedis= new Jedis(config.getCache().getRedis().getHost(),
                config.getCache().getRedis().getPort(), Protocol.DEFAULT_TIMEOUT, false);
        System.out.print("redis host:port " + config.getCache().getRedis().getHost() + ":" +
                config.getCache().getRedis().getPort());
        jedis.auth( config.getCache().getRedis().getPassword() );
        
        assert jedis.ping().equals("PONG") : "Could not establish connection to redis";
        System.out.println(jedis.ping());
        
        jedis.close();
    }

    @Ignore
    @Test
    public void getResponseAndPrint() {
        try {
            String gradeProcId = "3c1d66cd-17df-45c6-a554-274f1d3a4f74";
            WebTarget target = client.target(TestConfig.getServer())
                .path("test").path("gradeprocesses").path(gradeProcId);
            try (Response response = target.request()
                .header("Authorization", "basic "
                    + Base64.getEncoder().encodeToString(basicAuth.getBytes()))
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                //.accept(MediaType.MULTIPART_FORM_DATA)
                .get()) {
                System.out.println(response.getStatus());
                var respBody = response.readEntity(byte[].class);
                byte[] xmlBytes = respBody;
                String respXml = IOUtils.toString(xmlBytes, "utf8");
                System.out.println(respXml);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Ignore
    @Test
    public void poll() throws Exception {
        String gradeProcId = "71adb87d-2b78-4c6c-b52e-d18c84ac3ed4";
        try {
            WebTarget target = client.target(TestConfig.getServer()).path("/test/gradeprocesses")
                .path(gradeProcId);

            try (Response response = target
                .request()
                .header("Authorization", "basic "
                    + Base64.getEncoder().encodeToString(basicAuth.getBytes()))
                .accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                .get()) {

                if (response.getStatus() == Response.Status.ACCEPTED.getStatusCode()) {
                    String json = response.readEntity(String.class);
                    System.out.println("Status: " + response.getStatus() + ", JSON: " + json);
                } else if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                    if (response.getMediaType().isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE)) {
                        byte[] b = response.readEntity(byte[].class);
                        System.out.println("Status: " + response.getStatus() + ", byte[].length: " + b.length);
                        System.out.println("Content: ");
                        // it'll be plaintext XML, or binary nonesense:
                        System.out.println(new String(b, StandardCharsets.UTF_8));
                    } else
                        throw new Exception("unexpected media type");
                } else
                    throw new Exception("poll() received status " + response.getStatus());
            }
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
        }
    }

    @Ignore
    @Test
    public void testSubmissionQueueIndex() throws Exception {
        final String gradeProcId = "e40433e9-1184-40e9-a312-fa4545d1882a";
        RedisController.getInstance().init(config.getCache());
        int i = RedisController.getInstance().getQueuedSubmissionIndex(gradeProcId);
        System.out.println(i);
    }

    @Ignore
    @Test
    public void testStressTestSubmissionSubmitting() throws Exception {
        AtomicInteger cancelCounter = new AtomicInteger(0);
        var exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 50; i++) {
            System.out.println("Submitting no. " + i);
            String gradeProcId = testSubmitSubmission(submissionFilePath, false);
        }
    }

    @Ignore
    @Test
    public void testStressTestSubmissionSubmittingAsyncWithRandomCancelling() throws Exception {
        AtomicInteger cancelCounter = new AtomicInteger(0);
        var exec = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 200; i++) {
            CompletableFuture.supplyAsync(() -> {
                return testSubmitSubmission(submissionFilePath, true);
            }, exec).thenAccept((gradeProcId) -> {
                if (null == gradeProcId)
                    return;

                int rand = ThreadLocalRandom.current().nextInt(0, 3 + 1);
                if (rand % 4 == 0) {
                    try {
                        Thread.sleep(500);
                        cancelCounter.incrementAndGet();
                        testCancelSubmission(gradeProcId);
                    } catch (Exception e) {
                        System.out.println(ExceptionUtils.getStackTrace(e));
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
    public void testSubmitSubmissionAsync() {
        testSubmitSubmission(submissionFilePath, true);
    }

    public String testSubmitSubmission(String submFilePath, boolean async) {
        try {
            WebTarget target = client.target(TestConfig.getServer()).path("/test/gradeprocesses");
            File file = new File(submissionFilePath);
            MediaType mediaType = FilenameUtils.getExtension(submissionFilePath)
                .equals("xml") ? MediaType.APPLICATION_XML_TYPE : MediaType.APPLICATION_OCTET_STREAM_TYPE;

            try (Response response = target.queryParam("async", async)
                .queryParam("prioritize", true)
                .queryParam("graderId", "DummyGrader")
                .request()
                .header("Authorization", "basic "
                    + Base64.getEncoder().encodeToString(basicAuth.getBytes()))
                .post(Entity.entity(file, mediaType))) {

                String json = response.readEntity(String.class);
                System.out.println("Status: " + response.getStatus() + ", return body: " + json);
                JsonParser p = new JsonParser();
                JsonElement e = p.parse(json);
                if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                    String gradeProcId = e.getAsJsonObject().get("gradeProcessId").toString();
                    gradeProcId = gradeProcId.substring(1, gradeProcId.length() - 1);
                    System.out.println(String.format("Submitted Status: %s, gradeProcessId: %s",
                        response.getStatus(), gradeProcId));
                    return gradeProcId;
                } else {
                    String error = e.getAsJsonObject().get("error").toString();
                    System.out.println("Request resulted in an error: " + error);
                }
            }
        } catch (Exception e) {
            System.out.println(ExceptionUtils.getStackTrace(e));
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
        final String gradeProcId = "e40433e9-1184-40e9-a312-fa4545d1882a";
        try {
            WebTarget target =
                client.target(TestConfig.getServer()).path("/tasks/" + gradeProcId);
            Response response = target.request().head();
            System.out.println(response.getStatus());
            response.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}