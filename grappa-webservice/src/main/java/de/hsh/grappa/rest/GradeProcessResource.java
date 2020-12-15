
package de.hsh.grappa.rest;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.service.GraderPoolManager;
import de.hsh.grappa.utils.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/{lmsId}/gradeprocesses/{gradeProcessId}")
public class GradeProcessResource {

    private static final Logger log = LoggerFactory.getLogger(GradeProcessResource.class);

    @Context
    ContainerRequestContext sr;

    @Context
    ServletContext sc;

    public GradeProcessResource() {
    }

    @Context
    public void setServletContext(ServletContext context) {
        this.sc = context;
    }

    @GET
//    @Produces({
//        MediaType.MULTIPART_FORM_DATA, 
//        MediaType.APPLICATION_OCTET_STREAM,
//        MediaType.APPLICATION_JSON + "; charset=utf-8"
//    })
    public Response poll(@PathParam("gradeProcessId") String gradeProcessId, @Context HttpHeaders headers)
        throws Exception {
        log.debug("[GradeProcId: '{}']: poll() called.", gradeProcessId);

        int queuedSubmPos = -1;
        long avgGradingSeconds = RedisController.getInstance().getSubmissionAverageGradingDurationSeconds
            (gradeProcessId, GrappaServlet.CONFIG.getService()
                .getDefault_estimated_grading_seconds());
        
        // Check if the submission has been graded and if a response is available result
        ResponseResource responseResource = RedisController.getInstance().getResponse(gradeProcessId);
        if (null != responseResource) {
            log.debug("[GradeProcId: '{}']: ProformaResponse file is available.", gradeProcessId);
            String responseFileName = "response." +  (responseResource.getMimeType()
                .equals(MimeType.XML) ? "xml" : "zip");

            Response.ResponseBuilder resp =
                Response.status(Response.Status.OK)
                    .header("content-disposition","attachment; filename = " + responseFileName)
                    .entity(responseResource.getContent());

            var acceptableTypes = headers.getAcceptableMediaTypes();
            if (acceptableTypes.stream().anyMatch(mt -> mt.isCompatible(MediaType.APPLICATION_OCTET_STREAM_TYPE))) {
                log.debug("[GradeProcId: '{}']: Returning ProformaResponse as APPLICATION_OCTET_STREAM.",
                    gradeProcessId);
                return resp.type(MediaType.APPLICATION_OCTET_STREAM).build();
            } else { // for everything else
                log.debug("[GradeProcId: '{}']: Returning ProformaResponse as MULTIPART_FORM_DATA.",
                    gradeProcessId);
                return resp.type(MediaType.MULTIPART_FORM_DATA).build();
            }
        } else if (-1 != (queuedSubmPos = RedisController.getInstance().getQueuedSubmissionIndex(gradeProcessId))) {
            log.debug("[GradeProcId: '{}']: Submission is still queued at position {}.",
                gradeProcessId, queuedSubmPos);
            //String gradeProcIdResponse = Json.createJsonKeyValueAsString("gradeProcessId", gradeProcId);
            long estimatedSecondsRemaining =
                GraderPoolManager.getInstance()
                    .getEstimatedSecondsUntilGradeProcIdIsFinished(gradeProcessId);
            String jsonResp = Json.createJsonKeyValueAsString(new String[][] {
                {"estimatedSecondsRemaining", String.valueOf(estimatedSecondsRemaining)}
            });
            return Response.status(Response.Status.ACCEPTED).entity(jsonResp)
                .type(MediaType.APPLICATION_JSON + "; charset=utf-8").build();
        }
        else if (GraderPoolManager.getInstance().isGradeProcIdBeingGradedRightNow(gradeProcessId)) {
            log.debug("[GradeProcId: '{}']: Submission is being graded right now.", gradeProcessId);
            String jsonResp = Json.createJsonKeyValueAsString(new String[][] {
                {"estimatedGradingSeconds", String.valueOf(avgGradingSeconds)}
            });
            return Response.status(Response.Status.ACCEPTED).entity(jsonResp)
                .type(MediaType.APPLICATION_JSON + "; charset=utf-8").build();
        }
        throw new de.hsh.grappa.exceptions.NotFoundException(String.format("gradeProcessId '%s' was neither found in " +
            "the submission queue nor in an active grading process.", gradeProcessId));
    }

    @DELETE
    public Response cancel(@PathParam("gradeProcessId") String gradeProcessId) throws Exception {
        log.debug("[GradeProcId: '{}']: cancel() called.", gradeProcessId);
        // If the submission is still queued, remove it
        if(RedisController.getInstance().removeSubmission(gradeProcessId))  {
            log.info("[GradeProcId: '{}']: Queued submission removed.", gradeProcessId);
            return Response.status(Response.Status.OK).build();
        } else { // Submission might be in mid-grading process
            log.debug("[GradeProcId: '{}']: Submission not in queue, checking active grading processes.",
                gradeProcessId);
            if (GraderPoolManager.getInstance().cancelGradingProcess(gradeProcessId)) {
                log.info("[GradeProcId: '{}']: Active grading process cancelled.", gradeProcessId);
                return Response.status(Response.Status.OK).build();
            }
        }
        throw new de.hsh.grappa.exceptions.NotFoundException(String.format("gradeProcessId '%s' was neither found in " +
            "the submission queue nor in an active grading process.", gradeProcessId));
    }
}