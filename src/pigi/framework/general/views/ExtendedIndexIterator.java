package pigi.framework.general.views;

import java.util.Iterator;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.indexes.Serializer;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.DataRow;
import pigi.framework.tools.VScanner;

/**
 * Implementation of iterator for simple index
 */
public class ExtendedIndexIterator<T extends DataObject> implements Iterator<T> {
	protected VScanner scanner;
	protected Iterator<DataRow> iterator;
    private DataDescriptor<T> descriptor;
	
	public ExtendedIndexIterator(VScanner scanner, DataDescriptor<T> descriptor) {
		this.scanner = scanner;
		this.iterator = scanner.iterator();
		this.descriptor = descriptor;
	}
	
	public boolean hasNext() {
		return iterator.hasNext();
	}

	public void remove() {
		iterator.remove();		
	}

	public T next() {
		DataRow row = iterator.next();		
		try {
			Serializer serializationTool = new Serializer();
			return descriptor.buildData(serializationTool.deserialize(row.getCols().get("data")));		
		} catch (IndexException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}			
	}

	public void close(){
		scanner.close();
	}
	
}
