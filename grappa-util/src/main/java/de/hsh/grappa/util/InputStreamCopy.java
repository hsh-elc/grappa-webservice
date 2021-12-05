package de.hsh.grappa.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// incoming submission input streams may need to be read
// multiple times. Since the stream becomes closed if it's
// been read once, we can't access the data anymore,
// which is why this class creates copies of the original
// stream as byte array streams
public class InputStreamCopy
{
    private InputStream is;
    private ByteArrayOutputStream copy = new ByteArrayOutputStream();

    public InputStreamCopy(InputStream is) throws Exception
    {
        this.is = is;
        copy();
    }

    private int copy() throws IOException {
        int read = 0;
        int chunk = 0;
        byte[] data = new byte[256];
        while(-1 != (chunk = is.read(data))) {
            read += data.length;
            copy.write(data, 0, chunk);
        }
        return read;
    }

    public InputStream getCopy() {
        return new ByteArrayInputStream(copy.toByteArray());
    }
}
