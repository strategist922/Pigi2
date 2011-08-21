package pigi.examples.library.descriptors;

import java.util.Arrays;
import pigi.examples.library.vo.Category;
import pigi.examples.library.vo.Names;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.SimpleIndexDescriptor;
import pigi.framework.general.desriptors.fields.Field;

/**
 * this class describe Category object 
 *       we need only single index 
 *         1) all categories ordered by name ASC
 * @author acure
 *
 */
public class CategoryDescriptor extends DataDescriptor<Category> {

	
	public CategoryDescriptor() {
		super("categories", Category.class); // categories is a name of data table in hbase
		Field name = addField(new Field(Names.NAME));    // name is mapped as "name" column family and short name "n", max length 20 , not null   		
		
        addIndex(new SimpleIndexDescriptor<Category>(
					tableName(),				// name
					10, 							// page size
					Arrays.<IndexedField>asList(), 				// list of indexed fields - declared a few lines above
					Arrays.asList(name.ascending())
			));
	}
	
}
