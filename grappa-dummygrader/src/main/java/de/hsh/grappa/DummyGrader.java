package de.hsh.grappa;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ProformaResponse;
import de.hsh.grappa.proforma.ProformaSubmission;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DummyGrader implements de.hsh.grappa.plugins.backendplugin.BackendPlugin {
    Logger log = LoggerFactory.getLogger(DummyGrader.class);

    @Override
    public void init(Properties properties) throws Exception {
        log.debug("DummyGrader: init called.");
    }

    @Override
    public ProformaResponse grade(ProformaSubmission proformaSubmission) throws Exception {
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
        //throw new Exception("DummyGrader is simulating a grading error.");
        return new ProformaResponse(IOUtils.toByteArray(createResponse()), MimeType.ZIP);
    }

    private InputStream createResponse() {
        return DummyGrader.class.getResourceAsStream("/response.zip");
    }
}