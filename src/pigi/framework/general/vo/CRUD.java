package pigi.framework.general.vo;

/**
 * Interface for create/read/update/delete
 * @param <T> data class
 */
public interface CRUD<T extends DataObject> {

	public String insert(DataObject object) throws CRUDException;

	public void update(T object) throws CRUDException;

	public void delete(String id) throws CRUDException;

	public T get(T object) throws CRUDException;
}
