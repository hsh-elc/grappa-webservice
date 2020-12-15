package de.hsh.grappa.proforma;

import org.apache.commons.lang3.NotImplementedException;
import proforma.xml.IncludedTaskFileType;


public class SubmissionWrapperImpl extends SubmissionWrapper {
    //private proforma.xml.SubmissionType concreteSubmPojo;

    public SubmissionWrapperImpl(SubmissionResource submissionBlob) throws Exception {
        super(submissionBlob);
        // Don't cast the concrete submisson object here.
        // The super class' constructor calls the template method createTaskRetriever(),
        // which makes use of the yet unassigned concreteSubmPojo field.
        // this.concreteSubmPojo = (proforma.xml.SubmissionType) getAbstractSubmPojo();
    }

    @Override
    protected TaskExtractor createTaskExtractor() {
        proforma.xml.SubmissionType concreteSubmPojo =
            (proforma.xml.SubmissionType) getAbstractSubmPojo();
        if (null != concreteSubmPojo.getExternalTask()) {
            return new ExternalTaskExtractor(concreteSubmPojo, this);
        } else if (null != concreteSubmPojo.getIncludedTaskFile()) {
            IncludedTaskFileType included = concreteSubmPojo.getIncludedTaskFile();
            //String taskUuid = included.getUuid(); // may be null, the attribute is optional
            if (null != included.getAttachedXmlFile()) {
                return new AttachedXmlTaskExtractor(concreteSubmPojo, this);
            } else if (null != included.getAttachedZipFile()) {
                return new AttachedZipTaskExtractor(concreteSubmPojo, this);
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
