package proforma.util.boundary;

import proforma.util.exception.NotFoundException;
import proforma.util.exception.UnexpectedDataException;
import proforma.util.resource.MimeType;
import proforma.util.resource.TaskResource;

import java.io.IOException;
import java.net.MalformedURLException;


public interface TaskBoundary {
    /**
     * Load the task from a local cache like Redis
     *
     * @param taskUuid
     * @return Never returns null
     * @throws NotFoundException       if the task is not in the cache
     * @throws UnexpectedDataException if the task can not be restored
     */
    TaskResource getCachedTask(String taskUuid) throws NotFoundException, UnexpectedDataException;

    /**
     * Load the task from an internet resource
     *
     * @param taskRepoUrl
     * @return
     * @throws MalformedURLException   in case of a malformed url
     * @throws UnexpectedDataException if the url denotes a resource of a mime type different from all the
     *                                 mime types declared in {@link MimeType}
     * @throws IOException             in case of other IO errors.
     */
    TaskResource downloadTask(String taskRepoUrl) throws MalformedURLException, UnexpectedDataException, IOException;
}
