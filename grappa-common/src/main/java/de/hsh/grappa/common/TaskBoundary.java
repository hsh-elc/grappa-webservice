package de.hsh.grappa.common;

import java.io.IOException;
import java.net.MalformedURLException;

import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;


public interface TaskBoundary {
    /**
     * Load the task from a local cache like Redis
     * @param taskUuid
     * @return Never returns null
     * @throws NotFoundException if the task is not in the cache
     * @throws GrappaException if the task can not be restored
     */
    TaskResource getCachedTask(String taskUuid) throws NotFoundException, GrappaException;
    
    /**
     * Load the task from an internet resource
     * @param taskRepoUrl
     * @return
     * @throws GrappaException  if the url denotes a resource of a mime type different from all the
     *                   mime types declared in {@link MimeType}
     * @throws MalformedURLException in case of a malformed url
     * @throws IOException in case of other IO errors.
     */
    TaskResource downloadTask(String taskRepoUrl) throws MalformedURLException, IOException, GrappaException;
}
