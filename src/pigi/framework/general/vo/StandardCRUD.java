package pigi.framework.general.vo;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;

import pigi.framework.general.idgenerators.Keys;
import pigi.framework.tools.RowTool;
import pigi.framework.tools.RowToolException;
import pigi.framework.tools.DataRow;

/**
 * Tool for VO objects
 * @param <T> VO class
 */
public class StandardCRUD<T extends DataObject> implements CRUD<T> {
	private HTable dataTable;
	private RowTool rowTool;
    private String familyName;

	public StandardCRUD(HTable dataTable, String familyName) {
		this.rowTool = new RowTool();
		this.dataTable = dataTable;
		this.familyName = familyName;
	}

	public void delete(String id) throws CRUDException {
		try {
			rowTool.delete(dataTable, id);
		} catch (RowToolException e) {
			throw new CRUDException(e);
		}
	}

	public String insert(DataObject object) throws CRUDException {
		try {
			if (object.getId() == null) {
				Keys idtool = new Keys();
				object.setId(idtool.newId());
			}
			rowTool.insert(dataTable, familyName, object.getId(), object.getFields());
			return object.getId();
		} catch (RowToolException e) {
			throw new CRUDException(e);
		}
	}

	public void update(T object) throws CRUDException {
		try {
			if (object.getId() == null) {
				throw new CRUDException(" update VO object must have id != null !");
			}
			rowTool.update(dataTable, familyName, object.getId(), object.getFields());
		} catch (RowToolException e) {
			throw new CRUDException(e);
		}

	}

	@SuppressWarnings("unchecked")
    public T get(T prototype) throws CRUDException {
		try {
		    String key = prototype.getId();
			DataRow row = rowTool.resultToRow(dataTable.get(new Get(key.getBytes())));
			Class<T> dataClass = (Class<T>) prototype.getClass();
            return Factory.build(dataClass, key, row.getCols());
		} catch (RowToolException e) {
			throw new CRUDException(e);
		} catch (IOException e) {
			throw new CRUDException(e);
		}
	}

}
