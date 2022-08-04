package proforma.util;

import proforma.util.div.PropertyHandle;

/**
 * This is an abstraction of a child &lt;task&gt; element as part of a submission.
 * It is independent of a specific ProFormA version.
 * The type of {@code submission} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaChildElementTaskHandle {
    private PropertyHandle propertyHandle;

    public ProformaChildElementTaskHandle(Object submission, String propertyName, Class<?> clazz) {
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

    public ProformaChildElementTaskHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }

    /**
     * @return uuid
     */
    public abstract String getUuid();

    /**
     * Sets the uuid
     *
     * @param uuid
     * @return can be used for method call chaining on this object.
     */
    public abstract ProformaChildElementTaskHandle setUuid(String value);


}
