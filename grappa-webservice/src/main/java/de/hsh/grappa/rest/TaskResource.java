package de.hsh.grappa.rest;

import de.hsh.grappa.application.GrappaServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path("/tasks")
public class TaskResource {
    Logger log = LoggerFactory.getLogger(TaskResource.class);

    @Context
    ContainerRequestContext sr;
    @Context
    ServletContext sc;

    @Context
    public void setServletContext(ServletContext context) {
        this.sc = context;
    }

    @HEAD
    @Path("/{taskUuid}")
    public Response exists(@PathParam("taskUuid") String taskUuid) {
        if (GrappaServlet.redis.isTaskCached(taskUuid))
            return Response.ok().build();
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
