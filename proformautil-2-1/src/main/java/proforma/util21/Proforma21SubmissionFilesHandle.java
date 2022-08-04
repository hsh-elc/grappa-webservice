package proforma.util21;

import proforma.util.ProformaSubmissionFilesHandle;
import proforma.util.div.Zip.ZipContent;
import proforma.xml21.SubmissionFileType;
import proforma.xml21.SubmissionFilesType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements
 * of the <code>submission-files-type</code>.
 */
public class Proforma21SubmissionFilesHandle extends ProformaSubmissionFilesHandle {

    public Proforma21SubmissionFilesHandle(Object submission, ZipContent zipContent, String propertyName) {
        super(submission, zipContent, propertyName, SubmissionFilesType.class);
    }

    @Override
    public SubmissionFilesType get() {
        return (SubmissionFilesType) super.get();
    }

    @Override
    public Class<Proforma21SubmissionFileHandle> getElementHandleClass() {
        return Proforma21SubmissionFileHandle.class;
    }

    @Override
    public Class<?> getElementClass() {
        return SubmissionFileType.class;
    }
}
