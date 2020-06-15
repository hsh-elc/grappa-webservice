package de.hsh.grappa.test;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaSubmission;
import de.hsh.grappa.service.SubmissionProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;

public class SubmissionProcessorTest {
    private GrappaServlet grapServlet;

    private String proformaSubmissionFilePath = "...";

    @Before
    public void init() throws Exception {
        grapServlet = new GrappaServlet();
        grapServlet.contextInitialized(null);
    }

    @After
    public void deinit() throws Exception {
        grapServlet.contextDestroyed(null);
    }

    @Test
    public void testSubmissionProcessor() throws Exception {
        try (FileInputStream fis = new FileInputStream(proformaSubmissionFilePath)) {
            ProformaSubmission subm = new ProformaSubmission(IOUtils.toByteArray(fis), MimeType.ZIP);
            SubmissionProcessor proc = new SubmissionProcessor(subm, "DummyGrader");
            proc.process();
        }
    }
}
