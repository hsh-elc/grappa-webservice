package de.hsh.grappa.common.util.proforma;

import de.hsh.grappa.common.TaskResource;

/**
 * Currently only ProFormA 2.1 task is supported.
 */
public abstract class ProformaTaskHelper extends ProformaPojoHelper {

	/**
	 * @param taskResource
	 * @return
	 * @throws UnsupportedOperationException if the task is unsupported version
	 * @throws BadRequestException if taskuuid is missing in the task
	 * @throws Exception
	 */
	public abstract String getTaskUuid(TaskResource taskResource) throws Exception;


	
	static {
		tryRegister("2.1", ProformaTaskHelper.class, "de.hsh.grappa.proforma21.Proforma21TaskHelper");
	}
	
    public static ProformaTaskHelper getInstance(String proformaVersion) {
    	return ProformaHelper.getInstance(proformaVersion, ProformaTaskHelper.class);
    }

}
