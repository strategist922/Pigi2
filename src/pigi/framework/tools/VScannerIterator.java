package pigi.framework.tools;

import java.util.Iterator;

import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;

/**
 * Representation of ResultScanner iterator
 * @author kgalecki
 *
 */
public class VScannerIterator implements Iterator<DataRow> {
	private ResultScanner ResultScanner;
	private RowTool rowTool;
	private String stopRow;
	private Long limit;
	private int counter;
	private Iterator<Result> iterator;
	private DataRow tempRow; // temporary object


	public VScannerIterator(ResultScanner ResultScanner, Long limit){
		this(ResultScanner, null, limit);
	}

	public VScannerIterator(ResultScanner ResultScanner, String stopRow){
		this(ResultScanner, stopRow, null);
	}

	public VScannerIterator(ResultScanner ResultScanner, String stopRow, Long limit) {
		this.counter = 0;
		this.ResultScanner = ResultScanner;
		this.iterator = ResultScanner.iterator();
		this.rowTool = new RowTool();
		this.stopRow = stopRow;
		this.limit = limit;
	}

	public boolean hasNext() {
		//** new item
		if (!iterator.hasNext()){
			return false;
		}

		try {
			tempRow = rowTool.resultToRow(iterator.next());
		} catch (RowToolException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		if (tempRow == null){
			return false;
		}

		if (stopRow != null){
			if ((tempRow != null)&&(tempRow.getId().compareTo(stopRow) >= 0)){
				tempRow = null;
				return false;
			}
		}
		if (limit != null){
			if (counter >= limit){
				tempRow = null;
				return false;
			}
		}
		return true;
	}


	public DataRow next() {
		counter++;
		return tempRow;
	}

	public void remove() {
		this.iterator.remove();
	}

	public void close() {
		this.ResultScanner.close();
	}

}
