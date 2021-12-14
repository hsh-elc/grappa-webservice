package de.hsh.grappa.backendstarter;

import java.io.IOException;
import java.net.MalformedURLException;

import proforma.util.boundary.Boundary;
import proforma.util.boundary.ResourceDownloader;
import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.exception.NotFoundException;
import proforma.util.exception.UnexpectedDataException;
import proforma.util.resource.SubmissionResource;
import proforma.util.resource.TaskResource;

public class BackendStarterBoundaryImpl implements Boundary {

	@Override
	public TaskResource getCachedTask(String taskUuid) throws NotFoundException, UnexpectedDataException {
		throw new IllegalArgumentException("Currently Grappa does not support grading submissions referring external tasks by uuid in a docker container, since the Redis cache is not reachable from the container.");
	}

	@Override
	public TaskResource downloadTask(String taskRepoUrl) throws MalformedURLException, UnexpectedDataException, IOException {
		return new ResourceDownloader().downloadTaskResource(taskRepoUrl);
	}

	@Override
	public SubmissionResource downloadSubmission(String submissionRepoUrl) throws MalformedURLException, UnexpectedDataException, IOException {
		return new ResourceDownloader().downloadSubmissionResource(submissionRepoUrl);
	}

	@Override
	public Resource downloadResource(String anyUrl) throws MalformedURLException, UnexpectedDataException, IOException {
		return new ResourceDownloader().downloadResource(anyUrl);
	}

}
