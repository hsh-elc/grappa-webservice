package proforma.util21;

import proforma.util.ProformaEmbeddedBinFileHandle;
import proforma.xml21.EmbeddedBinFileType;

class Proforma21EmbeddedBinFileHandle extends ProformaEmbeddedBinFileHandle {
    public Proforma21EmbeddedBinFileHandle(Object file, String propertyName) {
        super(file, propertyName, EmbeddedBinFileType.class);
    }

    @Override
    public EmbeddedBinFileType get() {
        return (EmbeddedBinFileType) super.get();
    }

    @Override
    public Proforma21EmbeddedBinFileHandle setFilename(String filename) {
        assertNotNull("set filename");
        get().setFilename(filename);
        return this;
    }

    @Override
    public Proforma21EmbeddedBinFileHandle setContent(byte[] content) {
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
    public byte[] getContent() {
        assertNotNull("get content");
        return get().getValue();
    }
}