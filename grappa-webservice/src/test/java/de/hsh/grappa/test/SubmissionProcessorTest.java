package de.hsh.grappa.test;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.service.SubmissionProcessor;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;

public class SubmissionProcessorTest {
    private GrappaServlet grapServlet;

    private String proformaSubmissionFilePath = "";

    @Before
    public void init() throws Exception {
        grapServlet = new GrappaServlet();
        grapServlet.contextInitialized(null);
    }

    @After
    public void deinit() throws Exception {
        grapServlet.contextDestroyed(null);
    }

    @Ignore
    @Test
    public void testSubmissionProcessor() throws Exception {
        try (FileInputStream fis = new FileInputStream(proformaSubmissionFilePath)) {
            SubmissionResource subm = new SubmissionResource(IOUtils.toByteArray(fis), MimeType.ZIP);
            SubmissionProcessor proc = new SubmissionProcessor(subm, "DummyGrader");
            proc.process(false);
        }
    }
}
