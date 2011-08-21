package pigi.examples.library.tests;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

import pigi.framework.tools.HTableFactory;


public class LibraryConfiguration {

	public static Configuration configure() {
 	    Configuration config = HBaseConfiguration.create();
		//config.set("hbase.master", "127.0.0.1:60010");
	    config.set("hbase.zookeeper.quorum", "localhost:60010");
	    config.set("hbase.zookeeper.property.clientPort", "2181");
 	   return config;
	}
}
