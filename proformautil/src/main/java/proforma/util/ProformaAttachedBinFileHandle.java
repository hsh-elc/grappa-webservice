package proforma.util;

import proforma.util.div.PropertyHandle;

/**
 * This is an abstraction of an attached binary file, independent of a specific ProFormA version.
 * The type of {@code file} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaAttachedBinFileHandle implements ProformaAttachedOrEmbeddedBonOrTxtFileHandle {

    private PropertyHandle propertyHandle;

    public ProformaAttachedBinFileHandle(Object file, String propertyName, Class<?> clazz) {
        propertyHandle = new PropertyHandle(file, propertyName, clazz);
        if (file == null) throw new AssertionError(this.getClass() + ": file shouldn't be null");
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

    public ProformaAttachedBinFileHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }

    /**
     * Sets the path
     *
     * @param path
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaAttachedBinFileHandle setPath(String path);

    /**
     * @return the path inside the zip file
     */
    public abstract String getPath();


}
