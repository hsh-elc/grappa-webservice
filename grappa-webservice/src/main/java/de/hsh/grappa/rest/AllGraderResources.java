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
import java.util.List;
import java.util.Map;

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
     *
     * @return a JSON array of mappings (graderId, graderName) of *online* graders.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getGraders() {
        var ids = GraderPoolManager.getInstance().getGraderIds();
        JsonObject graders = new JsonObject();
        for (String graderId : ids) {
            var gc = GrappaServlet.CONFIG.getGraders().stream()
                .filter(g -> g.getId().equals(graderId)).findFirst().get();
            JsonObject grader = new JsonObject();
            grader.addProperty(gc.getId(), gc.getName());
            graders.add("graders", grader);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(graders);
    }

//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    public String getStatus() {
//        JsonObject gradersStatus = new JsonObject();
//        for (GraderConfig g : GrappaServlet.CONFIG.getGraders()) {
//            try {
//                var gc = GraderResource.getGraderStatus(g.getId());
//                gradersStatus.add("grader", gc);
//            } catch (Exception e) {
//                log.error(e.getMessage());
//                log.error(ExceptionUtils.getStackTrace(e));
//            }
//        }
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(gradersStatus);
//    }

    public static JsonObject getGradersStatus() {
        JsonObject gradersStatus = new JsonObject();
        for (GraderConfig g : GrappaServlet.CONFIG.getGraders()) {
            try {
                var gc = GraderResource.getGraderStatus(g.getId());
                gradersStatus.add("grader", gc);
            } catch (Exception e) {
                log.error(e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return gradersStatus;
    }
}

