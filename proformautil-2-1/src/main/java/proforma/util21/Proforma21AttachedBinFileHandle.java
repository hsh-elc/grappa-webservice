package proforma.util21;

import proforma.util.ProformaAttachedBinFileHandle;

public class Proforma21AttachedBinFileHandle extends ProformaAttachedBinFileHandle {
    public Proforma21AttachedBinFileHandle(Object file, String propertyName) {
        super(file, propertyName, String.class);
    }


    @Override
    public String get() {
        return (String) super.get();
    }

    /**
     * Sets the path
     *
     * @param path
     * @return can be used for method call chaining on this object.
     */
    public Proforma21AttachedBinFileHandle setPath(String path) {
        set(path);
        return this;
    }

    /**
     * @return the path inside the zip file
     */
    public String getPath() {
        return get();
    }

}
