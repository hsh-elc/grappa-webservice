module grappa.backendplugin.dockerproxy {
    requires org.slf4j;
    requires grappa.util;
    requires grappa.backendplugin;
    requires transitive proforma.util;
    requires com.github.dockerjava.core;
    requires com.github.dockerjava.api;
    requires com.github.dockerjava.transport;
    requires com.github.dockerjava.transport.httpclient5;
    
    exports de.hsh.grappa.backendplugin.dockerproxy;
}