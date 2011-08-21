package pigi.framework.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import pigi.framework.general.descriptors.DataDescriptor;

/**
 * Simple tool to create and drop tables in HBase
 * @author kgalecki
 *
 */
public class TableTool {

	private HBaseAdmin admin;

	public TableTool() throws IOException, HTableFactoryException {
        this.admin = new HTableFactory().getAdmin();
	}

	public void createTableIfNotExists(String tableName, String... families) throws IOException {
	    if (admin.tableExists(tableName)) {
			System.out.println(" table " + tableName + " already exists;");
		} else {
	        createTableForSure(tableName, families);
		}
	}

    public void createTable(String tableName, String... families) throws IOException {
        if (admin.tableExists(tableName)) {
            System.out.println(" table " + tableName + " already exists;");
        }
        createTableForSure(tableName, families);
    }

    private void createTableForSure(String tableName, String... families)
            throws IOException {
        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        for (String family : families) {
            tableDescriptor.addFamily(new HColumnDescriptor(family));
        }
        admin.createTable(tableDescriptor);
        System.out.println(" table " + tableName + " created; ");
    }

	public void deleteTable(String tablename) throws IOException {
		if (admin.tableExists(tablename)){
			admin.disableTable(tablename);
			admin.deleteTable(tablename);
			System.out.println(" table " + tablename + " deleted; ");
		} else {
			System.out.println(" table " + tablename + " did not exist.");
		}		
	}
	
	public List<String> listTables(String pattern) throws IOException {
	    List<String> result = new ArrayList<String>();
	    for (HTableDescriptor descriptor : admin.listTables()) {
	        String name = descriptor.getNameAsString();
	        if (name.matches(pattern)) result.add(name);
	    }
	    return result;
	}

}
