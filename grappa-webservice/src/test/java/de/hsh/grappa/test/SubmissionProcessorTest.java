package de.hsh.grappa.test;

import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.config.GraderID;
import de.hsh.grappa.config.LmsConfig;
import de.hsh.grappa.service.SubmissionProcessor;
import proforma.util.div.IOUtils;
import proforma.util.resource.MimeType;
import proforma.util.resource.SubmissionResource;

import org.junit.After;
import org.junit.Before;
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

    @Test
    public void testSubmissionProcessor() throws Exception {
        try (FileInputStream fis = new FileInputStream(proformaSubmissionFilePath)) {
            SubmissionResource subm = new SubmissionResource(IOUtils.toByteArray(fis), MimeType.ZIP);
            LmsConfig lmsConfig = new LmsConfig();
            lmsConfig.setId("test");
            lmsConfig.setName("test@SubmissionProcessorTest");
            lmsConfig.setPassword_hash("test");
            GraderID newGrader = new GraderID("DummyGrader", "1.0");
            SubmissionProcessor proc = new SubmissionProcessor(subm, newGrader, lmsConfig);
            proc.process(false);
        }
    }
}
