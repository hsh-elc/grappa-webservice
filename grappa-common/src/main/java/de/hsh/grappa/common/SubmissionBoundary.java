package de.hsh.grappa.common;

import java.io.IOException;
import java.net.MalformedURLException;

import de.hsh.grappa.exceptions.GrappaException;

public interface SubmissionBoundary {
	/**
	 * Load the submission from an internet resource
	 * @param submissionRepoUrl
	 * @return
	 * @throws GrappaException  if the url denotes a resource of a mime type different from all the
	 *                   mime types declared in {@link MimeType}
	 * @throws MalformedURLException in case of a malformed url
	 * @throws IOException in case of other IO errors.
	 */
	SubmissionResource downloadSubmission(String submissionRepoUrl) throws MalformedURLException, IOException, GrappaException;
}
