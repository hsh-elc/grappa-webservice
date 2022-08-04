package proforma.util21;

import proforma.util.ProformaSubmissionTaskHandle;
import proforma.util.SubmissionLive;
import proforma.util.boundary.TaskBoundary;
import proforma.xml21.SubmissionType;

public class Proforma21SubmissionTaskHandle extends ProformaSubmissionTaskHandle {

    private Proforma21IncludedTaskFileHandle includedTaskFileHandle;
    private Proforma21ExternalTaskHandle externalTaskHandle;
    private Proforma21ChildElementTaskHandle childElementTaskHandle;

    protected Proforma21SubmissionTaskHandle(SubmissionLive submission, TaskBoundary tb) {
        super(submission, tb);
        if (submission == null) throw new AssertionError(this.getClass() + ": submission shouldn't be null");
    }

    @Override
    public Proforma21IncludedTaskFileHandle includedTaskFileHandle() throws Exception {
        SubmissionType s = getSubmission().getSubmission();
        if (includedTaskFileHandle == null) {
            includedTaskFileHandle = new Proforma21IncludedTaskFileHandle(s, "includedTaskFile");
        }
        return includedTaskFileHandle;
    }

    @Override
    public Proforma21ExternalTaskHandle externalTaskHandle() throws Exception {
        SubmissionType s = getSubmission().getSubmission();
        if (externalTaskHandle == null) {
            externalTaskHandle = new Proforma21ExternalTaskHandle(s, "externalTask");
        }
        return externalTaskHandle;
    }

    @Override
    public Proforma21ChildElementTaskHandle childElementTaskHandle() throws Exception {
        SubmissionType s = getSubmission().getSubmission();
        if (childElementTaskHandle == null) {
            childElementTaskHandle = new Proforma21ChildElementTaskHandle(s, "task");
        }
        return childElementTaskHandle;
    }
}
