package proforma.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proforma.xml.AbstractProformaType;


public abstract class ProformaHelper {


    private static final Logger log = LoggerFactory.getLogger(ProformaHelper.class);

    private ProformaVersion proformaVersion;

    public ProformaHelper(ProformaVersion pv) {
        this.proformaVersion = pv;
    }

    public ProformaVersion getProformaVersion() {
        return proformaVersion;
    }

    public abstract <T extends AbstractProformaType> Class<T> getPojoType();


}
