package de.hsh.grappa.common.util.proforma.impl;

/**
 * This is an abstraction of an embedded binary file, independent of a specific ProFormA version.
 * The type of {@code file} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor.
 */
public abstract class ProformaEmbeddedBinFileHandle  {
	private PropertyHandle propertyHandle;
	
	public ProformaEmbeddedBinFileHandle(Object file, String propertyName, Class<?> clazz) {
		propertyHandle = new PropertyHandle(file, propertyName, clazz);
		if (file == null) throw new AssertionError(this.getClass() + ": file shouldn't be null");
	}
	
	public Object get() {
		return propertyHandle.get();
	}

	public void set(Object value) {
		propertyHandle.set(value);
	}
	
	public ProformaEmbeddedBinFileHandle createAndSet() {
		propertyHandle.createAndSet();
		return this;
	}

	public void remove() {
		propertyHandle.set(null);
	}
	
	
	/**
	 * @return the filename
	 */
	public abstract String getFilename();
	
	/**
	 * @return the binary content
	 */
	public abstract byte[] getContent();
	
	/**
	 * Sets the filename
	 * @param filename
	 * @return can be used for method call chaining on this object.
	 */
	public abstract ProformaEmbeddedBinFileHandle setFilename(String filename);
	
	/**
	 * Sets the binary content
	 * @param content
	 * @return can be used for method call chaining on this object.
	 */
	public abstract ProformaEmbeddedBinFileHandle setContent(byte[] content);
}
