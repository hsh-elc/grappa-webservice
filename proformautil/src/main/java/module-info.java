module proforma.util {
    requires java.xml.bind;

    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires org.apache.commons.text;

    requires transitive org.slf4j;

    requires transitive proforma.xml;

    exports proforma.util;
    exports proforma.util.exception;
    exports proforma.util.div;
    exports proforma.util.boundary;
    exports proforma.util.resource;
}