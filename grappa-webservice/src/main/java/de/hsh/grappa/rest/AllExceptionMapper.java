package de.hsh.grappa.rest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class AllExceptionMapper implements
    ExceptionMapper<Throwable> {
    private static final Logger log = LoggerFactory.getLogger(AllExceptionMapper.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response toResponse(Throwable e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(Util.createJsonExceptionMessage(e)).build();
    }
}
