package de.hsh.grappa.test;

import de.hsh.grappa.utils.InputStreamCopy;
import io.netty.util.internal.StringUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class TestInputStreamCopy {
    @Test
    public void test() throws Exception {
        String initialString = "text";
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        InputStreamCopy c = new InputStreamCopy(targetStream);

        StringWriter writer = new StringWriter();
        IOUtils.copy(c.getCopy(), writer, StandardCharsets.UTF_8);
        String theString = writer.toString();
        System.out.println(theString);
        assert !StringUtil.isNullOrEmpty(theString);
        //assert theString.equals(initialString);
    }
}
