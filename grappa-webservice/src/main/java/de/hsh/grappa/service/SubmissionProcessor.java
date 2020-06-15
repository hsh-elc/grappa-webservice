package de.hsh.grappa.service;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import de.hsh.grappa.application.GrappaServlet;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.exceptions.GrappaException;
import de.hsh.grappa.exceptions.NotFoundException;
import de.hsh.grappa.proforma.*;
import de.hsh.grappa.utils.ObjectId;
import de.hsh.grappa.utils.XmlUtils;
import de.hsh.grappa.utils.Zip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmissionProcessor {
    private static final Logger log = LoggerFactory.getLogger(SubmissionProcessor.class);
    private SubmissionInternals subm;
    private String graderId;

    public SubmissionProcessor(/*GrappaConfig config,*/ ProformaSubmission subm, String graderId) throws Exception {
        //this.config = config;
        this.subm = createVersionedProformaSubmission(subm);
        this.graderId = graderId;
    }

    private SubmissionInternals createVersionedProformaSubmission(ProformaSubmission proformaSubmission) throws Exception {
        // get the submission xml file bytes, unless it's a zipped submission...
        byte[] submXmlFileBytes = proformaSubmission.getContent();
        if (proformaSubmission.getMimeType().equals(MimeType.ZIP)) {
            String submXmlFileContent = Zip.getTextFileContentFromZip(proformaSubmission.getContent(),
                ProformaSubmissionZipPathes.SUBMISSION_XML_FILE_NAME, Charsets.UTF_8);
            submXmlFileBytes = submXmlFileContent.getBytes(Charsets.UTF_8);
        }

        AbstractSubmissionType abstractSubmType = XmlUtils.unmarshalToObject(submXmlFileBytes,
            AbstractSubmissionType.class);

        if (abstractSubmType instanceof de.hsh.grappa.proformaxml.v201.SubmissionType) {
            return new SubmissionInternalsV201(proformaSubmission);
        } /*else if (abstractSubmType instanceof de.hsh.grappa.proformaxml.v2xx.SubmissionType) {
            // add new versions here
        }*/

        throw new GrappaException("Unknown Proforma version of submission.");
    }

    /**
     * @throws BadRequestException when a ill-formatted submission is received
     * @throws GrappaException     when an internal service error occurs
     */
    private void validateSubmission() throws BadRequestException, NotFoundException, GrappaException {
        // Make sure the requested graderId exists and
        // is enabled in the config file
        var grader = GrappaServlet.CONFIG.getGraders().stream().filter(g -> g.getId().equals(graderId)).findFirst();
        if (!grader.isPresent())
            throw new NotFoundException(String.format("The requested grader '%s' to be used for " +
                "grading does not exist.", graderId));
        else if (!grader.get().getEnabled())
            throw new GrappaException(String.format("Grader '%s' is disabled in the service's configuration file.",
                graderId));

        try {
            var task = subm.getTask();
            String taskuuid = task.getUuid();
            if (Strings.isNullOrEmpty(taskuuid)) {
                // TODO: taskuuid may not be set in the submission, it might be in the task ojbect though
                throw new BadRequestException("taskuuid is not set in the submission file.");
            }
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new GrappaException(e);
        }
    }

    /**
     *
     * @return the gradeProcId
     * @throws BadRequestException
     * @throws NotFoundException
     * @throws GrappaException
     */
    public String process() throws BadRequestException, NotFoundException, GrappaException {
        validateSubmission();
        cacheTask();
        // Queue submission for grading
        String gradeProcId = ObjectId.createObjectId();
        GrappaServlet.redis.pushSubmission(graderId, gradeProcId, subm.getProformaSubmission());
        synchronized (GraderPoolManager.getInstance()) {
            GraderPoolManager.getInstance().notify();
        }
        return gradeProcId;
    }

    private void cacheTask() throws GrappaException {
        try {
            TaskInternals task = subm.getTask();
            if (!GrappaServlet.redis.isTaskCached(task.getUuid())) {
                GrappaServlet.redis.cacheTask(task.getUuid(), task.getProformaTask());
            } else {
                // otherwise, refresh existing cached task timeout
                GrappaServlet.redis.refreshTaskTimeout(task.getUuid());
            }
        } catch (Exception e) {
            throw new GrappaException(e);
        }
    }
}









        //var proformaUri = new ProFormAXmlUriInspector().getUriFromXml(is);
        //var x = new XmlSubmissionOld<AbstractSubmissionType>(is, AbstractSubmissionType.class).toPOJO();
        //var x = new XmlSubmission(is).toPOJO();

        //var x = (SubmissionType)subm.toPOJO(); // uncomment and do this https://stackoverflow.com/questions/7805266/how-can-i-reopen-a-closed-inputstream-when-i-need-to-use-it-2-times
        //System.out.println("x: " + x);


        //String taskuuid = x..getTask().getUuid().;

//    byte[] bytes = IOUtils.toByteArray(this.subm.getRawInputStream());
//    r.sync().set("test", bytes);


        //String taskuuid = null;

//    // test: write task bytes to zip file in temp dir
//    byte[] taskBytes = null;
//    AbstractSubmissionType s = subm.getSubmissionPOJO();
//    System.out.println(s);
//    taskBytes = subm.getTaskBytes();
//    Path tmpDir = Files.createTempDirectory("grdprocid");
//    File submZipTargetFile = new File(Paths.get(tmpDir.toString(), "task.zip").toString());
//    System.out.println("Writing zip to: " + submZipTargetFile.getPath());
//    System.out.println("Writing byte count: " + taskBytes.length);
//    OutputStream out = new FileOutputStream(submZipTargetFile);
//    out.write(taskBytes);
//    out.close();

        //var submPojo = subm.getSubmissionPOJO();

        //System.out.println("taskuuid: " + uuid);


//    if(s instanceof de.hsh.grappa.proformaxml.v201.SubmissionType) {
//      de.hsh.grappa.proformaxml.v201.SubmissionType st = (de.hsh.grappa.proformaxml.v201.SubmissionType)s;
//      de.hsh.grappa.proformaxml.v201.TaskType tt = st.getTask();
//      taskuuid = st.getTask().getUuid();
//      submBytes = IOUtils.toByteArray(this.subm.getRawInputStream());
//    } else {
//      doCacheTask = false;
//    }
//


        //GrappaServlet.redisClient.connect()

//    }
//}
