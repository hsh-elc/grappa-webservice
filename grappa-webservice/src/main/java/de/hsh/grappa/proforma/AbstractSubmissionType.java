package de.hsh.grappa.proforma;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({
    de.hsh.grappa.proformaxml.v201.SubmissionType.class,
    de.hsh.grappa.proformaxml.v300.SubmissionType.class
})
public interface AbstractSubmissionType {
}
