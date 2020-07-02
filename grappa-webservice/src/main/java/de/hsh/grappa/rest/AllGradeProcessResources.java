package de.hsh.grappa.rest;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaSubmission;
import de.hsh.grappa.service.SubmissionProcessor;
import de.hsh.grappa.utils.InputStreamCopy;
import de.hsh.grappa.utils.Json;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileOutputStream;
import java.io.InputStream;

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
                          @DefaultValue("true") @QueryParam("async") boolean async,
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
            ProformaSubmission proformaSubm = new ProformaSubmission
                (IOUtils.toByteArray(submission), mimeType);
            log.info("[GraderId: {}] Processing submission: {}", graderId, proformaSubm);
            String gradeProcId = new SubmissionProcessor(proformaSubm, graderId).process();
            String gradeProcIdResponse = Json.createJsonKeyValueAsString("gradeProcessId", gradeProcId);
            return Response.status(Response.Status.CREATED).entity(gradeProcIdResponse).build();
            String gradeProcId = new SubmissionProcessor(proformaSubm, graderId).process(prioritize);
        }
        throw new de.hsh.grappa.exceptions.BadRequestException("Received grade request with unspecified content type.");
    }
}
