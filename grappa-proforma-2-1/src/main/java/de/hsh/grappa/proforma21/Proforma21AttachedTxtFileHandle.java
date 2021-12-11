package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaAttachedTxtFileHandle;
import proforma.xml21.AttachedTxtFileType;

class Proforma21AttachedTxtFileHandle extends ProformaAttachedTxtFileHandle {
	public Proforma21AttachedTxtFileHandle(Object file, String propertyName) {
		super(file, propertyName, AttachedTxtFileType.class);
	}
	
	@Override
	public AttachedTxtFileType get() {
		return (AttachedTxtFileType) super.get();
	}
	
	@Override
	public Proforma21AttachedTxtFileHandle setPath(String path) {
		if (get() == null) throw new NullPointerException("Cannot set path because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setValue(path);
		return this;
	}
	@Override
	public Proforma21AttachedTxtFileHandle setNaturalLang(String naturalLang) {
		if (get() == null) throw new NullPointerException("Cannot set naturallang because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setNaturalLang(naturalLang);
		return this;
	}
	@Override
	public Proforma21AttachedTxtFileHandle setEncoding(String encoding) {
		if (get() == null) throw new NullPointerException("Cannot set encoding because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setEncoding(encoding);
		return this;
	}
	@Override
	public String getPath() {
		if (get() == null) throw new NullPointerException("Cannot get path because "+this.getClass()+" embraces a null value");
		return get().getValue();
	}
	@Override
	public String getNaturalLang() {
		if (get() == null) throw new NullPointerException("Cannot get naturallang because "+this.getClass()+" embraces a null value");
		return get().getNaturalLang();
	}
	@Override
	public String getEncoding() {
		if (get() == null) throw new NullPointerException("Cannot get encoding because "+this.getClass()+" embraces a null value");
		return get().getEncoding();
	}
}