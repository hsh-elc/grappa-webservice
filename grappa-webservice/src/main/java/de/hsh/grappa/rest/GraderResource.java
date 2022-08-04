package de.hsh.grappa.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.GraderID;
import de.hsh.grappa.service.GraderPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proforma.util.exception.NotFoundException;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/graders/{graderName}/{graderVersion}")
public class GraderResource {

    Logger log = LoggerFactory.getLogger(GraderResource.class);
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
    public Response getStatus(@PathParam("graderName") String graderName, @PathParam("graderVersion") String graderVersion) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return Response.ok().entity(gson.toJson(getGraderStatus(GraderPoolManager.getInstance().getGraderId(graderName, graderVersion)))).build();
    }

    public static JsonObject getGraderStatus(GraderID graderId) throws NotFoundException {
        var gcOpt = GrappaServlet.CONFIG.getGraders().stream()
            .filter(g -> g.getEnabled() && g.getId().equals(graderId)).findFirst();
        if (!gcOpt.isPresent())
            throw new NotFoundException(String.format("GraderId '%s' does not exist or is disabled.", graderId.toString()));
        var gc = gcOpt.get();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject graderStatus = new JsonObject();
        graderStatus.addProperty("name", graderId.getName());
        graderStatus.addProperty("version", graderId.getVersion());
        graderStatus.addProperty("displayName", gc.getDisplay_name());
        graderStatus.addProperty("poolSize", GraderPoolManager.getInstance().getPoolSize(graderId));
        graderStatus.addProperty("busyInstances", GraderPoolManager.getInstance().getBusyCount(graderId));
        graderStatus.addProperty("queuedSubmissions",
            RedisController.getInstance().getSubmissionQueueCount(graderId));
//        graderStatus.addProperty("estimatedGradingSecondsTillQueueProcessed",
//            GraderPoolManager.getInstance().getEstimatedSecondsUntilQueueIsGraded(graderId));
        var gsOpt = GraderPoolManager.getInstance().getGraderStatistics()
            .entrySet().stream().filter(e -> e.getKey().equals(graderId)).findFirst();
        if (gsOpt.isPresent()) {
            var gs = gsOpt.get().getValue();
            graderStatus.addProperty("gradingProcessesExecuted", gs.getExecuted());
            graderStatus.addProperty("gradingProcessesSucceeded", gs.getSucceeded());
            graderStatus.addProperty("gradingProcessesFailed", gs.getFailed());
            graderStatus.addProperty("gradingProcessesCancelled", gs.getCancelled());
            graderStatus.addProperty("gradingProcessesTimedOut", gs.getTimedOut());
        }

        return graderStatus;
    }
}

