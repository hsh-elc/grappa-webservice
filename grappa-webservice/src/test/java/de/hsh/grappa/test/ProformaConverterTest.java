package de.hsh.grappa.test;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.utils.ProformaConverter;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

public class ProformaConverterTest {
    private final String submissionFilePath = "";

    @Test
    public void testConversion() throws Exception {
        byte[] b = FileUtils.readFileToByteArray(new File(submissionFilePath));
        SubmissionResource subm = new SubmissionResource(b, MimeType.ZIP);
        var s = ProformaConverter.convertToPojo(subm);
    }
}
