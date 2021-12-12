package de.hsh.grappa.common;

import java.util.Properties;

public abstract class BackendPlugin {

    private Boundary boundary;
    
    /**
     * A backend plugin might run as part of the JVM running the webapp or as part of a separate JVM
     * in a docker container. In both cases a backend plugin needs access to external resources.
     * The return value provides this access. In the latter case of a docker container not all
     * external resources are available.
     */
    public Boundary getBoundary() {
        return boundary;
    }
    
    /**
     * This initialization method is called before every call to grade().
     * @param props
     * @param Boundary boundary
     * @throws Exception
     */
    public void init(Properties props, Boundary boundary) throws Exception {
        this.boundary = boundary;
        init(props);
    }

    
    /**
     * This initialization method is called before every call to grade().
     * @param props
     * @throws Exception
     */
    public abstract void init(Properties props) throws Exception;

    
    /**
     * grades a proforma submission and returns a proforma response
     * @param submission the submission to be graded
     * @return a valid proforma response if the grading process finished successfully,
     * or null if the grading process was interrupted and shut down gracefully without
     * any result
     * @throws Exception on any grading execution error
     */
    public abstract ResponseResource grade(SubmissionResource submission) throws Exception;
}
