package de.hsh.grappa.common.util.proforma;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractAttachedTxtFileType;
import proforma.xml.AbstractEmbeddedBinFileType;
import proforma.xml.AbstractEmbeddedTxtFileType;

public abstract class ProformaFileChoiceGroupHelper extends ProformaHelper {
	/**
     * Read information about a file that is represented by a ProFormA file choice group.
     * Only one of the four file parameters is allowed. The other three must be null.
     * @param id optional
     * @param mimetype optional
     * @param embeddedBinFile
     * @param embeddedTxtFile
     * @param attachedBinFile
     * @param attachedTxtFile
     * @param optionalRootFolder This folder is prepended to any path spec in any of the attached... elements.
     * @return
     * @throws UnsupportedEncodingException
     */
    public abstract ProformaAttachedEmbeddedFileInfo getFromFileChoiceGroup(
            String id,
            String mimetype,
            AbstractEmbeddedBinFileType embeddedBinFile,
            AbstractEmbeddedTxtFileType embeddedTxtFile,
            String attachedBinFile,
            AbstractAttachedTxtFileType attachedTxtFile,
            String optionalRootFolder,
            Map<String, ZipContentElement> zipContent) throws UnsupportedEncodingException ;
    
    
	
	static {
		tryRegister("2.1", ProformaFileChoiceGroupHelper.class, "de.hsh.grappa.proforma21.Proforma21FileChoiceGroupHelper");
	}
	
    public static ProformaFileChoiceGroupHelper getInstance(String proformaVersion) {
    	return ProformaHelper.getInstance(proformaVersion, ProformaFileChoiceGroupHelper.class);
    }

}
