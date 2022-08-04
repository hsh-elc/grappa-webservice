package proforma.util21;

import proforma.util.ProformaChildElementTaskHandle;
import proforma.xml21.TaskType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements
 * of the <code>task-type</code> elements as part of a <code>submission-type</code>.
 */
public class Proforma21ChildElementTaskHandle extends ProformaChildElementTaskHandle {
    public Proforma21ChildElementTaskHandle(Object submission, String propertyName) {
        super(submission, propertyName, TaskType.class);
    }

    @Override
    public TaskType get() {
        return (TaskType) super.get();
    }

    @Override
    public Proforma21ChildElementTaskHandle setUuid(String value) {
        assertNotNull("set uuid");
        get().setUuid(value);
        return this;
    }

    @Override
    public String getUuid() {
        assertNotNull("get uuid");
        return get().getUuid();
    }
}
