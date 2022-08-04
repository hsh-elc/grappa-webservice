/**
 * <p>
 * This package includes transfer object classes of
 * the <a href="https://github.com/ProFormA/taskxml/blob/master/whitepaper.md">proforma
 * task format</a>
 * </p>
 * <p>
 * For more information about the classes in this package and what they mean in Graja context
 * have a look at the
 * <a href="http://graja.hs-hannover.de/doc/executegraja/">online documentation</a>.
 * </p>
 */
@XmlSchema(
    namespace = "urn:proforma:v2.1",
    xmlns = {
        @XmlNs(namespaceURI = "urn:proforma:v2.1", prefix = "")
    },
    elementFormDefault = XmlNsForm.QUALIFIED)
package proforma.xml21;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

