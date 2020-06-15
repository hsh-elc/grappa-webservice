package de.hsh.grappa.proforma;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
@XmlSeeAlso({
    de.hsh.grappa.proformaxml.v201.TaskType.class,
    de.hsh.grappa.proformaxml.v300.TaskType.class
})
public interface AbstractTaskType {
    //String getUuid();

}
