package de.hsh.grappa.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderConfig;
import de.hsh.grappa.config.GraderID;
import de.hsh.grappa.service.GraderPoolManager;
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
import javax.ws.rs.core.Response;

@Path("/graders")
public class AllGraderResources {
    private static final Logger log = LoggerFactory.getLogger(AllGraderResources.class);

    @Context
    ContainerRequestContext sr;
    @Context
    ServletContext sc;

    @Context
    public void setServletContext(ServletContext context) {
        this.sc = context;
    }

    /**
     * @return a JSON array with graderInformation (name, version, display_name, proglangs, result_spec) of *online* graders
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response getGraderInfoArray() {
        JsonArray graderInfos = new JsonArray();
        for (GraderID graderId : GraderPoolManager.getInstance().getGraderIds()) {
            var gc = GrappaServlet.CONFIG.getGraders().stream()
                .filter(g -> g.getId().equals(graderId)).findFirst().get();

            JsonObject g = new JsonObject();
            g.addProperty("name", gc.getId().getName());
            g.addProperty("version", gc.getId().getVersion());
            g.addProperty("display_name", gc.getDisplay_name());
            if (gc.getProglangs() != null) {
                JsonArray proglangs = new JsonArray();
                for (String proglang : gc.getProglangs()) {
                    proglangs.add(proglang);
                }
                g.add("proglangs", proglangs);
            }
            if (gc.getResult_spec() != null) {
                JsonObject resultSpec = new JsonObject();
                resultSpec.addProperty("format", gc.getResult_spec().getFormat());
                resultSpec.addProperty("structure", gc.getResult_spec().getStructure());
                resultSpec.addProperty("teacher_feedback_level", gc.getResult_spec().getTeacher_feedback_level());
                resultSpec.addProperty("student_feedback_level", gc.getResult_spec().getStudent_feedback_level());
                g.add("result_spec", resultSpec);
            }
            graderInfos.add(g);
        }
        JsonObject gradersJson = new JsonObject();
        gradersJson.add("graders", graderInfos);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return Response.ok().entity(gson.toJson(gradersJson)).build();
    }

    public static JsonArray getGraderStatusArray() {
        JsonArray graderStatusArray = new JsonArray();
        for (GraderConfig g : GrappaServlet.CONFIG.getGraders()) {
            if(g.getEnabled()) {
                try {
                    var status = GraderResource.getGraderStatus(g.getId());
                    graderStatusArray.add(status);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    log.error(ExceptionUtils.getStackTrace(e));
                }
            }
        }
        return graderStatusArray;
    }
}

