package pigi.framework.tools;

import java.util.Iterator;

import org.apache.hadoop.hbase.client.ResultScanner;

/**
 * Representation of single ResultScanner - based on Hbase ResultScanner implementation
 * @author acure
 *
 */
public class VScanner implements Iterable<DataRow> {
	private VScannerIterator scannerIterator; // iterator

	/**
	 *  
	 * @param ResultScanner - HBase ResultScanner
	 * @param limit - how many rows you need
	 */
	public VScanner(ResultScanner ResultScanner, Long limit){
		this.scannerIterator = new VScannerIterator(ResultScanner, limit);
	}

	/**
	 * 
	 * @param ResultScanner hbase canner
	 * @param stopRowId stop rowId
	 */
	public VScanner(ResultScanner ResultScanner, String stopRowId){
		this.scannerIterator = new VScannerIterator(ResultScanner, stopRowId);
	}

	/**
	 * stops when limit or stop rowId
	 * @param ResultScanner hbase ResultScanner
	 * @param stopRowId - stop rowId
	 * @param limit - limit
	 */
	public VScanner(ResultScanner ResultScanner, String stopRowId, Long limit) {
		this.scannerIterator = new VScannerIterator(ResultScanner, stopRowId, limit);
	}

	public Iterator<DataRow> iterator() {
		return this.scannerIterator;
	}

	/**
	 * close scanneriterator
	 *
	 */
	public void close(){
		scannerIterator.close();
	}
}
