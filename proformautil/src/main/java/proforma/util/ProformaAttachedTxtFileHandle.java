package proforma.util;

import proforma.util.div.PropertyHandle;

/**
 * This is an abstraction of an embedded binary file, independent of a specific ProFormA version.
 * The type of {@code file} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaAttachedTxtFileHandle implements ProformaAttachedOrEmbeddedBonOrTxtFileHandle {

    private PropertyHandle propertyHandle;

    public ProformaAttachedTxtFileHandle(Object file, String propertyName, Class<?> clazz) {
        this.propertyHandle = new PropertyHandle(file, propertyName, clazz);
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


    public ProformaAttachedTxtFileHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }


    /**
     * @return the path in the zip file
     */
    public abstract String getPath();

    /**
     * @return the character encoding
     */
    public abstract String getEncoding();

    /**
     * @return the natural language
     */
    public abstract String getNaturalLang();

    /**
     * Sets the path in the zip file
     *
     * @param path
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaAttachedTxtFileHandle setPath(String path);

    /**
     * Sets the character encoding
     *
     * @param encoding
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaAttachedTxtFileHandle setEncoding(String encoding);

    /**
     * Sets the natural language
     *
     * @param naturalLang
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaAttachedTxtFileHandle setNaturalLang(String naturalLang);
}
