package de.hsh.grappa.rest;

import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.utils.Json;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements
    ExceptionMapper<de.hsh.grappa.exceptions.NotFoundException> {
    private static final Logger log = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response toResponse(NotFoundException e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.NOT_FOUND)
            .entity(Json.createJsonExceptionMessage(e)).build();
    }
}
