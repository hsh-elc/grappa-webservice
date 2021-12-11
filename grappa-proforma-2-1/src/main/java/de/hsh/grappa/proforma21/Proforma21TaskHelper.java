package de.hsh.grappa.proforma21;

import java.util.ArrayList;
import java.util.List;

import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.common.util.proforma.impl.ProformaTaskHelper;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.util.Strings;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml.AbstractTaskType;
import proforma.xml21.TaskFileType;
import proforma.xml21.TaskFilesType;
import proforma.xml21.TaskType;

public class Proforma21TaskHelper extends ProformaTaskHelper {

	public Proforma21TaskHelper(ProformaVersion pv) {
		super(pv);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends AbstractTaskType> getPojoType() {
		return TaskType.class;
	}
	
	

	/**
	 * @param task
	 * @return
	 * @throws BadRequestException if taskuuid is missing in the task
	 * @throws Exception
	 */
	@Override 
	public String getTaskUuid(AbstractTaskType task) throws Exception {
		TaskType t = (TaskType)task;
	    String taskuuid = t.getUuid();
	    if (Strings.isNullOrEmpty(taskuuid)) {
	        throw new BadRequestException("taskuuid is not set in the task.");
	    }
	    return taskuuid;
	}

	
	
	private Proforma21TaskFileHandle getTaskFileHandle(TaskFileType file, ZipContent zipContent) {
		if (file == null) return null;
		return new Proforma21TaskFileHandle(file, zipContent);
	}
	
	
	@Override
	public List<Proforma21TaskFileHandle> getTaskFileHandles(AbstractTaskType task, ZipContent zipContent) {
        TaskType t = (TaskType) task;
		TaskFilesType tf = t.getFiles();
		if (tf == null) return null;
		ArrayList<Proforma21TaskFileHandle> list= new ArrayList<>();
		for (TaskFileType file : tf.getFile()) {
			list.add(getTaskFileHandle(file, zipContent));
		}
		return list;
	}
	


}
