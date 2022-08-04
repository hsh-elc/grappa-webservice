package proforma.util;

import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.boundary.SubmissionBoundary;
import proforma.util.div.PropertyHandle;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * This is an abstraction of an external-submission element as part of a submission.
 * It is independent of a specific ProFormA version.
 * The type of {@code submission} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaExternalSubmissionHandle {
    private PropertyHandle propertyHandle;
    private SubmissionBoundary submissionBoundary;

    public ProformaExternalSubmissionHandle(Object submission, String propertyName, Class<?> clazz) {
        propertyHandle = new PropertyHandle(submission, propertyName, clazz);
        if (submission == null) throw new AssertionError(this.getClass() + ": submission shouldn't be null");
    }

    public void setBoundary(SubmissionBoundary sb) {
        this.submissionBoundary = sb;
    }

    public SubmissionBoundary getBoundary() {
        return submissionBoundary;
    }

    protected void assertNotNull(String whatToDo) throws NullPointerException {
        propertyHandle.assertNotNull(whatToDo, this);
    }


    public Object get() {
        return propertyHandle.get();
    }

    public void set(Object value) {
        propertyHandle.set(value);
    }

    public ProformaExternalSubmissionHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }

    /**
     * @return the URI of the external submission.
     */
    public abstract String getUri();

    /**
     * Sets the uri
     *
     * @param uri
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaExternalSubmissionHandle setUri(String value);


    public Resource download() throws MalformedURLException, IOException, Exception {
        String uri = getUri();
        Resource resource = getBoundary().downloadResource(uri);
        return resource;
    }

}
