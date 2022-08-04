package proforma.util.boundary;

import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;

import java.io.IOException;
import java.net.MalformedURLException;

public interface SubmissionBoundary {
    /**
     * Load the submission from an internet resource
     *
     * @param submissionRepoUrl
     * @return
     * @throws Exception             if the url denotes a resource of a mime type different from all the
     *                               mime types declared in {@link MimeType}
     * @throws MalformedURLException in case of a malformed url
     * @throws IOException           in case of other IO errors.
     */
    SubmissionResource downloadSubmission(String submissionRepoUrl) throws MalformedURLException, IOException, Exception;


    /**
     * Load submitted file from an internet resource.
     * Currently Grappa does not support {@code anyUrl} denoting a resolvable list of files, such as a web directory. Only
     * a single file can be downloaded.
     *
     * @param anyUrl
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws Exception
     */
    Resource downloadResource(String anyUrl) throws MalformedURLException, IOException, Exception;
}
