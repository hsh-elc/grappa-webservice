package proforma.util21;

import proforma.util.ProformaResponseHelper;
import proforma.util.ProformaSubmissionHelper;
import proforma.util.ProformaTaskHelper;
import proforma.util.ProformaVersion;

public class Proforma21Version extends ProformaVersion {


    private Proforma21TaskHelper th;
    private Proforma21SubmissionHelper sh;
    private Proforma21ResponseHelper rh;

    public Proforma21Version() {
        th = new Proforma21TaskHelper(this);
        sh = new Proforma21SubmissionHelper(this);
        rh = new Proforma21ResponseHelper(this);

    }

    @Override
    public ProformaTaskHelper getTaskHelper() {
        return th;
    }

    @Override
    public ProformaSubmissionHelper getSubmissionHelper() {
        return sh;
    }

    @Override
    public ProformaResponseHelper getResponseHelper() {
        return rh;
    }

    @Override
    public String getVersionNumber() {
        return "2.1";
    }

    @Override
    public String getXmlNamespaceUri() {
        return "urn:proforma:v2.1";
    }

}
