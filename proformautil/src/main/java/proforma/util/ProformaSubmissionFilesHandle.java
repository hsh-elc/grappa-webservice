package proforma.util;

import proforma.util.div.PropertyHandle;
import proforma.util.div.Zip.ZipContent;

import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstraction of an submission-files element as part of a submission.
 * It is independent of a specific ProFormA version.
 * The type of {@code submission} must implement a getter and a setter for a
 * property named "{@code propertyName}" of type {@code clazz}.
 * The type {@code clazz} must implement a default constructor
 * and a method {@code getFile} that returns a writable list of the contained
 * submission file elements.
 */
public abstract class ProformaSubmissionFilesHandle {
    private ZipContent zipContent;
    private PropertyHandle propertyHandle;

    public ProformaSubmissionFilesHandle(Object submission, ZipContent zipContent, String propertyName, Class<?> clazz) {
        this.zipContent = zipContent;
        if (submission == null) throw new AssertionError(this.getClass() + ": submission shouldn't be null");
        propertyHandle = new PropertyHandle(submission, propertyName, clazz);
    }

    protected void assertNotNull(String whatToDo) throws NullPointerException {
        propertyHandle.assertNotNull(whatToDo, this);
    }


    public Object get() {
        return propertyHandle.get();
    }

    public void set(Object value) {
        propertyHandle.set(value);
    }

    public ProformaSubmissionFilesHandle createAndSet() {
        propertyHandle.createAndSet();
        return this;
    }

    public void remove() {
        propertyHandle.set(null);
    }

    /**
     * @return a writable map or null, if this is an XML submission.
     */
    public ZipContent getZipContent() {
        return zipContent;
    }

    /**
     * @return must provide a two-parameter constructor accepting (Object, ZipContent).
     */
    protected abstract Class<? extends ProformaSubmissionFileHandle> getElementHandleClass();


    /**
     * @return the class of the submission-file object. Must provide a default constructor.
     */
    protected abstract Class<?> getElementClass();


    public ProformaSubmissionFileHandle createNewFileHandleFromScratch() {
        try {
            return createNewFileHandle(getElementClass().getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new AssertionError(e);
        }
    }


    public ProformaSubmissionFileHandle createNewFileHandle(Object file) {
        try {
            return getElementHandleClass().getConstructor(Object.class, ZipContent.class).newInstance(file, getZipContent());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new AssertionError(e);
        }

    }


    /**
     * @return a list of file handles that is fed by the submission-file elements.
     * A file handle provides service routines
     * for further processing of each embedded or attached file.
     */
    public List<ProformaSubmissionFileHandle> getSubmissionFileHandles() {
        if (get() == null) return null;
        PropertyHandle listHandle = new PropertyHandle(get(), "file", List.class);

        class WrapperList extends AbstractList<ProformaSubmissionFileHandle> {
            private ArrayList<ProformaSubmissionFileHandle> listOfFileHandles = new ArrayList<>();

            WrapperList() {
                for (Object elem : theList()) {
                    listOfFileHandles.add(createNewFileHandle(elem));
                }
            }

            @SuppressWarnings("unchecked")
            private List<Object> theList() {
                return (List<Object>) listHandle.get();
            }

            @Override
            public ProformaSubmissionFileHandle get(int index) {
                return listOfFileHandles.get(index);
            }

            @Override
            public int size() {
                return listOfFileHandles.size();
            }

            @Override
            public ProformaSubmissionFileHandle set(int index, ProformaSubmissionFileHandle element) {
                ProformaSubmissionFileHandle result = listOfFileHandles.get(index);
                theList().set(index, element.getFile());
                listOfFileHandles.set(index, element);
                return result;
            }

            @Override
            public void add(int index, ProformaSubmissionFileHandle element) {
                theList().add(index, element.getFile());
                listOfFileHandles.add(index, element);
            }

            @Override
            public ProformaSubmissionFileHandle remove(int index) {
                theList().remove(index);
                return listOfFileHandles.remove(index);
            }
        }
        ;
        return new WrapperList();
    }


    /**
     * Convert any attached files to embedded files.
     *
     * @return true, if anything was converted.
     */
    public boolean convertAttachedToEmbedded() {
        boolean result = false;
        for (ProformaSubmissionFileHandle psfa : getSubmissionFileHandles()) {
            result = result | psfa.convertSubmissionFileToEmbedded(true, true);
        }
        return result;
    }

}
