package de.hsh.grappa.common.util.proforma;

import proforma.xml.AbstractProformaType;

public abstract class ProformaPojoHelper extends ProformaHelper {
	public abstract <T extends AbstractProformaType> Class<T> getPojoType();

}
