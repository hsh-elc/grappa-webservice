package de.hsh.grappa;

import java.io.IOException;
import java.net.MalformedURLException;

import de.hsh.grappa.common.Boundary;
import de.hsh.grappa.common.ResourceDownloader.Resource;
import de.hsh.grappa.common.ResourceDownloader;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;

public class BackendStarterBoundaryImpl implements Boundary {

	@Override
	public TaskResource getCachedTask(String taskUuid) throws NotFoundException, GrappaException {
		throw new IllegalArgumentException("Currently Grappa does not support grading submissions referring external tasks by uuid in a docker container, since the Redis cache is not reachable from the container.");
	}

	@Override
	public TaskResource downloadTask(String taskRepoUrl) throws MalformedURLException, IOException, GrappaException {
		return new ResourceDownloader().downloadTaskResource(taskRepoUrl);
	}

	@Override
	public SubmissionResource downloadSubmission(String submissionRepoUrl) throws MalformedURLException, IOException, GrappaException {
		return new ResourceDownloader().downloadSubmissionResource(submissionRepoUrl);
	}

	@Override
	public Resource downloadResource(String anyUrl) throws MalformedURLException, IOException, Exception {
		return new ResourceDownloader().downloadResource(anyUrl);
	}

}
