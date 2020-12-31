package de.hsh.grappa.proforma;

import org.apache.commons.lang3.NotImplementedException;
import proforma.xml.IncludedTaskFileType;


public class SubmissionWrapperImpl extends SubmissionWrapper {

    public SubmissionWrapperImpl(SubmissionResource submissionResource) throws Exception {
        super(submissionResource);
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
            if (null != included.getAttachedXmlFile()) {
                return new AttachedXmlTaskExtractor(concreteSubmPojo, this);
            } else if (null != included.getAttachedZipFile()) {
                return new AttachedZipTaskExtractor(concreteSubmPojo, this);
            } else if (null != included.getEmbeddedZipFile()) {
                throw new NotImplementedException("embedded-zip-file not yet supported");
            } else if (null != included.getEmbeddedXmlFile()) {
                // See the latest addition to the Proforma format v2.1:
                // https://github.com/ProFormA/proformaxml/blob/master/Whitepaper.md#722-the-included-task-file-element
                // TODO: This case should handle a bare-bone task XML encoded as base64,
                // without the leading XML preamble.
                throw new NotImplementedException("embedded-xml-file element not yet supported");
            } else
                throw new IllegalArgumentException("Unknown IncludedTaskFileType");
        } else if (null != concreteSubmPojo.getTask())
            throw new NotImplementedException("TODO: implement native xml task element");
        else throw new IllegalArgumentException("Unknown task element in submission");
    }
}
