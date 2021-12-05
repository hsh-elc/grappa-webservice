package de.hsh.grappa.proforma21;

import de.hsh.grappa.common.TaskResource;
import de.hsh.grappa.common.util.proforma.ProformaTaskHelper;
import de.hsh.grappa.common.util.proforma.TaskLive;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.util.Strings;
import proforma.xml.AbstractTaskType;
import proforma.xml21.TaskType;

public class Proforma21TaskHelper extends ProformaTaskHelper {

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AbstractTaskType> getPojoType() {
		return TaskType.class;
	}
	
	/**
	 * @param taskResource
	 * @return
	 * @throws UnsupportedOperationException if the task is unsupported version
	 * @throws BadRequestException if taskuuid is missing in the task
	 * @throws Exception
	 */
	@Override
	public String getTaskUuid(TaskResource taskResource) throws Exception {
		TaskType task = new TaskLive(taskResource).getTask(TaskType.class);
	    String taskuuid = task.getUuid();
	    if (Strings.isNullOrEmpty(taskuuid)) {
	        // TODO: taskuuid may not be set in the submission, it might be in the task ojbect though
	        throw new BadRequestException("taskuuid is not set in the task.");
	    }
	    return taskuuid;
	}



}
