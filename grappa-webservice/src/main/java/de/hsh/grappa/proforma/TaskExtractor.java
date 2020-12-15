package de.hsh.grappa.proforma;

// Sub classes retrieve a task object from a submission
// and add add additional logic, e.g. downolading an
// external task from a remote task repository
public abstract class TaskExtractor {
    public abstract TaskWrapper getTask() throws Exception;
}
