package proforma.util21;

import proforma.util.ProformaTaskHelper;
import proforma.util.ProformaVersion;
import proforma.util.div.Strings;
import proforma.util.div.Zip.ZipContent;
import proforma.xml.AbstractProformaType;
import proforma.xml.AbstractTaskType;
import proforma.xml21.TaskFileType;
import proforma.xml21.TaskFilesType;
import proforma.xml21.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Proforma21TaskHelper extends ProformaTaskHelper {

    public Proforma21TaskHelper(ProformaVersion pv) {
        super(pv);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractProformaType> Class<T> getPojoType() {
        return (Class<T>) TaskType.class;
    }


    /**
     * @param task
     * @return
     * @throws IllegalArgumentException if taskuuid is missing in the task
     * @throws Exception
     */
    @Override
    public String getTaskUuid(AbstractTaskType task) throws Exception {
        TaskType t = (TaskType) task;
        String taskuuid = t.getUuid();
        if (Strings.isNullOrEmpty(taskuuid)) {
            throw new IllegalArgumentException("taskuuid is not set in the task.");
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
        ArrayList<Proforma21TaskFileHandle> list = new ArrayList<>();
        for (TaskFileType file : tf.getFile()) {
            list.add(getTaskFileHandle(file, zipContent));
        }
        return list;
    }


}
