package pigi.examples.library.descriptors;

import pigi.examples.library.vo.Book;
import pigi.examples.library.vo.Names;
import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.ValueField;
import pigi.framework.general.desriptors.fields.Field;


/**
 * this class describe Book object - how to index books
 *       we need five indexes for this object (please look into presentation: http://docs.google.com/Present?docid=dhsz359t_2fgbm9x32)
 *         1) books by authorId ordered by title ASC
 *         2) books by categoryId ordered by title ASC
 *         3) books by categoryId ordered by title DESC
 *         4) books by categoryId, authorId ordered by title ASC
 *         5) books by ISBN
 * @author acure
 *
 */

public class BookDescriptor extends DataDescriptor<Book> {

	
	public BookDescriptor() {
		super("books", Book.class);
		
		// next we declare fields descriptors :
		//             FieldDescriptor(name, shortName)                  
		Field authorId	 = addField(new Field(Names.AUTHOR_ID));
		Field categoryId = addField(new Field(Names.CATEGORY_ID));      		
		Field title  	 = addField(new Field(Names.TITLE));     
		Field isbn   	 = addField(new Field(Names.ISBN));
		
        addSimpleIndex(
					10, 								// page size
					listOf(new ValueField(authorId)), 				// list of indexed fields
					listOf(title.ascending()) 					// list of order fields
			);
	
		//-------------------------------------------------------------------------------------------------------------------------
		// 2) index books by categoryId ordered by title ASC
		//-------------------------------------------------------------------------------------------------------------------------
		
        addSimpleIndex(
					10, 								// page size
					listOf(new ValueField(categoryId)), // list of indexed fields
					listOf(title.ascending()) 					// list of order fields
			);

		//-------------------------------------------------------------------------------------------------------------------------
		// 3) index books by categoryId ordered by title ASC
		//-------------------------------------------------------------------------------------------------------------------------

		// next we create index 			
        addSimpleIndex(
					10, 								// page size
					listOf(new ValueField(categoryId)), // list of indexed fields
					listOf(title.descending()) 					// list of order fields - declared a few lines above
			);
		
		//-------------------------------------------------------------------------------------------------------------------------
		// 4) index books by categoryId, authorId ordered by title ASC
		//-------------------------------------------------------------------------------------------------------------------------
			
		// next we create index 			
        addSimpleIndex(
					10, 								// page size
					listOf(new ValueField(categoryId),
                           new ValueField(authorId)), 				// list of indexed fields - declared a few lines above
                    listOf(title.ascending()) 					// list of order fields - declared a few lines above
			);

		//-------------------------------------------------------------------------------------------------------------------------
		// 5) index books by ISBN 
		//-------------------------------------------------------------------------------------------------------------------------
		
		// next we create index 			
        addSimpleIndex(
					10, 								// page size
					listOf(new ValueField(isbn)), 				// list of indexed fields - declared a few lines above
					NO_ORDER 					// list of order fields - declared a few lines above
			);
	}

}
