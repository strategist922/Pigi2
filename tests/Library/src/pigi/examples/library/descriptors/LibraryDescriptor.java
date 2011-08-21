package pigi.examples.library.descriptors;

import java.util.Arrays;

import pigi.examples.library.vo.Library;
import pigi.examples.library.vo.Names;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.desriptors.fields.Field;

/**
 * this class is a descriptor of the Library
 *       we need only one index for libraries list
 *         1) all libraries ordered by name ASC
 * @author acure
 *
 */
public class LibraryDescriptor extends DataDescriptor<Library> {
	/**
	 * constructor  
	 * @throws GeneralFieldDescriptorsCollectionException 
	 * @throws GeneralFieldObjectDescriptorException 
	 */
	public LibraryDescriptor() {
		super("libraries", Library.class); // libraries is a name of data table in hbase
		
		// next we declare fields descriptors :
		//             FieldDescriptor( name, shortName, length, notNull )                  
		Field name = addField(new Field(Names.NAME));    // name is maped as "name" column family and short name "n", max length 20 , not null 
					 addField(new Field(Names.ADDRESS));   // there we don't need variable - address field isn't indexed - and we don't need to this definition later.  
		
		// next we create index 		
		addSimpleIndex(
					10, 							// page size
					Arrays.<IndexedField>asList(), 				// list of indexed fields - declared a few lines above
					Arrays.asList(name.ascending()));
	}

}
