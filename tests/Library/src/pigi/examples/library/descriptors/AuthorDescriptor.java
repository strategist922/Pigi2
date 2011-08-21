package pigi.examples.library.descriptors;

import java.util.List;

import pigi.examples.library.vo.Author;
import pigi.examples.library.vo.Names;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexedField;
import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.desriptors.fields.Field;
import pigi.framework.general.desriptors.fields.FieldOrder;


/**
 * this class describe Author object - how to index all authors
 *       we need four indexes for this object (please look into presentation: http://docs.google.com/Present?docid=dhsz359t_2fgbm9x32)
 *         1) author by surname
 *         2) author by name + surname
 *         3) all authors ordered by surname ASC, name ASC
 *         4) all authors ordered by surname DESC, name DESC
 * @author acure
 *
 */
public class AuthorDescriptor extends DataDescriptor<Author> {

    private static final Field SURNAME_FIELD = new Field(Names.SURNAME);
    private static final Field NAME_FIELD    = new Field(Names.NAME);

    private static final IndexedField NAME_INDEX    = new ValueField(NAME_FIELD);
    private static final IndexedField SURNAME_INDEX = new ValueField(SURNAME_FIELD);
	
	private static final List<IndexedField> NO_PROPERTIES_INDEX = listOf();

    private static final List<FieldOrder> BY_SURNAME_AND_NAME_ASC  = listOf(SURNAME_FIELD.ascending(),  NAME_FIELD.ascending());
    private static final List<FieldOrder> BY_SURNAME_AND_NAME_DESC = listOf(SURNAME_FIELD.descending(), NAME_FIELD.descending());

    public AuthorDescriptor() {
		super("authors", Author.class); // data table name in hbase
		
		addField(NAME_FIELD);      // name is mapped as "name" column family and short name "n", max length 20 , not null 
		addField(SURNAME_FIELD);     
		
		//-------------------------------------------------------------------------------------------------------------------------
		// 1) index for indexing only "surname" field
		//-------------------------------------------------------------------------------------------------------------------------
			
		// next we create index 			
		addSimpleIndex(
					10, 								// page size
					listOf(SURNAME_INDEX), 				// list of indexed fields - declared a few lines above
					BY_SURNAME_AND_NAME_ASC 					// list of order fields - declared a few lines above
			);
		
		//-------------------------------------------------------------------------------------------------------------------------
		// 2) index for indexing "name + surname" property
		//-------------------------------------------------------------------------------------------------------------------------
			
		// next we create index 			
	        addSimpleIndex(
					10, 									// page size
					listOf(NAME_INDEX, SURNAME_INDEX), 				// list of indexed fields - declared a few lines above
					BY_SURNAME_AND_NAME_ASC 					// list of order fields - declared a few lines above
			);
		
		//-------------------------------------------------------------------------------------------------------------------------
		// 3) index for all authors ordered by surname ASC and name ASC
		//-------------------------------------------------------------------------------------------------------------------------	
	
	        addSimpleIndex(
					10, 
					NO_PROPERTIES_INDEX, 
					BY_SURNAME_AND_NAME_ASC);
		
		//-------------------------------------------------------------------------------------------------------------------------
		// 3) index for all authors ordered by surname DESC and name DESC
		//-------------------------------------------------------------------------------------------------------------------------	
		
        addSimpleIndex(
                    10, 
					NO_PROPERTIES_INDEX, 
					BY_SURNAME_AND_NAME_DESC);		
	}
}
