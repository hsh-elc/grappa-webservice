package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.impl.ProformaResponseHelper;
import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionHelper;
import de.hsh.grappa.common.util.proforma.impl.ProformaTaskHelper;

public class Proforma21Version extends ProformaVersion {

	
	private Proforma21TaskHelper th;
	private Proforma21SubmissionHelper sh;
	private Proforma21ResponseHelper rh;
	
	public Proforma21Version() {
		th = new Proforma21TaskHelper(this);
		sh = new Proforma21SubmissionHelper(this);
		rh = new Proforma21ResponseHelper(this);
		
	}
	
	@Override
	public ProformaTaskHelper getTaskHelper() {
		return th;
	}

	@Override
	public ProformaSubmissionHelper getSubmissionHelper() {
		return sh;
	}

	@Override
	public ProformaResponseHelper getResponseHelper() {
		return rh;
	}

	@Override 
	public String getVersionNumber() {
		return "2.1";
	}
	
}
