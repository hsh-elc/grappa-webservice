package proforma.util21;


import proforma.util.ProformaTaskFileHandle;
import proforma.util.div.Zip.ZipContent;
import proforma.xml21.TaskFileType;

/**
 * This is a ProFormA 2.1 specific implementation of the child elements for
 * attached and embedded files of the <code>task-file-type</code>.
 * For usage examples see {@link ProformaTaskFileHandle}.
 */
public class Proforma21TaskFileHandle extends ProformaTaskFileHandle {

    private Proforma21EmbeddedTxtFileHandle embeddedTxtFileHandle;
    private Proforma21AttachedTxtFileHandle attachedTxtFileHandle;
    private Proforma21EmbeddedBinFileHandle embeddedBinFileHandle;
    private Proforma21AttachedBinFileHandle attachedBinFileHandle;


    public Proforma21TaskFileHandle(TaskFileType file, ZipContent zipContent) {
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