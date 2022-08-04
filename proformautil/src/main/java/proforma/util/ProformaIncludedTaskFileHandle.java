package proforma.util;

import proforma.util.div.PropertyHandle;

/**
 * This is an abstraction of an included-task-file element as part of a submission.
 * It is independent of a specific ProFormA version.
 * The type of {@code submission} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaIncludedTaskFileHandle {

    private PropertyHandle propertyHandle;

    public ProformaIncludedTaskFileHandle(Object submission, String propertyName, Class<?> clazz) {
        propertyHandle = new PropertyHandle(submission, propertyName, clazz);
        if (submission == null) throw new AssertionError(this.getClass() + ": submission shouldn't be null");
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

    public ProformaIncludedTaskFileHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }


    public abstract ProformaAttachedBinFileHandle attachedZipFileHandle();

    public abstract ProformaAttachedTxtFileHandle attachedXmlFileHandle();

    public abstract ProformaEmbeddedBinFileHandle embeddedZipFileHandle();

    public abstract ProformaEmbeddedBinFileHandle embeddedXmlFileHandle();

}
