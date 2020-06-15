package de.hsh.grappa.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.service.GraderStatistics;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Path("/")
public class GrappaResource {

    Logger log = LoggerFactory.getLogger(GrappaResource.class);
    @Context
    ContainerRequestContext sr;
    @Context
    ServletContext sc;

    @Context
    public void setServletContext(ServletContext context) {
        this.sc = context;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStatus() {
        var graderStat = GraderPoolManager.getInstance().getGraderStatistics();


        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject gradersRuntimeInfo = new JsonObject();
        for (GraderConfig g : GrappaServlet.CONFIG.getGraders()) {
            JsonObject graderStatus = new JsonObject();
            graderStatus.addProperty("id", g.getId());
            graderStatus.addProperty("currentlyQueuedSubmissions",
                GrappaServlet.redis.getSubmissionQueueCount(g.getId()));
            GraderStatistics gs = graderStat.get(g.getId());
            if (null != gs) {
                graderStatus.addProperty("gradingProcessesExecuted", gs.getExecuted());
                graderStatus.addProperty("gradingProcessesSucceeded", gs.getSucceeded());
                graderStatus.addProperty("gradingProcessesFailed", gs.getFailed());
                graderStatus.addProperty("gradingProcessesCancelled", gs.getCancelled());
                graderStatus.addProperty("gradingProcessesTimedOut", gs.getTimedOut());
            }
            gradersRuntimeInfo.add("grader", graderStatus);
        }

        JsonObject service = new JsonObject();
        service.addProperty("webappName", "grappa-webapp-name_retrieve-from-context");
        service.addProperty("staticConfigPath", GrappaServlet.CONFIG_FILENAME_PATH);


        GraderStatistics total = new GraderStatistics();
        for (Map.Entry<String, GraderStatistics> e : graderStat.entrySet())
            total = total.add(e.getValue());
        service.addProperty("totalGradingProcessesExecuted", total.getExecuted());
        service.addProperty("totalGradingProcessesSucceeded", total.getSucceeded());
        service.addProperty("totalGradingProcessesFailed", total.getFailed());
        service.addProperty("totalGradingProcessesCancelled", total.getCancelled());
        service.addProperty("totalGradingProcessesTimedOut", total.getTimedOut());
        service.addProperty("totalAllExceptExecuted",
            total.getSucceeded()+total.getCancelled()+total.getFailed()+
            total.getTimedOut());
        service.add("graderRuntimeInfo", gradersRuntimeInfo);

        // TODO maybe add: service.add("static_config", new JsonParser().parse(gson.toJson(GrappaServlet.CONFIG))
        // .getAsJsonObject());

        JsonObject status = new JsonObject();
        status.add("service", service);

        return gson.toJson(status);
    }

    private String getConfigString() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            var mapper = new ObjectMapper(new YAMLFactory());
            mapper.writeValue(baos, GrappaServlet.CONFIG);
            return baos.toString(Charsets.UTF_8);
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
            return e.getMessage();
        }
    }
}

