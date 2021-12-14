package de.hsh.grappa.rest;

import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.config.LmsConfig;
import de.hsh.grappa.service.GradePoller;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.service.SubmissionProcessor;
import de.hsh.grappa.util.Json;
import proforma.util.div.IOUtils;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

@Path("/{lmsId}/gradeprocesses")
public class AllGradeProcessResources {

    Logger log = LoggerFactory.getLogger(AllGradeProcessResources.class);

    @Context
    ContainerRequestContext sr;
    @Context
    ServletContext sc;

    @Context
    public void setServletContext(ServletContext context) {
        this.sc = context;
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    @Produces(MediaType.APPLICATION_JSON + "; charset=utf-8")
    public Response grade(InputStream submission, @QueryParam("graderId") String graderId,
                          @DefaultValue("true") @QueryParam("async") String async,
                          @DefaultValue("false") @QueryParam("prioritize") boolean prioritize,
                          @Context HttpHeaders headers) throws Exception {
        log.debug("[GraderId: '{}']: grade() with async={} called.", graderId, async);
        MediaType contentType = headers.getMediaType();
        if (null != contentType) {
            MimeType mimeType = null;
            if (contentType.isCompatible(MediaType.APPLICATION_XML_TYPE))
                mimeType = MimeType.XML;
            else if (contentType.isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE))
                mimeType = MimeType.ZIP;
            else if (contentType.isCompatible(MediaType.MULTIPART_FORM_DATA_TYPE))
                mimeType = MimeType.ZIP;
            else
                throw new de.hsh.grappa.exceptions.BadRequestException("Received grade request with " +
                    "unsupported content type: " + contentType.toString());

            SubmissionResource proformaSubm = new SubmissionResource
                (IOUtils.toByteArray(submission), mimeType);
            log.info("[GraderId: {}] Processing submission: {}", graderId, proformaSubm);
            LmsConfig lmsConfig = null;
            try {
                lmsConfig = (LmsConfig) sr.getProperty("logged_user");
            } catch (Exception ex) {
                throw new de.hsh.grappa.exceptions.BadRequestException("Internal error. Cannot map grade request to LMS.");
            }
            String gradeProcId = new SubmissionProcessor(proformaSubm, graderId, lmsConfig).process(prioritize);

            if(Boolean.parseBoolean(async) || async.equals("1"))
                return replyWithTimeRemaining(gradeProcId);
            return replyWhenResponseIsAvailable(gradeProcId);
        }

        throw new de.hsh.grappa.exceptions.BadRequestException("Received grade request with unspecified content type.");
    }

    /**
     * Determines the estimated grading time remaining.
     * @param gradeProcId
     * @return Status 201 Created and estimated time remaining
     * @throws Exception
     */
    private Response replyWithTimeRemaining(String gradeProcId) throws Exception {
        int queuedSubmPos = RedisController.getInstance().getQueuedSubmissionIndex(gradeProcId);
        log.debug("subm to be graded in queue at pos {}", queuedSubmPos);
        long estimatedSecondsRemaining =
            GraderPoolManager.getInstance()
                .getEstimatedSecondsUntilGradeProcIdIsFinished(gradeProcId);
        String jsonResp = Json.createJsonKeyValueAsString(new String[][] {
            {"gradeProcessId", gradeProcId},
            {"estimatedSecondsRemaining", String.valueOf(estimatedSecondsRemaining)}
        });
        return Response.status(Response.Status.CREATED).entity(jsonResp).build();
    }

    /**
     * Blocks the calling thread until the submission has been graded or a timeout occurred.
     * @param gradeProcId
     * @return Status 200 OK and a valid proforma response.
     * @throws Exception
     */
    private Response replyWhenResponseIsAvailable(String gradeProcId) throws Exception {
        log.debug("Grading submission synchronously...");

        ResponseResource respBlob = null;

        try {
            respBlob = new GradePoller(gradeProcId).poll();
        } catch (TimeoutException e) {
            // Waiting timed out. Fall back to 202 Accepted and time remaining
            // so that the client may poll at a later time.
            return replyWithTimeRemaining(gradeProcId);
        }

        String responseFileName = "response." +  (respBlob.getMimeType()
            .equals(MimeType.XML) ? "xml" : "zip");
        MediaType mediaType = respBlob.getMimeType().equals(MimeType.XML)
            ? MediaType.APPLICATION_XML_TYPE : MediaType.APPLICATION_OCTET_STREAM_TYPE;
        Response.ResponseBuilder resp =
            Response.status(Response.Status.OK)
                .header("content-disposition","attachment; filename = " + responseFileName)
                .entity(respBlob.getContent());

        return resp.type(mediaType).build();
    }
}
