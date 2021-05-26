package de.hsh.grappa;

import de.hsh.grappa.plugin.BackendPlugin;
import de.hsh.grappa.proforma.MimeType;
import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;
import de.hsh.grappa.utils.ProformaConverter;
import org.apache.commons.io.IOUtils;
import proforma.xml.SubmissionType;

import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class DummyGrader implements BackendPlugin {

    @Override
    public void init(Properties properties) throws Exception {
    }

    @Override
    public ResponseResource grade(SubmissionResource submission) throws Exception {
        TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(2, 6));
        SubmissionType submPojo = ProformaConverter.convertToPojo(submission);
        return new ResponseResource(IOUtils.toByteArray(createResponse()), MimeType.ZIP);
    }

    private InputStream createResponse() {
        return DummyGrader.class.getResourceAsStream("/response.zip");
    }
}