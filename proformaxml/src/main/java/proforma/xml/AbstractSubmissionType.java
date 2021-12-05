package proforma.xml;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({
    SubmissionType.class,
    // Different versions (other than Proforma 2.1)
    // go here in case a multi versioned Proforma
    // library should be used
})
public interface AbstractSubmissionType extends AbstractProformaType {

}
