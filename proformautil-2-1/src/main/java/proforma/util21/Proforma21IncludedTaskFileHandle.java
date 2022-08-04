package proforma.util21;

import proforma.util.ProformaIncludedTaskFileHandle;
import proforma.xml21.IncludedTaskFileType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements for
 * attached and embedded zip or xml files of the <code>included-task-file-type</code>.
 */
public class Proforma21IncludedTaskFileHandle extends ProformaIncludedTaskFileHandle {

    private Proforma21EmbeddedBinFileHandle embeddedZipFileHandle;
    private Proforma21AttachedTxtFileHandle attachedXmlFileHandle;
    private Proforma21EmbeddedBinFileHandle embeddedXmlFileHandle;
    private Proforma21AttachedBinFileHandle attachedZipFileHandle;

    public Proforma21IncludedTaskFileHandle(Object submission, String propertyName) {
        super(submission, propertyName, IncludedTaskFileType.class);
    }

    @Override
    public IncludedTaskFileType get() {
        return (IncludedTaskFileType) super.get();
    }

    @Override
    public Proforma21AttachedBinFileHandle attachedZipFileHandle() {
        assertNotNull("get attachedZipFile");
        if (attachedZipFileHandle == null) {
            attachedZipFileHandle = new Proforma21AttachedBinFileHandle(get(), "attachedZipFile");
        }
        return attachedZipFileHandle;
    }

    @Override
    public Proforma21AttachedTxtFileHandle attachedXmlFileHandle() {
        assertNotNull("get attachedXmlFile");
        if (attachedXmlFileHandle == null) {
            attachedXmlFileHandle = new Proforma21AttachedTxtFileHandle(get(), "attachedXmlFile");
        }
        return attachedXmlFileHandle;
    }

    @Override
    public Proforma21EmbeddedBinFileHandle embeddedZipFileHandle() {
        assertNotNull("get embeddedZipFile");
        if (embeddedZipFileHandle == null) {
            embeddedZipFileHandle = new Proforma21EmbeddedBinFileHandle(get(), "embeddedZipFile");
        }
        return embeddedZipFileHandle;
    }

    @Override
    public Proforma21EmbeddedBinFileHandle embeddedXmlFileHandle() {
        assertNotNull("get embeddedXmlFile");
        if (embeddedXmlFileHandle == null) {
            embeddedXmlFileHandle = new Proforma21EmbeddedBinFileHandle(get(), "embeddedXmlFile");
        }
        return embeddedXmlFileHandle;
    }

}
