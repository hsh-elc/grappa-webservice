package de.hsh.grappa.rest;

import de.hsh.grappa.utils.Json;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements
    ExceptionMapper<de.hsh.grappa.exceptions.BadRequestException> {
    private static Logger log = LoggerFactory.getLogger(BadRequestExceptionMapper.class);

    @Override
    public Response toResponse(de.hsh.grappa.exceptions.BadRequestException e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(Json.createJsonExceptionMessage(e)).build();
    }
}
