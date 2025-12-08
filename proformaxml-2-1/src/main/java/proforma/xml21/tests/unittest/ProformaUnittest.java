package proforma.xml21.tests.unittest;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "unittest", propOrder = {
    "entryPoint",
    "framework",
    "version"
})
@XmlRootElement(name = "unittest")
public class ProformaUnittest {

    @XmlElement(name="entry-point", required=true)
    protected List<String> entryPoint;
    @XmlAttribute(name = "framework")
    protected String framework;
    @XmlAttribute(name = "version")
    protected String version;
    
    public List<String> getEntryPoints() {
        if (entryPoint == null) {
            entryPoint= new ArrayList<>();
        }
        return entryPoint;
    }
    public String getFramework() {
        return framework;
    }
    public void setFramework(String framework) {
        this.framework = framework;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }



}
