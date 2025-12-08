/**
 * <p>
 *  This package includes transfer object classes of 
 *  the <a href="https://github.com/ProFormA/taskxml/blob/master/whitepaper.md">proforma
 *  task format</a>
 *  </p>
 * <p>
 * For more information about the classes in this package and what they mean in Graja context
 * have a look at the 
 * <a href="http://graja.hs-hannover.de/doc/executegraja/">online documentation</a>.
 * </p>
 *  
 */
@XmlSchema(
    namespace = ProformaUnilttestXmlNamespace.XML_NAMESPACE_PROFORMA_TESTS_UNITTEST,
    xmlns = {   
            @XmlNs(namespaceURI = ProformaUnilttestXmlNamespace.XML_NAMESPACE_PROFORMA_TESTS_UNITTEST, prefix = "u")
    },
    elementFormDefault = XmlNsForm.QUALIFIED)
package proforma.xml21.tests.unittest;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import de.hsh.graja.transform.ProformaUnilttestXmlNamespace;

