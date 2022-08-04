package proforma.util;

import proforma.util.boundary.ResourceDownloader.Resource;
import proforma.util.boundary.SubmissionBoundary;
import proforma.util.div.FilenameUtils;
import proforma.util.div.Strings;
import proforma.util.div.Zip;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;

/**
 * <p>An instance of a subclass can interact with submitted files or external resources
 * associated with or
 * part of a ProFormA submission.</p>
 *
 * <p>This abstract class is independent of a specific ProFormA version.
 * Subclasses are version specific.</p>
 */
public abstract class ProformaSubmissionSubmissionHandle {

    private SubmissionLive submission;
    private SubmissionBoundary submissionBoundary;

    protected ProformaSubmissionSubmissionHandle(SubmissionLive submission, SubmissionBoundary sb) {
        this.submission = submission;
        this.submissionBoundary = sb;
    }

    protected SubmissionLive getSubmission() {
        return submission;
    }

    protected SubmissionBoundary getSubmissionBoundary() {
        return submissionBoundary;
    }

    public ProformaExternalSubmissionHandle externalSubmissionHandle() throws Exception {
        ProformaExternalSubmissionHandle result = externalSubmissionHandleImpl();
        if (result != null) result.setBoundary(getSubmissionBoundary());
        return result;
    }

    protected abstract ProformaExternalSubmissionHandle externalSubmissionHandleImpl() throws Exception;

    public abstract ProformaSubmissionFilesHandle submissionFilesHandle() throws Exception;

    /**
     * @return true, if the submission has files instead of an external submission.
     * @throws Exception
     */
    public boolean hasSubmissionFiles() throws Exception {
        return submissionFilesHandle().get() != null;
    }

    /**
     * @return true, if this is a submission from an external source.
     * @throws Exception
     */
    public boolean hasExternalSubmission() throws Exception {
        return externalSubmissionHandle().get() != null;
    }


    private boolean unzipToEmbedded(byte[] bytes, int maxNestedFiles) throws Exception {
        boolean result = false;
        ZipContent zc = Zip.readZipFileToMap(new ByteArrayInputStream(bytes));
        if (maxNestedFiles == 0 || zc.size() <= maxNestedFiles) {
            for (ZipContentElement elem : zc.values()) {
                if (!hasSubmissionFiles()) {
                    submissionFilesHandle().createAndSet();
                }
                ProformaSubmissionFileHandle sfh = submissionFilesHandle().createNewFileHandleFromScratch();
                String filename = FilenameUtils.getName(elem.getPath());
                if (Strings.looksLikeUTF8(elem.getBytes())) {
                    String content = new String(elem.getBytes(), StandardCharsets.UTF_8);
                    sfh.embeddedTxtFileHandle().createAndSet()
                        .setContent(content)
                        .setFilename(filename);
                } else {
                    sfh.embeddedBinFileHandle().createAndSet()
                        .setContent(elem.getBytes())
                        .setFilename(filename);
                }
                submissionFilesHandle().getSubmissionFileHandles().add(sfh);
                result = true;
            }
        }
        return result;
    }

    /**
     * If this submission represents a single submitted zip file (either external or as an attached or embedded file),
     * then the zip file will be unzipped and the nested files are placed as embedded submission files. If possible,
     * UTF-8 files are placed as embedded text files.
     *
     * @param maxNestedFiles If this is &gt;0, then the unzipping will only take place, when the zip files has no more than
     *                       {@link maxNestedFiles} files. If this is 0, then the unzipping will take place independent of the number of
     *                       nested files.
     * @return true, if anything was converted.
     * @throws MalformedURLException
     * @throws IOException
     * @throws Exception
     */
    public boolean unzipToEmbedded(int maxNestedFiles) throws MalformedURLException, IOException, Exception {
        boolean result = false;
        if (hasExternalSubmission()) {
            Resource resource = externalSubmissionHandle().download();
            if (resource.isZipContent()) {
                if (unzipToEmbedded(resource.getContent(), maxNestedFiles)) {
                    externalSubmissionHandle().remove();
                    result = true;
                }
            }
        } else if (hasSubmissionFiles()) {
            if (submissionFilesHandle().getSubmissionFileHandles().size() == 1) {
                ProformaSubmissionFileHandle sfh = submissionFilesHandle().getSubmissionFileHandles().get(0);
                if (sfh.attachedBinFileHandle().get() != null) {
                    String path = sfh.attachedBinFileHandle().getPath();
                    String fullPath = ProformaSubmissionZipPathes.SUBMISSION_DIRECTORY + "/" + path;
                    if (!getSubmission().getZipContent().containsKey(fullPath)) {
                        throw new IllegalArgumentException("There is no file '" + fullPath + "' inside the zip file");
                    }
                    byte[] bytes = getSubmission().getZipContentElement(fullPath).getBytes();
                    if (Zip.isZip(bytes) && unzipToEmbedded(bytes, maxNestedFiles)) {
                        getSubmission().getZipContent().remove(fullPath);
                        result = true;
                    }
                } else if (sfh.embeddedBinFileHandle().get() != null) {
                    byte[] bytes = sfh.embeddedBinFileHandle().getContent();
                    if (Zip.isZip(bytes) && unzipToEmbedded(bytes, maxNestedFiles)) {
                        result = true;
                    }
                }
                if (result) {
                    submissionFilesHandle().getSubmissionFileHandles().remove(0);
                }
            }
        }

        return result;
    }

    public boolean convertExternalToEmbeddedSubmission() throws Exception {
        if (hasExternalSubmission()) {
            Resource resource = externalSubmissionHandle().download();
            submissionFilesHandle().createAndSet();
            ProformaSubmissionFileHandle sfh = submissionFilesHandle().createNewFileHandleFromScratch();
            String filename = resource.getFileNameOrDefault();
            if (resource.isTextContent()) {
                String enc = resource.getEncodingOrUtf8AsDefault();
                String content = new String(resource.getContent(), enc);
                sfh.embeddedTxtFileHandle().createAndSet()
                    .setContent(content)
                    .setFilename(filename);
            } else {
                sfh.embeddedBinFileHandle().createAndSet()
                    .setContent(resource.getContent())
                    .setFilename(filename);
            }
            submissionFilesHandle().getSubmissionFileHandles().add(sfh);
            externalSubmissionHandle().remove();
        }
        return false;
    }
}
