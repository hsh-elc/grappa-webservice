package de.hsh.grappa.common.util.proforma;

/**
 * TODO: replace this class by a generic handling of different ProFormA versions at the same time.
 * 
 * Currently the ProFormA version is nailed down to 2.1
 *
 */
public class ProformaVersion {
	
	private static final String version = "2.1";

	private static ProformaTaskHelper taskHelper = ProformaTaskHelper.getInstance(version);
	private static ProformaSubmissionHelper submissionHelper = ProformaSubmissionHelper.getInstance(version);
	private static ProformaResponseHelper responseHelper = ProformaResponseHelper.getInstance(version);
	private static ProformaFileChoiceGroupHelper fileChoiceGroupHelper = ProformaFileChoiceGroupHelper.getInstance(version);

	public static String getVersion() {
		return version;
	}
	
	public static ProformaTaskHelper getTaskHelper() {
		return taskHelper;
	}
	
	public static ProformaSubmissionHelper getSubmissionHelper() {
		return submissionHelper;
	}
	
	public static ProformaResponseHelper getResponseHelper() {
		return responseHelper;
	}
	
	public static ProformaFileChoiceGroupHelper getFileChoiceGroupHelper() {
		return fileChoiceGroupHelper;
	}
	

}
