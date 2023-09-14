module grappa.backendplugin.dummygrader {
    requires transitive proforma.util;
    requires proforma.xml;
    requires grappa.backendplugin;
    requires ch.qos.logback.classic;
    
    exports de.hsh.grappa.backendplugin.dummygrader;
}