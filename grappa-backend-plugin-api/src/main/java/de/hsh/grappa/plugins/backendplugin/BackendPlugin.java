package de.hsh.grappa.plugins.backendplugin;

import de.hsh.grappa.proforma.ResponseResource;
import de.hsh.grappa.proforma.SubmissionResource;

import java.util.Properties;

public interface BackendPlugin {
    void init(Properties props) throws Exception;

    /**
     * grades a proforma submission and returns a proforma response
     * @param submission the submission to be graded
     * @return a valid proforma response if the grading process finished successfully,
     * or null if the grading process was interrupted and shut down gracefully without
     * any result
     * @throws Exception on any grading execution error
     */
    ResponseResource grade(SubmissionResource submission) throws Exception;
}
