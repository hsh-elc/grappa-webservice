package de.hsh.grappa.boundary;

import java.io.IOException;
import java.net.MalformedURLException;

import de.hsh.grappa.cache.RedisController;
import de.hsh.grappa.common.Boundary;
import de.hsh.grappa.common.SubmissionResource;
import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;

public class BoundaryImpl implements Boundary {

	@Override
	public TaskResource getCachedTask(String taskUuid) throws NotFoundException, GrappaException {
		return RedisController.getInstance().getCachedTask(taskUuid);
	}

	@Override
	public TaskResource downloadTask(String taskRepoUrl) throws MalformedURLException, IOException, GrappaException {
		return new ResourceDownloader().downloadTaskResource(taskRepoUrl);
	}

	@Override
	public SubmissionResource downloadSubmission(String submissionRepoUrl) throws MalformedURLException, IOException, GrappaException {
		return new ResourceDownloader().downloadSubmissionResource(submissionRepoUrl);
	}

}
