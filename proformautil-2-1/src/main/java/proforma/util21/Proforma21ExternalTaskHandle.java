package proforma.util21;

import proforma.util.ProformaExternalTaskHandle;
import proforma.xml21.ExternalTaskType;


/**
 * This is a ProFormA 2.1 specific implementation of the child elements
 * of the <code>external-task-type</code>.
 */
public class Proforma21ExternalTaskHandle extends ProformaExternalTaskHandle {
    public Proforma21ExternalTaskHandle(Object submission, String propertyName) {
        super(submission, propertyName, ExternalTaskType.class);
    }

    @Override
    public ExternalTaskType get() {
        return (ExternalTaskType) super.get();

    }

    @Override
    public Proforma21ExternalTaskHandle setUuid(String value) {
        assertNotNull("set uuid");
        get().setUuid(value);
        return this;
    }

    @Override
    public Proforma21ExternalTaskHandle setUri(String value) {
        assertNotNull("set uri");
        get().setUri(value);
        return this;
    }

    @Override
    public String getUuid() {
        assertNotNull("get uuid");
        return get().getUuid();
    }

    @Override
    public String getUri() {
        assertNotNull("get uri");
        return get().getUri();
    }
}
