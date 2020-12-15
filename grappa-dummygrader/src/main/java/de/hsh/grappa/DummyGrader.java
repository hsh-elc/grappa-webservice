package de.hsh.grappa;

import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.utils.ProformaConverter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proforma.xml.SubmissionType;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DummyGrader implements de.hsh.grappa.plugins.backendplugin.BackendPlugin {
    private static final Logger log = LoggerFactory.getLogger(DummyGrader.class);

    @Override
    public void init(Properties properties) throws Exception {
        log.debug("DummyGrader: init called.");
    }

    @Override
    public ResponseResource grade(SubmissionResource submissionBlob) throws Exception {
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));

        SubmissionType submPojo = ProformaConverter.convertToPojo(submissionBlob);
        log.debug("DummyGrader SubmType pojo: {}", submPojo);

        //throw new Exception("DummyGrader is simulating a grading error.");
        return new ResponseResource(IOUtils.toByteArray(createResponse()), MimeType.ZIP);
    }

    private InputStream createResponse() {
        return DummyGrader.class.getResourceAsStream("/response.zip");
    }
}