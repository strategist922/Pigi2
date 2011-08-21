package pigi.framework.general.views;

import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.RowTool;
import pigi.framework.tools.RowToolException;
import pigi.framework.tools.DataRow;
import pigi.framework.tools.VScanner;

/**
 * Implementation of iterator for simple index strategy 
 */
public class SimpleIndexIterator<T extends DataObject> implements Iterator<T>{
	protected VScanner scanner;
	protected Iterator<DataRow> iterator;
	protected RowTool rowTool;
    private DataDescriptor<T> objectDescriptor;
    private HTable table;

	public SimpleIndexIterator(VScanner scanner, HTable table, DataDescriptor<T> objectDescriptor) {
		this.scanner = scanner;
		this.iterator = scanner.iterator();
		this.rowTool = new RowTool();
		this.table = table;
		this.objectDescriptor = objectDescriptor;
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
			Map<String, String> fields = rowTool.getRow(table, row.getCols().get("dataId"));
			return objectDescriptor.buildData(new DataObject(row.getCols().get("dataId"), fields));
		} catch (RowToolException e) {
			throw new RuntimeException(e);
		}
	}

	public void close(){
		scanner.close();
	}

}
