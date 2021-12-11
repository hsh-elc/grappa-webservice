package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaChildElementTaskHandle;
import proforma.xml21.TaskType;

public class Proforma21ChildElementTaskHandle extends ProformaChildElementTaskHandle {
	public Proforma21ChildElementTaskHandle(Object submission, String propertyName) {
		super(submission, propertyName, TaskType.class);
	}
	
	@Override
	public TaskType get() {
		return (TaskType) super.get();
	}
	
	@Override
	public Proforma21ChildElementTaskHandle setUuid(String value) {
		if (get() == null) throw new NullPointerException("Cannot set uuid because "+this.getClass()+" embraces a null value. You should call createAndSet() first.");
		get().setUuid(value);
		return this;
	}
	@Override
	public String getUuid() {
		if (get() == null) throw new NullPointerException("Cannot get uuid because "+this.getClass()+" embraces a null value");
		return get().getUuid();
	}
}
