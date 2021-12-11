package de.hsh.grappa.proforma21;


import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionFileHandle;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml21.SubmissionFileType;

public class Proforma21SubmissionFileHandle extends ProformaSubmissionFileHandle {
	
	private Proforma21EmbeddedTxtFileHandle embeddedTxtFileHandle;
	private Proforma21AttachedTxtFileHandle attachedTxtFileHandle;
	private Proforma21EmbeddedBinFileHandle embeddedBinFileHandle;
	private Proforma21AttachedBinFileHandle attachedBinFileHandle;
	

	
	public Proforma21SubmissionFileHandle(SubmissionFileType file, ZipContent zipContent) {
		super(file, zipContent);
		this.embeddedTxtFileHandle = new Proforma21EmbeddedTxtFileHandle(file, "embeddedTxtFile");
		this.attachedTxtFileHandle = new Proforma21AttachedTxtFileHandle(file, "attachedTxtFile");
		this.embeddedBinFileHandle = new Proforma21EmbeddedBinFileHandle(file, "embeddedBinFile");
		this.attachedBinFileHandle = new Proforma21AttachedBinFileHandle(file, "attachedBinFile");
	}
	
	
	@Override
	public Proforma21EmbeddedTxtFileHandle embeddedTxtFileHandle() {
		return embeddedTxtFileHandle;
	}
	
	@Override
	public Proforma21AttachedTxtFileHandle attachedTxtFileHandle() {
		return attachedTxtFileHandle;
	}
	
	@Override
	public Proforma21EmbeddedBinFileHandle embeddedBinFileHandle() {
		return embeddedBinFileHandle;
	}
	
	@Override
	public Proforma21AttachedBinFileHandle attachedBinFileHandle() {
		return attachedBinFileHandle;
	}
}