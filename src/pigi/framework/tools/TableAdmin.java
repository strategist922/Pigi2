package pigi.framework.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldsCollection;
import pigi.framework.general.idgenerators.Keys;
import pigi.framework.general.vo.DataObject;

/**
 * Creates and drops tables based on Descriptor class
 * @author kgalecki
 *
 */
public class TableAdmin<T extends DataObject> {
	private static final String[] INDEX_COLUMNS   = {"dataId", "dataIdIndex", "data"};
    private static final String[] PAGE_COLUMNS    = {"firstId", "page"};
    private static final String[] COUNTER_COLUMNS = {"pageSize", "firstPageId", "lastPageId", "childrenCount", "stickyChildrenCount"};
    public DataDescriptor<T> dataDescriptor;

	public TableAdmin(DataDescriptor<T> objectDescriptor) {
		this.dataDescriptor = objectDescriptor;
	}

	public static <T extends DataObject> TableAdmin<T> build(DataDescriptor<T> dataDescriptor) {
	    return new TableAdmin<T>(dataDescriptor);
	}
	
	public void createTables() {
		try {
			createDataTable();
			createIndexTables();
		} catch (HTableFactoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void createDataTable() throws IOException, HTableFactoryException {
        new TableTool().createTable(dataDescriptor.tableName(), dataDescriptor.familyName());
    }
    
    public void createTables_for_testing(boolean withIndexes) throws IOException, HTableFactoryException {
        createDataTable();

        if (withIndexes) {
            createIndexTables();
        }
    }

    public void createIndexTables() throws IOException, HTableFactoryException {
        TableTool tool = new TableTool();
        for (IndexDescriptor<T> descriptor : dataDescriptor.allIndexDescriptors()) {
            tool.createTableIfNotExists(descriptor.countersTableName(), COUNTER_COLUMNS);
            tool.createTableIfNotExists(descriptor.pagesTableName(),    PAGE_COLUMNS);
            tool.createTableIfNotExists(descriptor.indexTableName(),    INDEX_COLUMNS);
        }
    }

    public void dropIndexes() {
        try {
            TableTool tool = new TableTool();

            // drop table for indexes
            for (String name : listIndexTables(dataDescriptor.tableName())) {
                tool.deleteTable(name);
            }
        } catch (HTableFactoryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> listIndexTables(String tableName) throws IOException, HTableFactoryException {
        TableTool tool = new TableTool();
        return tool.listTables(tableName + Keys.ESCAPED_NAME_SEPARATOR + ".*");
    }

	public void dropTables() {
		try {
			TableTool tool = new TableTool();

			// drop data table
			tool.deleteTable(dataDescriptor.tableName());
			dropIndexes();
		} catch (HTableFactoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
