package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaExternalTaskHandle;
import proforma.xml21.ExternalTaskType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements  
 * of the <code>external-task-type</code>.
 */
public class Proforma21ExternalTaskHandle extends ProformaExternalTaskHandle {
	public Proforma21ExternalTaskHandle(Object submission, String propertyName) {
		super(submission, propertyName, ExternalTaskType.class);
	}
	
	@Override
	public ExternalTaskType get() {
		return (ExternalTaskType) super.get();
		
	}
	@Override
	public Proforma21ExternalTaskHandle setUuid(String value) {
		if (get() == null) throw new NullPointerException("Cannot set uuid because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setUuid(value);
		return this;
	}
	@Override
	public Proforma21ExternalTaskHandle setUri(String value) {
		if (get() == null) throw new NullPointerException("Cannot set uri because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setUri(value);
		return this;
	}
	@Override
	public String getUuid() {
		if (get() == null) throw new NullPointerException("Cannot get uuid because "+this.getClass()+" embraces a null value");
		return get().getUuid();
	}
	@Override
	public String getUri() {
		if (get() == null) throw new NullPointerException("Cannot get uri because "+this.getClass()+" embraces a null value");
		return get().getUri();
	}
}
