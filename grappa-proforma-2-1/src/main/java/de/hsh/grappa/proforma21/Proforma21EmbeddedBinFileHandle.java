package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaEmbeddedBinFileHandle;
import proforma.xml21.EmbeddedBinFileType;

class Proforma21EmbeddedBinFileHandle extends ProformaEmbeddedBinFileHandle {
	public Proforma21EmbeddedBinFileHandle(Object file, String propertyName) {
		super(file, propertyName, EmbeddedBinFileType.class);
	}
	
	@Override
	public EmbeddedBinFileType get() {
		return (EmbeddedBinFileType) super.get();
	}
	
	@Override
	public Proforma21EmbeddedBinFileHandle setFilename(String filename) {
		if (get() == null) throw new NullPointerException("Cannot set filename because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setFilename(filename);
		return this;
	}
	@Override
	public Proforma21EmbeddedBinFileHandle setContent(byte[] content) {
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
	public byte[] getContent() {
		if (get() == null) throw new NullPointerException("Cannot get content because "+this.getClass()+" embraces a null value");
		return get().getValue(); 
	}
}