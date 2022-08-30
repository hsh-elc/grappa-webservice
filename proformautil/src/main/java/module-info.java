module proformautil {
    requires java.xml;
    requires java.xml.bind;

    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.text;

    requires transitive slf4j.api;

    requires proformaxml;

    exports proforma.util;
    exports proforma.util.div;
    exports proforma.util.boundary;
    exports proforma.util.resource;
}