package proforma.util21;

import proforma.util.ProformaEmbeddedTxtFileHandle;
import proforma.xml21.EmbeddedTxtFileType;

class Proforma21EmbeddedTxtFileHandle extends ProformaEmbeddedTxtFileHandle {
    public Proforma21EmbeddedTxtFileHandle(Object file, String propertyName) {
        super(file, propertyName, EmbeddedTxtFileType.class);
    }

    @Override
    public EmbeddedTxtFileType get() {
        return (EmbeddedTxtFileType) super.get();
    }

    @Override
    public Proforma21EmbeddedTxtFileHandle setFilename(String filename) {
        assertNotNull("set filename");
        get().setFilename(filename);
        return this;
    }

    @Override
    public Proforma21EmbeddedTxtFileHandle setContent(String content) {
        assertNotNull("set content");
        get().setValue(content);
        return this;
    }

    @Override
    public String getFilename() {
        assertNotNull("get filename");
        return get().getFilename();
    }

    @Override
    public String getContent() {
        assertNotNull("get content");
        return get().getValue();
    }
}