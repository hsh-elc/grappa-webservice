package de.hsh.grappa.proforma;

import de.hsh.grappa.proformaxml.v201.IncludedTaskFileType;
import org.apache.commons.lang3.NotImplementedException;


public class SubmissionInternalsV201 extends SubmissionInternals {
    //private de.hsh.grappa.proformaxml.v201.SubmissionType concreteSubmPojo;

    public SubmissionInternalsV201(ProformaSubmission proformaSubmission) throws Exception {
        super(proformaSubmission);
        // Don't cast the concrete submisson object here.
        // The super class' constructor calls the template method createTaskRetriever(),
        // which makes use of the yet unassigned concreteSubmPojo field.
        //this.concreteSubmPojo = (de.hsh.grappa.proformaxml.v201.SubmissionType) getAbstractSubmPojo();
    }

    @Override
    protected TaskRetriever createTaskRetriever() {
        de.hsh.grappa.proformaxml.v201.SubmissionType concreteSubmPojo =
            (de.hsh.grappa.proformaxml.v201.SubmissionType) getAbstractSubmPojo();
        if (null != concreteSubmPojo.getExternalTask()) {
            String taskUuid = concreteSubmPojo.getExternalTask().getUuid(); // may return null;
            String taskRepoUrl = concreteSubmPojo.getExternalTask().getValue();
            return new ExternalTaskRetrieverV201(concreteSubmPojo, this);
        } else if (null != concreteSubmPojo.getIncludedTaskFile()) {
            IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
            //String taskUuid = included.getUuid(); // may be null, the attribute is optional
            if (null != included.getAttachedXmlFile()) {
                return new AttachedXmlTaskRetrieverV201(concreteSubmPojo, this);
            } else if (null != included.getAttachedZipFile()) {
                return new AttachedZipTaskRetrieverV201(concreteSubmPojo, this);
            } else if (null != included.getEmbeddedZipFile()) {
                // return EmbeddedZipTaskRetriever
                //String zip = included.getEmbeddedZipFile();
                throw new NotImplementedException("TODO: implement embedded zip");
            } else throw new IllegalArgumentException("Unknown IncludedTaskFileType");
        } else if (null != concreteSubmPojo.getTask())
            throw new NotImplementedException("TODO: implement native xml task element");
        else throw new IllegalArgumentException("Unknown task element in submission");
    }
}
