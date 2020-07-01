package de.hsh.grappa.rest;

import de.hsh.grappa.exceptions.AuthenticationException;
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
public class AuthExceptionMapper implements
    ExceptionMapper<AuthenticationException> {
    private static Logger log = LoggerFactory.getLogger(AuthExceptionMapper.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response toResponse(AuthenticationException e) {
        log.error(e.getMessage());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(Json.createJsonExceptionMessage(e)).build();
    }
}
