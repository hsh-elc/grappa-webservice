package proforma.util;

import proforma.util.boundary.SubmissionBoundary;
import proforma.util.boundary.TaskBoundary;
import proforma.util.div.XmlUtils.MarshalOption;
import proforma.util.div.Zip.ZipContent;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;
import proforma.xml.AbstractSubmissionType;

/**
 * <p>Helper class to represent a ProFormA submission in memory. This could be either a representation of
 * a XML or a ZIP file. </p>
 *
 * <p>For usage scenarios see {@link ProformaLiveObject}.</p>
 */
public class SubmissionLive extends ProformaLiveObject<SubmissionResource, AbstractSubmissionType> {

    /**
     * Creates an in-memory representation of a given submission resource
     *
     * @param submissionResource The given resource
     * @throws Exception
     */
    public SubmissionLive(SubmissionResource submissionResource, Class<?>... contextClasses) throws Exception {
        super(submissionResource, contextClasses);
    }


    /**
     * Creates an object from an in-memory representation and augments this with additional
     * data to be written to a ZIP or XML file when serialized.
     *
     * @param mimeType must be ZIP, if {@code otherZipContentExceptMainXmlFile} is not empty.
     */
    public SubmissionLive(AbstractSubmissionType subm, ZipContent otherZipContentExceptMainXmlFile, MimeType mimeType, MarshalOption[] marshalOptions, Class<?>... contextClasses) throws Exception {
        super(subm, otherZipContentExceptMainXmlFile, mimeType, marshalOptions, contextClasses);
    }


    /**
     * @return the string "submission"
     */
    @Override
    public String displayName() {
        return "submission";
    }

    /**
     * @return a pojo deserialized from the submission.xml file. This pojo can be modified and stored later
     * on by calling {@link #markPojoChanged(MarshalOption[], Class...)}.
     * @throws Exception
     */
    public <T extends AbstractSubmissionType> T getSubmission() throws Exception {
        return super.getPojo(getProformaVersion().getSubmissionHelper().getPojoType());
    }

    /**
     * @return the original resource or the new resource created by {@link #toResource(Class...)}.
     * @throws Exception
     */
    @Override
    public SubmissionResource getResource() throws Exception {
        return (SubmissionResource) super.getResource();
    }


    @Override
    protected Class<SubmissionResource> getResourceType() {
        return SubmissionResource.class;
    }

    @Override
    public String getMainXmlFileName() {
        return ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME;
    }

    public TaskLive getTask(TaskBoundary tb) throws Exception {
        return getSubmissionTaskHandle(tb).getTask();
    }


    public String getSubmissionId() throws Exception {
        return getProformaVersion().getSubmissionHelper().getSubmissionId(getSubmission());
    }

    /**
     * The returned object provides service routines to process the task
     * as part of a submission.
     *
     * @param tb the task boundary is used to resolve external elements in the task. It can be null,
     *           if there are no external elements.
     * @return a handle object.
     * @throws Exception
     */
    public ProformaSubmissionTaskHandle getSubmissionTaskHandle(TaskBoundary tb) throws Exception {
        return getProformaVersion().getSubmissionHelper().getSubmissionTaskHandle(this, tb);
    }


    /**
     * The returned object provides service routines to process the task
     * as part of a submission.
     *
     * @param sb the submission boundary is used to resolve external elements in the submission. It can be null,
     *           if there are no external elements.
     * @return a handle object.
     * @throws Exception
     */
    public ProformaSubmissionSubmissionHandle getSubmissionSubmissionHandle(SubmissionBoundary sb) throws Exception {
        return getProformaVersion().getSubmissionHelper().getSubmissionSubmissionHandle(this, sb);
    }

//	/**
//	 * @return true, if the submission has files instead of an external submission.
//	 * @throws Exception
//	 */
//	public boolean hasSubmissionFiles() throws Exception {
//		return getProformaVersion().getSubmissionHelper().hasSubmissionFiles(getSubmission());
//	}

//	/**
//	 * @return a list of handles, one for each submitted file. A file handle provides service routines
//	 *     for further processing of each embedded or attached file.
//	 * @throws Exception
//	 */
//	public List<? extends ProformaSubmissionFileHandle> getSubmissionFileHandles() throws Exception {
//		return getProformaVersion().getSubmissionHelper().getSubmissionFileHandles(getSubmission(), getZipContent());
//	}

//	/**
//	 * @return true, if this is a submission from an external source.
//	 * @throws Exception
//	 */
//	public boolean hasExternalSubmission() throws Exception {
//		return getProformaVersion().getSubmissionHelper().hasExternalSubmission(getSubmission());
//	}
//	
//	/**
//	 * @return the URI of the external submission.
//	 * @throws Exception
//	 */
//	public String getExternalSubmissionUri() throws Exception {
//		return getProformaVersion().getSubmissionHelper().getExternalSubmissionUri(getSubmission());
//	}
//	

}
