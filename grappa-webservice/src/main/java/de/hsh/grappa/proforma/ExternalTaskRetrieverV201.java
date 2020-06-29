package de.hsh.grappa.proforma;

import de.hsh.grappa.proformaxml.v201.SubmissionType;
import org.apache.commons.lang3.NotImplementedException;

public class ExternalTaskRetrieverV201 extends TaskRetriever {
    private SubmissionType concreteSubmPojo;
    private SubmissionInternals submissionInternals;
    private TaskInternals taskInternals;

    public ExternalTaskRetrieverV201(SubmissionType concreteSubmPojo,
                                     SubmissionInternals submissionInternals) {
        this.concreteSubmPojo = concreteSubmPojo;
        // An attached xml task file comes only in a zipped submission
        this.submissionInternals = submissionInternals;
    }

    @Override
    public TaskInternals getTask() throws Exception {
        throw new NotImplementedException("TODO: implement external task download");
        //            if (Strings.isNullOrEmpty(taskRepoUrl)) {
//                taskAvailability = TaskAvailabilityType.UUID;
//                if (Strings.isNullOrEmpty(taskuuid))
//                    throw new IllegalArgumentException("taskuuid and task repository are not specified");
//            } else {
//                taskAvailability = TaskAvailabilityType.EXTERNAL;
//            }
    }
}
