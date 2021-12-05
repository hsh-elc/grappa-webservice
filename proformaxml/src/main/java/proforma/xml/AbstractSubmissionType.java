package proforma.xml;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
//XmlSeeAlso leads to a circular dependency between the general proformaxml module and
//a version specific module. Now we explicitly implement the version specific class as
//an interface method AbstractProformaType::getContextClasses.
//@XmlSeeAlso({
//    SubmissionType.class,
//    // Different versions (other than Proforma 2.1)
//    // go here in case a multi versioned Proforma
//    // library should be used
//})
public interface AbstractSubmissionType extends AbstractProformaType {

}
