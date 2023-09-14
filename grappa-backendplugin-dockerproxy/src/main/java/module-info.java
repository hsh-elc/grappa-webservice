module grappa.backendplugin.dockerproxy {
    requires org.slf4j;
    requires grappa.util;
    requires grappa.backendplugin;
    requires proforma.util;
    requires com.github.dockerjava.core;
    requires com.github.dockerjava.api;
    requires com.github.dockerjava.transport;
    requires com.github.dockerjava.transport.httpclient5;
}