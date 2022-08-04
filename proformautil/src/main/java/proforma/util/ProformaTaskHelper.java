package proforma.util;

import proforma.util.div.Zip.ZipContent;
import proforma.xml.AbstractTaskType;

import java.util.List;

/**
 * This class is not tied to a specific ProFormA version. Subclasses will implement
 * this for a specific version.
 */
public abstract class ProformaTaskHelper extends ProformaHelper {

    public ProformaTaskHelper(ProformaVersion pv) {
        super(pv);
    }

    /**
     * @param task
     * @return
     * @throws UnsupportedOperationException if the task is unsupported version
     * @throws IllegalArgumentException      if taskuuid is missing in the task
     * @throws Exception
     */
    public abstract String getTaskUuid(AbstractTaskType task) throws Exception;


    /**
     * @param task
     * @param zipContent
     * @return a list of attached and embedded files.
     */
    public abstract List<? extends ProformaTaskFileHandle> getTaskFileHandles(AbstractTaskType task, ZipContent zipContent);

}


