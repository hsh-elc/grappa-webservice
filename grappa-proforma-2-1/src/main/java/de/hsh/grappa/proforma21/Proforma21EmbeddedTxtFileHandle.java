package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaEmbeddedTxtFileHandle;
import proforma.xml21.EmbeddedTxtFileType;

class Proforma21EmbeddedTxtFileHandle extends ProformaEmbeddedTxtFileHandle {
	public Proforma21EmbeddedTxtFileHandle(Object file, String propertyName) {
		super(file, propertyName, EmbeddedTxtFileType.class);
	}
	
	@Override
	public EmbeddedTxtFileType get() {
		return (EmbeddedTxtFileType) super.get();
	}
	
	@Override
	public Proforma21EmbeddedTxtFileHandle setFilename(String filename) {
		if (get() == null) throw new NullPointerException("Cannot set filename because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setFilename(filename);
		return this;
	}
	@Override
	public Proforma21EmbeddedTxtFileHandle setContent(String content) {
		if (get() == null) throw new NullPointerException("Cannot set content because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setValue(content);
		return this;
	}
	@Override
	public String getFilename() {
		if (get() == null) throw new NullPointerException("Cannot get filename because "+this.getClass()+" embraces a null value");
		return get().getFilename();
	}
	@Override
	public String getContent() {
		if (get() == null) throw new NullPointerException("Cannot get content because "+this.getClass()+" embraces a null value");
		return get().getValue();
	}
}