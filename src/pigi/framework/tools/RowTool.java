package pigi.framework.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;

/**
 * RowTool class - performs operations on HBase
 * @author kgalecki
 *
 */
public class RowTool {
	private static final byte[] NO_NAME = "".getBytes();
    private static Log logger = LogFactory.getLog(RowTool.class);
	private String encoding;

	public RowTool() {
		this.encoding = HConstants.UTF8_ENCODING;
	}
	
	public byte[] bytes(String s) {
	    try {
	        return s.getBytes(encoding);
	    } catch (UnsupportedEncodingException uee) {
	        return s.getBytes(); // Java wtf
	    }
	}
    
    public String string(byte[] bytes) {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException uee) {
            return new String(bytes); // Java wtf
        }
    }

	/**
	 * inserts all elements into table with id
	 * 
	 * @param table
	 * @param id
	 * @param elements
	 * @throws RowToolException
	 */
	public void insert(HTable table, String id, Map<String, String> elements) throws RowToolException {
        logger.debug(" inseting into table " + new String(table.getTableName()) + ", row " + id);
		put(table, id, elements);
	}

    public void insert(HTable table, String family, String id, Map<String, String> fields) throws RowToolException {
        logger.debug(" inseting into table " + new String(table.getTableName()) + ", family " + family + ", row " + id);
        put(table, family, id, fields);
    }

	/**
	 * update table row with id
	 * 
	 * @param table
	 * @param id
	 * @param elements
	 * @throws RowToolException
	 */
	public void update(HTable table, String id, Map<String, String> elements) throws RowToolException {
        logger.debug(" update table " + new String(table.getTableName()) + ", row " + id);
		put(table, id, elements);
	}

    public void update(HTable table, String family, String id, Map<String, String> fields) throws RowToolException {
        logger.debug(" update table " + new String(table.getTableName()) + ", family " + family + ", row " + id);
        put(table, family, id, fields);
    }

	/**
	 * removes whole data from row
	 * 
	 * @param table
	 * @param id
	 * @throws RowToolException
	 */
	public void delete(HTable table, String id) throws RowToolException {
		try {
			table.delete(new Delete(bytes(id)));
		} catch (UnsupportedEncodingException e) {
			throw new RowToolException(e);
		} catch (IOException e) {
			throw new RowToolException(e);
		}
	}

	/**
	 * execute batch update - for insert and update
	 * 
	 * @param table
	 * @param id
	 * @param elements
	 * @throws RowToolException
	 */
	private void put(HTable table, String id, Map<String, String> elements) throws RowToolException {
        Put put = new Put(bytes(id));
		try {
			for (Entry<String, String> entry : elements.entrySet()) {
				put.add(bytes(entry.getKey().split(":")[0]), NO_NAME, bytes(entry.getValue()));
			}
			table.put(put);
			table.flushCommits();
		} catch (Exception e) {
			throw new RowToolException(
			        "Tried to add to \'" + new String(table.getTableName()) + "\':\'" + id + "\'->" + elements,
			        e);
		}
	}

    private void put(HTable table, String family, String id, Map<String, String> elements) throws RowToolException {
        Put put = new Put(bytes(id));
        try {
            for (Entry<String, String> entry : elements.entrySet()) {
                put.add(bytes(family), bytes(entry.getKey()), bytes(entry.getValue()));
            }
            table.put(put);
            table.flushCommits();
        } catch (Exception e) {
            throw new RowToolException(
                    "Tried to add to \'" + new String(table.getTableName()) + "\':\'" + id + "\'->" + elements,
                    e);
        }
    }

	/**
	 * gets row as a map of strings
	 * 
	 * @param table
	 * @param id
	 * @return
	 * @throws RowToolException
	 */
	public Map<String, String> getRow(HTable table, String id) throws RowToolException {
	    if (id == null) {
	        throw new NullPointerException("id can't be null");
	    }
	    return getRow(table, bytes(id));
	}

	public Map<String, String> getRow(HTable table, byte[] rowKey) throws RowToolException {
        try {
            return resultToMap(table.get(new Get(rowKey)));
        } catch (UnsupportedEncodingException e) {
            throw new RowToolException(e);
        } catch (IOException e) {
            throw new RowToolException(e);
        }
    }

	/**
	 * gets specified columns from row as a map of strings
	 * 
	 * @param table
	 * @param id
	 * @param colname
	 * @return
	 * @throws RowToolException
	 */
	public Map<String, String> getRow(HTable table, String id, String... colname) throws RowToolException {
		try {
	        Result result = table.get(newGet(id, colname));
			return resultToMap(result);
		} catch (IOException e) {
			throw new RowToolException(e);
		}
	}

    private Get newGet(String id, String... colnames) {
        Get get = new Get(bytes(id));
        for (String col : colnames) {
            get.addColumn(bytes(col));
        }
        return get;
    }

	/**
	 * gets value of single col
	 * 
	 * @param table
	 * @param id
	 * @param col
	 * @return
	 * @throws RowToolException
	 */
	public String get(HTable table, String id, String col) throws RowToolException {
        return getRow(table, id, col).get(col);
	}

    /**
     * gets value of single col
     * 
     * @param table
     * @param id
     * @param col
     * @return
     * @throws RowToolException
     */
    public int getInt(HTable table, String id, String col, int defaultValue) throws RowToolException {
        String s = getRow(table, id, col).get(col);
        try {
            if (s != null) return Integer.parseInt(s);
        } catch (Exception x) {}
        
        return defaultValue;
    }

	/**
	 * change Result into map of strings
	 * 
	 * @param Result
	 * @return
	 * @throws RowToolException
	 */
	public Map<String, String> resultToMap(Result result) throws RowToolException {
		Map<String, String> map = new HashMap<String, String>();
		NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> resultMap = getResultMap(result);
        for (byte[] family : resultMap.keySet()) {
            String familyName = string(family);
            for (byte[] column : resultMap.get(family).keySet()) {
                String col = string(column);
                String key = col.isEmpty() ? familyName : col;
                Entry<Long, byte[]> cell = resultMap.get(family).get(column).firstEntry();
                map.put(key, string(cell.getValue()));
            }
        }
		return map;
	}

	private final static <K, V> NavigableMap<K, V> emptyMap() {
	    return new TreeMap<K, V>();
	}
	
    private NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> getResultMap(Result result) {
        if (result == null || result.getMap() == null) return emptyMap();
        return result.getMap();
    }

	/**
	 * returns VRow object instead of Result
	 * 
	 * @param Result
	 * @return
	 * @throws RowToolException
	 */
	public DataRow resultToRow(Result Result) throws RowToolException {
		return new DataRow(string(Result.getRow()), resultToMap(Result));
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 
	 * @param table
	 * @param startId
	 * @param stopId
	 * @param columns
	 * @return
	 * @throws RowToolException
	 */
	public VScanner getVScanner(HTable table, String startId, String stopId, String... columns) throws RowToolException {
		try {
            return new VScanner(newScanner(table, startId, stopId, columns), stopId);
		} catch (IOException e) {
			throw new RowToolException(e);
		}
	}

    private ResultScanner newScanner(HTable table, String startId,
            String stopId, String... columns) throws IOException {
        Scan scan = new Scan(bytes(startId), bytes(stopId));
        return newScanner(table, scan, columns);
    }

    private ResultScanner newScanner(HTable table, Scan scan, String... columns)
            throws IOException {
        for (String column : columns) {
            scan.addColumn(bytes(column));
        }
        return table.getScanner(scan);
    }

    private ResultScanner newScanner(HTable table, String startId,
            String... columns) throws IOException {
        Scan scan = new Scan(bytes(startId));
        return newScanner(table, scan, columns);
    }

	/**
	 * 
	 * @param table
	 * @param startId
	 * @param stopId
	 * @param limit
	 * @param columns
	 * @return
	 * @throws RowToolException
	 */
	public VScanner newVScanner(HTable table, String startId, String stopId, long limit, String... columns) throws RowToolException {
		try {
			return new VScanner(newScanner(table, startId, stopId, columns), stopId, limit);
		} catch (IOException e) {
			throw new RowToolException(e);
		}
	}

	/**
	 * 
	 * @param table
	 * @param startId
	 * @param limit
	 * @param columns
	 * @return
	 * @throws RowToolException
	 */
	public VScanner newVScanner(HTable table, String startId, long limit, String... columns) throws RowToolException {
		try {
			return new VScanner(newScanner(table, startId, columns), limit);
		} catch (IOException e) {
			throw new RowToolException(e);
		}
	}

}
