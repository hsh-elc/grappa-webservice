package de.hsh.grappa.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderID;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.service.GraderStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getStatus() {
        var graderStat = GraderPoolManager.getInstance().getGraderStatistics();
        JsonArray graderStatusArray = AllGraderResources.getGraderStatusArray();

        JsonObject service = new JsonObject();
        service.addProperty("webappName", "grappa-webapp-name_retrieve-from-context");
        service.addProperty("staticConfigPath", GrappaServlet.CONFIG_FILENAME_PATH);

        GraderStatistics total = new GraderStatistics();
        for (Map.Entry<GraderID, GraderStatistics> e : graderStat.entrySet())
            total = total.add(e.getValue());
        service.addProperty("totalGradingProcessesExecuted", total.getExecuted());
        service.addProperty("totalGradingProcessesSucceeded", total.getSucceeded());
        service.addProperty("totalGradingProcessesFailed", total.getFailed());
        service.addProperty("totalGradingProcessesCancelled", total.getCancelled());
        service.addProperty("totalGradingProcessesTimedOut", total.getTimedOut());
        service.addProperty("totalAllExceptExecuted",
            total.getSucceeded()+total.getCancelled()+total.getFailed()+
            total.getTimedOut());
        service.add("graderRuntimeInfo", graderStatusArray);

        // TODO maybe add: service.add("static_config", new JsonParser().parse(gson.toJson(GrappaServlet.CONFIG))

        JsonObject status = new JsonObject();
        status.add("service", service);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return Response.ok().entity(gson.toJson(status)).build();
    }
}

