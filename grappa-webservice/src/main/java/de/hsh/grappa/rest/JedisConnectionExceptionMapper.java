package de.hsh.grappa.rest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * This class is for responding with HTTP 503 to clients
 * when redis is unavailable.
 *
 * This is an implementation dependent exception mapper
 * This class specifically maps a jedis client
 * implementation detail. However, if Grappa were to use
 * letuce or any other redis client implementation, this
 * exception mapper class would need adjustments.
 */
@Provider
public class JedisConnectionExceptionMapper implements
    ExceptionMapper<JedisConnectionException> {
    private static final Logger log = LoggerFactory.getLogger(JedisConnectionExceptionMapper.class);

    @Override
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response toResponse(JedisConnectionException e) {
        log.error(e.getMessage());
        log.error(ExceptionUtils.getStackTrace(e));
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
            .entity(Util.createJsonExceptionMessage(e)).build();
    }
}
