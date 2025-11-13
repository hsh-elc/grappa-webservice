package proforma.util;

import proforma.util.boundary.SubmissionBoundary;
import proforma.util.boundary.TaskBoundary;
import proforma.xml.AbstractSubmissionType;

public abstract class ProformaSubmissionHelper extends ProformaHelper {

    public ProformaSubmissionHelper(ProformaVersion pv) {
        super(pv);
    }

    public abstract String getSubmissionId(AbstractSubmissionType submission);

    public abstract ProformaSubmissionTaskHandle getSubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb);

    public abstract ProformaSubmissionSubmissionHandle getSubmissionSubmissionHandle(SubmissionLive submission, SubmissionBoundary sb);

    public abstract ProformaSubmissionRestrictionsChecker getSubmissionRestrictionsChecker(SubmissionLive submission, TaskBoundary tb);


    public abstract void setResultSpecDetailsIfEmpty(AbstractSubmissionType submission, String structure, String format,
                                                     String studentFeedbackLevel, String teacherFeedbackLevel);

    public abstract String[] getAllFeedbackLevels();

    public abstract void addObjectsToLmsAnyNamespace(AbstractSubmissionType submission, Object... objects);

    public abstract String getResultSpecFormat(AbstractSubmissionType submission);

}
