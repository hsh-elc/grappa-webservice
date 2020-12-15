package de.hsh.grappa.rest;

import de.hsh.grappa.utils.Json;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.FileNotFoundException;

@Provider
public class FileNotFoundExceptionMapper implements
    ExceptionMapper<FileNotFoundException> {
    private static final Logger log = LoggerFactory.getLogger(FileNotFoundExceptionMapper.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response toResponse(FileNotFoundException e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.BAD_REQUEST)
            .entity(Json.createJsonExceptionMessage(e)).build();
    }
}
