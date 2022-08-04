package proforma.xml;

public interface AbstractProformaType {

    /**
     * @return a string like "2.1"
     */
    String proFormAVersionNumber();

//    /**
//     * This method returns the class object, which is to be added to JAXB marshalling operations to
//     * ensure discovery of the specifics of the subclass representing a specific ProFormA version.
//     * 
//     * @return The subclass of either {@link AbstractTaskType}, {@link AbstractSubmissionType}, or
//     * {@link AbstractResponseType}.
//     */
//    Class<? extends AbstractProformaType> getContextClass();

}
