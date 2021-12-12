package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.impl.ProformaExternalSubmissionHandle;
import proforma.xml21.ExternalSubmissionType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements  
 * of the <code>external-submission-type</code>.
 */
public class Proforma21ExternalSubmissionHandle extends ProformaExternalSubmissionHandle {
	public Proforma21ExternalSubmissionHandle(Object submission, String propertyName) {
		super(submission, propertyName, ExternalSubmissionType.class);
	}
	
	@Override
	public ExternalSubmissionType get() {
		return (ExternalSubmissionType) super.get();
		
	}
	@Override
	public Proforma21ExternalSubmissionHandle setUri(String value) {
		assertNotNull("set uri");
		get().setUri(value);
		return this;
	}
	@Override
	public String getUri() {
		assertNotNull("get uri");
		return get().getUri();
	}
}
