module grappa.webservice {
    requires grappa.util;
    requires grappa.backendplugin;
    requires grappa.backendplugin.dockerproxy;
    requires redis.clients.jedis;
    requires org.apache.commons.lang3;
    requires org.apache.commons.pool2;
    requires org.apache.commons.collections4;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires ch.qos.logback.core;
    requires com.google.gson;
    requires java.ws.rs;
    requires java.servlet;
    requires proforma.xml;
    requires proforma.util;
}