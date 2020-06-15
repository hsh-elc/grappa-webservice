package de.hsh.grappa.proforma;

public class TaskInternalsV201 extends TaskInternals {
    private de.hsh.grappa.proformaxml.v201.TaskType concreteTaskPojo;

    public TaskInternalsV201(ProformaTask proformaTask) throws Exception {
        super(proformaTask);
        concreteTaskPojo = (de.hsh.grappa.proformaxml.v201.TaskType) getAbstractTaskPojo();
    }

    @Override
    public String getUuid() {
        return concreteTaskPojo.getUuid();
    }
}
