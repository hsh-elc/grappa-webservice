package de.hsh.grappa.boundary;

import de.hsh.grappa.cache.RedisController;
import proforma.util.boundary.Boundary;
import proforma.util.boundary.ResourceDownloader;
import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.exception.NotFoundException;
import proforma.util.exception.UnexpectedDataException;
import proforma.util.resource.SubmissionResource;
import proforma.util.resource.TaskResource;

import java.io.IOException;
import java.net.MalformedURLException;

public class BoundaryImpl implements Boundary {

    @Override
    public TaskResource getCachedTask(String taskUuid) throws NotFoundException, UnexpectedDataException {
        return RedisController.getInstance().getCachedTask(taskUuid);
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
    public Resource downloadResource(String url) throws MalformedURLException, UnexpectedDataException, IOException {
        return new ResourceDownloader().downloadResource(url);
    }

}
