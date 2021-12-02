package de.hsh.grappa.test;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.LmsConfig;
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
            LmsConfig lmsConfig = new LmsConfig();
            lmsConfig.setId("test");
            lmsConfig.setName("test@SubmissionProcessorTest");
            lmsConfig.setPassword_hash("test");
            SubmissionProcessor proc = new SubmissionProcessor(subm, "DummyGrader", lmsConfig);
            proc.process(false);
        }
    }
}
