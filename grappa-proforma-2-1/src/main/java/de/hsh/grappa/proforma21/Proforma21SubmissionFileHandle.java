package de.hsh.grappa.proforma21;


import de.hsh.grappa.common.util.proforma.impl.ProformaSubmissionFileHandle;
import de.hsh.grappa.common.util.proforma.impl.ProformaTaskFileHandle;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml21.SubmissionFileType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements for 
 * attached and embedded files of the <code>submission-file-type</code>.
 * For usage examples see {@link ProformaTaskFileHandle}.
 */
public class Proforma21SubmissionFileHandle extends ProformaSubmissionFileHandle {
	
	private Proforma21EmbeddedTxtFileHandle embeddedTxtFileHandle;
	private Proforma21AttachedTxtFileHandle attachedTxtFileHandle;
	private Proforma21EmbeddedBinFileHandle embeddedBinFileHandle;
	private Proforma21AttachedBinFileHandle attachedBinFileHandle;
	

	
	public Proforma21SubmissionFileHandle(Object file, ZipContent zipContent) {
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