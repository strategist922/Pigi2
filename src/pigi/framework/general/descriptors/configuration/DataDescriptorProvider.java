package pigi.framework.general.descriptors.configuration;

import pigi.framework.general.PigiException;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.vo.DataObject;

public interface DataDescriptorProvider {
    <T extends DataObject> DataDescriptor<T> build(String dataTableName, Class<T> dataClass) throws PigiException;
}
