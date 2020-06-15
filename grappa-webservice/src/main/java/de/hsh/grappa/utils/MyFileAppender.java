package de.hsh.grappa.utils;

import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import ch.qos.logback.core.rolling.RollingFileAppender;

import java.io.IOException;

public class MyFileAppender<E> extends RollingFileAppender<E> {
    protected void writeOut(E event) throws IOException {
        super.writeOut(event);
        ResilientFileOutputStream resilientFos = (ResilientFileOutputStream) super
                .getOutputStream();
        resilientFos.flush();
        resilientFos.getChannel().force(true);
    }
}
