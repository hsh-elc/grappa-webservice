package de.hsh.grappa.proforma;

public class TaskWrapperImpl extends TaskWrapper {
    private proforma.xml.TaskType concreteTaskPojo;

    public TaskWrapperImpl(TaskResource taskResource) throws Exception {
        super(taskResource);
        concreteTaskPojo = (proforma.xml.TaskType) getAbstractTaskPojo();
    }

    @Override
    public String getUuid() {
        return concreteTaskPojo.getUuid();
    }
}
