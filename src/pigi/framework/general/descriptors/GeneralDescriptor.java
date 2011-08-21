package pigi.framework.general.descriptors;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.vo.DataObject;

public class GeneralDescriptor extends DataDescriptor<DataObject> {
	
	public static String dataTableName;

	public GeneralDescriptor() {
		super(dataTableName, DataObject.class);
	}	
}
