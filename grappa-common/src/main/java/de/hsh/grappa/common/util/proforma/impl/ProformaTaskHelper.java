package de.hsh.grappa.common.util.proforma.impl;

import java.util.List;

import de.hsh.grappa.common.util.proforma.ProformaVersion;
import de.hsh.grappa.exceptions.BadRequestException;
import de.hsh.grappa.util.Zip.ZipContent;
import proforma.xml.AbstractTaskType;

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
	 * @throws BadRequestException if taskuuid is missing in the task
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


