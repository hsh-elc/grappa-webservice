package de.hsh.grappa.rest;

import de.hsh.grappa.utils.Json;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AllExceptionMapper implements
    ExceptionMapper<Throwable> {
    private static Logger log = LoggerFactory.getLogger(AllExceptionMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(Json.createJsonExceptionMessage(e)).build();
    }
}
