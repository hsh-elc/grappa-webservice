package proforma.util21;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import proforma.util.ProformaSubmissionRestrictionViolation;
import proforma.util.ProformaSubmissionRestrictionViolations;
import proforma.util.ProformaSubmissionRestrictionsChecker;
import proforma.util.SubmissionLive;
import proforma.util.TaskLive;
import proforma.util.boundary.TaskBoundary;
import proforma.util.div.Zip;
import proforma.util.div.Zip.ZipContent;
import proforma.util.div.Zip.ZipContentElement;
import proforma.xml21.FileRestrType;
import proforma.xml21.SubmissionRestrictionsType;
import proforma.xml21.TaskType;

public class Proforma21SubmissionRestrictionsChecker extends ProformaSubmissionRestrictionsChecker {

    private final List<String> submissionFileNames = new ArrayList<>();
    private final List<ProformaSubmissionRestrictionViolation> restrictionViolations = new ArrayList<>();

    public Proforma21SubmissionRestrictionsChecker(SubmissionLive submission, TaskBoundary tb) {
        super(submission, tb);
    }

    @Override
    public ProformaSubmissionRestrictionViolations checkSubmissionRestrictions() throws Exception {
        extractSubmissionFileNames();

        TaskLive taskLive = this.submissionLive.getTask(tb);
        TaskType task = (TaskType) taskLive.getTask();
        SubmissionRestrictionsType restrictions = task.getSubmissionRestrictions();

        if (null == restrictions) {
            return null;
        }

        BigInteger maxSize = restrictions.getMaxSize();
        if (maxSize != null) {
            checkMaxSize(restrictions.getMaxSize());
        }

        List<FileRestrType> restrictionList = restrictions.getFileRestriction();
        for (FileRestrType restriction : restrictionList) {
            checkFileRestriction(restriction);
        }

        return new Proforma21SubmissionRestrictionViolations(this.restrictionViolations, restrictions.getDescription());
    }

    /**
     * Checks if the max submission size is exceeded
     */
    private void checkMaxSize(BigInteger maxSize) throws Exception {
        BigInteger total = BigInteger.ZERO;
        ZipContent content = this.submissionLive.getZipContent();
        for (String file : content.keySet()) {
            if (file.startsWith("submission/")) {
                total = total.add(BigInteger.valueOf(content.get(file).getSize()));
            }
        }
        if (total.compareTo(maxSize) > 0) {
            this.restrictionViolations.add(Proforma21SubmissionRestrictionViolation.maxSizeExceeded(maxSize));
        }
    }

    /**
     * Checks if submission file names match given file restriction
     */
    private void checkFileRestriction(FileRestrType restriction) throws Exception {
        boolean isUseRequired = restriction.getUse().equals("required");
        boolean isUseProhibited = restriction.getUse().equals("prohibited");
        boolean isFormatNone = restriction.getPatternFormat().equals("none");
        boolean isFormatPosixEre = restriction.getPatternFormat().equals("posix-ere");

        String value = restriction.getValue();
        
        // Add "/" in front of file name, which could be left out if format is none (Proforma 2.1 Whitepaper 5.5.2)
        if (isFormatNone) {
            value = addSlashToFilename(value);
        }

        Proforma21SubmissionRestrictionViolation violation = null;
        if (isUseRequired && isFormatNone) {
            if (!this.submissionFileNames.contains(value)) {
                violation = Proforma21SubmissionRestrictionViolation.missingRequiredFile(value);
            }
        } else if (isUseRequired && isFormatPosixEre) {
            if (!doesAnySubmissionFilenameMatchRegex(value)) {
                violation = Proforma21SubmissionRestrictionViolation.missingRequiredRegexMatch(value);
            }
        } else if (isUseProhibited && isFormatNone) {
            if (this.submissionFileNames.contains(value)) {
                violation = Proforma21SubmissionRestrictionViolation.existingProhibitedFile(value);
            }
        } else if (isUseProhibited && isFormatPosixEre) {
            if (doesAnySubmissionFilenameMatchRegex(value)) {
                violation = Proforma21SubmissionRestrictionViolation.existingProhibitedRegexMatch(value);
            }
        }
        if (violation != null) {
            this.restrictionViolations.add(violation);
        }
    }

    /**
     * Extracts all filenames as Strings that are located in the "submission/" directive
     */
    private void extractSubmissionFileNames() throws Exception {
        ZipContent zipContent = this.submissionLive.getZipContent();
        for (String file : zipContent.keySet()) {
            if (!file.startsWith("submission/")) {
                continue;
            }
            ZipContentElement zipFile = zipContent.get(file);
            if (zipFile.isDirectory()) {
                continue;
            }
            byte[] zipFileBytes = zipFile.getBytes();
            if (Zip.isZip(zipFileBytes)) {
                extractZipInnerFilenames(zipFileBytes);
            } else {
                String submissionFile = file.substring("submission/".length());
                this.submissionFileNames.add(addSlashToFilename(submissionFile));
            }
        }
    }

    /**
     * Extracts all file names inside of a zip file
     */
    private void extractZipInnerFilenames(byte[] zipFileBytes) throws Exception {
        ZipContent innerZipContent = Zip.readZipFileToMap(new ByteArrayInputStream(zipFileBytes));
        for (String innerFile : innerZipContent.keySet()) {
            if (!innerZipContent.get(innerFile).isDirectory()) {
                this.submissionFileNames.add(addSlashToFilename(innerFile));
            }
        }
    }

    /**
     * Checks if any String in this.submissionFileNames matches the given regex pattern
     */
    private boolean doesAnySubmissionFilenameMatchRegex(String regexPattern) {
        Pattern regex = Pattern.compile(regexPattern);
        for (String filename : this.submissionFileNames) {
            Matcher matcher = regex.matcher(filename);
            if (matcher.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a leading "/" to filename if it does not already have one
     */
    private String addSlashToFilename(String filename) {
        if (filename.startsWith("/")) {
            return filename;
        } else {
            return "/" + filename;
        }
    }
}