package de.hsh.grappa.util.proforma;

import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.util.Strings;
import proforma.xml.TaskType;

public class Proforma21TaskHelper {

	/**
	 * Currently only ProFormA 2.1 task is supported.
	 * @param taskResource
	 * @return
	 * @throws UnsupportedOperationException if the task is unsupported version
	 * @throws BadRequestException if taskuuid is missing in the task
	 * @throws Exception
	 */
	public static String getTaskUuid(TaskResource taskResource) throws Exception {
		TaskType task = new TaskLive(taskResource).getTaskAs(TaskType.class);
	    String taskuuid = task.getUuid();
	    if (Strings.isNullOrEmpty(taskuuid)) {
	        // TODO: taskuuid may not be set in the submission, it might be in the task ojbect though
	        throw new BadRequestException("taskuuid is not set in the task.");
	    }
	    return taskuuid;
	}

}
