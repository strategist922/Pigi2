package pigi.framework.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;

// FIXME ============= UWAGA !!! STATYCZNE HTABLE - FULL REUSING - MOZLIWE KONFLIKTY !!!! =============
/**
 * Factory for HTable objects
 */
public class HTableFactory {
	private static Log log = LogFactory.getLog(HTableFactory.class);
	private static Map<String, HTable> tables;
	private static Configuration configuration;

	public HTableFactory() {
		if (tables == null) {
			tables = new HashMap<String, HTable>();
		}
		if (configuration == null){
             configuration = getDefaultConfiguration();
			 log.warn("NO CONFIGURATION - HTableFactory is using default local host master");
			 System.out.println(" NO CONFIGURATION - HTableFactory is using default local host master");
		}
	}


    private Configuration getDefaultConfiguration() {
        // HBaseConfiguration conf = new HBaseConfiguration();
        // conf.set("hbase.master", "127.0.0.1:60000");
        // HTableFactory.setHBaseConfiguration(conf);

        Configuration configuration = HBaseConfiguration.create();
        String zookeeperClientPort = "2181";
        String zookeeperQuorum = "localhost";
        configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
        configuration.set("hbase.zookeeper.property.clientPort", zookeeperClientPort);
        return configuration;
    }

	/**
	 * sets HBaseConfiguration
	 * @param configuration
	 */
	public static void setHBaseConfiguration(Configuration configuration){
		HTableFactory.configuration = configuration;
	}

	/**
	 * returns current HBaseConfiguration
	 * @return
	 */
	public static Configuration getHBaseConfiguration(){
		return configuration;
	}

	/**
	 * returns table
	 *
	 * @param name
	 * @return
	 * @throws HTableFactoryException
	 */
	public HTable getHTable(String name) throws HTableFactoryException {
		try {
			if (!tables.containsKey(name)) {
				HTable table = new HTable(configuration,name);
				tables.put(name, table);
			}
			return tables.get(name);
		} catch (IOException e) {
			throw new HTableFactoryException(e);
		}
	}

	/**
	 * returns hbase admin for table's manipulation
	 *
	 * @return
	 * @throws HTableFactoryException
	 */
	public HBaseAdmin getAdmin() throws HTableFactoryException {
		try {
			return new HBaseAdmin(configuration);
		} catch (MasterNotRunningException e) {
			throw new HTableFactoryException(e);
		} catch (ZooKeeperConnectionException e) {
            throw new HTableFactoryException(e);
        }
	}

}
