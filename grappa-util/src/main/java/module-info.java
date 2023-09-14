module grappa.util {
    requires transitive org.slf4j;
    
    requires org.apache.commons.compress;
    requires org.apache.commons.io;
    requires com.google.gson;
    requires ch.qos.logback.classic;
    
    exports de.hsh.grappa.util;
}