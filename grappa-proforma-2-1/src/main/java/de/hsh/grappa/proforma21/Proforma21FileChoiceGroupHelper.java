package de.hsh.grappa.proforma21;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import de.hsh.grappa.common.util.proforma.ProformaAttachedEmbeddedFileInfo;
import de.hsh.grappa.common.util.proforma.ProformaFileChoiceGroupHelper;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.Zip.ZipContentElement;
import proforma.xml.AbstractAttachedTxtFileType;
import proforma.xml.AbstractEmbeddedBinFileType;
import proforma.xml.AbstractEmbeddedTxtFileType;
import proforma.xml21.AttachedTxtFileType;
import proforma.xml21.EmbeddedBinFileType;
import proforma.xml21.EmbeddedTxtFileType;

public class Proforma21FileChoiceGroupHelper extends ProformaFileChoiceGroupHelper {
	
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
	@Override
    public ProformaAttachedEmbeddedFileInfo getFromFileChoiceGroup(
            String id,
            String mimetype,
            AbstractEmbeddedBinFileType embeddedBinFile,
            AbstractEmbeddedTxtFileType embeddedTxtFile,
            String attachedBinFile,
            AbstractAttachedTxtFileType attachedTxtFile,
            String optionalRootFolder,
            Map<String, ZipContentElement> zipContent) throws UnsupportedEncodingException {
        int cnt = 0;
        ProformaAttachedEmbeddedFileInfo result = null;
        if (embeddedBinFile != null) {
            cnt++;
            EmbeddedBinFileType ebf = (EmbeddedBinFileType)embeddedBinFile;
            result = new ProformaAttachedEmbeddedFileInfo(id, mimetype, ebf.getFilename(), ebf.getValue());
        }
        if (embeddedTxtFile != null) {
            cnt++;
            EmbeddedTxtFileType etf = (EmbeddedTxtFileType)embeddedTxtFile;
            result = new ProformaAttachedEmbeddedFileInfo(id, mimetype, etf.getFilename(), etf.getValue());
        }
        if (attachedBinFile != null) {
            cnt++;
            String path = concat(optionalRootFolder, attachedBinFile);
            ZipContentElement elem = zipContent.get(path);
            if (elem == null) {
                throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
            }
            result = new ProformaAttachedEmbeddedFileInfo(id, mimetype, path, elem.getBytes());
        }
        if (attachedTxtFile != null) {
            cnt++;
            AttachedTxtFileType atf = (AttachedTxtFileType)attachedTxtFile;
            String path = concat(optionalRootFolder, atf.getValue());
            ZipContentElement elem = zipContent.get(path);
            if (elem == null) {
                throw new IllegalArgumentException("the path '" + path + "' does not exist in the zip contents.");
            }
            String encoding = atf.getEncoding();
            if (Strings.isNullOrEmpty(encoding)) {
                // TODO: guess encoding from language and content.
                encoding = StandardCharsets.UTF_8.name();
            }
               result = new ProformaAttachedEmbeddedFileInfo(id, mimetype, path, new String(elem.getBytes(), encoding));
        }
        if (cnt != 1) {
            throw new IllegalArgumentException("file choice group should have a single file");
        }
        return result;
    }
    
    private String concat(String optionalRootFolder, String path) {
        if (Strings.isNullOrEmpty(optionalRootFolder)) return path;
        return optionalRootFolder + "/" + path;
    }

}
