package proforma.util21;

import proforma.util.ProformaAttachedTxtFileHandle;
import proforma.xml21.AttachedTxtFileType;

class Proforma21AttachedTxtFileHandle extends ProformaAttachedTxtFileHandle {
    public Proforma21AttachedTxtFileHandle(Object file, String propertyName) {
        super(file, propertyName, AttachedTxtFileType.class);
    }

    @Override
    public AttachedTxtFileType get() {
        return (AttachedTxtFileType) super.get();
    }

    @Override
    public Proforma21AttachedTxtFileHandle setPath(String path) {
        assertNotNull("set path");
        get().setValue(path);
        return this;
    }

    @Override
    public Proforma21AttachedTxtFileHandle setNaturalLang(String naturalLang) {
        assertNotNull("set naturallang");
        get().setNaturalLang(naturalLang);
        return this;
    }

    @Override
    public Proforma21AttachedTxtFileHandle setEncoding(String encoding) {
        assertNotNull("set encoding");
        get().setEncoding(encoding);
        return this;
    }

    @Override
    public String getPath() {
        assertNotNull("get path");
        return get().getValue();
    }

    @Override
    public String getNaturalLang() {
        assertNotNull("get naturallang");
        return get().getNaturalLang();
    }

    @Override
    public String getEncoding() {
        assertNotNull("get encoding");
        return get().getEncoding();
    }
}