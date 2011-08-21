package pigi.framework.general.descriptors.configuration;

import org.apache.hadoop.conf.Configuration;
import pigi.framework.general.PigiException;

public class DataDescriptorFactory {

	public static DataDescriptorProvider fromConfig(HbaseIndexConfigReader configReader) {
	    return new FromConfig(configReader);
	}

    public static DataDescriptorProvider fromHBase(Configuration config) throws PigiException {
        return new FromHBase(config);
    }
    
}
