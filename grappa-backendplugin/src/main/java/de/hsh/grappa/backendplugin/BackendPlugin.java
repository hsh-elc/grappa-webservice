package de.hsh.grappa.backendplugin;

import proforma.util.boundary.Boundary;
import proforma.util.resource.MimeType;
import proforma.util.resource.ResponseResource;
import proforma.util.resource.SubmissionResource;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Properties;

public abstract class BackendPlugin {

    private Boundary boundary;

    protected String logLevel;

    private String fileEncoding;
    private String userLanguage;
    private String userCountry;

    /**
     * A backend plugin might run as part of the JVM running the webapp or as
     * part of a separate JVM in a docker container. In both cases a backend
     * plugin needs access to external resources. The return value provides this
     * access. In the latter case of a docker container not all external
     * resources are available.
     */
    public Boundary getBoundary() {
        return boundary;
    }

    /**
     * This initialization method is called before every call to grade().
     *
     * @param props
     * @param Boundary boundary
     * @throws Exception
     */
    public void init(Properties props, Boundary boundary, String logLevel) throws Exception {
        this.boundary = boundary;
        this.logLevel = logLevel;
        init(props);
    }

    /**
     * This initialization method is called before every call to grade().
     *
     * @throws Exception
     */
    public void init(Properties props, Boundary boundary, String logLevel,
                     String fileEncoding, String userLanguage, String userCountry) throws Exception {
        this.fileEncoding = fileEncoding;
        this.userLanguage = userLanguage;
        this.userCountry = userCountry;
        this.init(props, boundary, logLevel);
    }

    /**
     * This initialization method is called before every call to grade().
     *
     * @param props
     * @throws Exception
     */
    public abstract void init(Properties props) throws Exception;

    /**
     * grades a proforma submission and returns a proforma response
     *
     * @param submission the submission to be graded
     * @return a valid proforma response if the grading process finished
     * successfully, or null if the grading process was interrupted and
     * shut down gracefully without any result
     * @throws Exception on any grading execution error
     */
    public abstract ResponseResource grade(SubmissionResource submission) throws Exception;

    //Getter ensuring receiving valid values
    protected String getFileEncoding() {
        if (fileEncoding == null || fileEncoding.equals("")) return Charset.defaultCharset().name();
        return fileEncoding;
    }

    protected String getUserLanguage() {
        if (userLanguage == null || userLanguage.equals("")) return Locale.getDefault().getLanguage();
        return userLanguage;
    }

    protected String getUserCountry() {
        if (userCountry == null || userCountry.equals("")) return Locale.getDefault().getCountry();
        return userCountry;
    }

    /**
     * Specifies the required task format for this backendplugin.
     * The task will be converted to the returned format if necessary.
     *
     * @return The format of the task (contained within a submission) that will be passed to the specific backendplugin.
     *         Possible values:
     *         <ul>
     *         <li> MimeType.XML: task will be converted to XML format if necessary </li>
     *         <li> MimeType.ZIP: task will be converted to ZIP format if necessary </li>
     *         <li> null: task will be passed in its original format without conversion </li>
     *         </ul>
     */
    public MimeType requiredTaskFormat() {
        return null;
    }
}
