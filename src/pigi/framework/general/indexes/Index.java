package pigi.framework.general.indexes;


import pigi.framework.general.views.query.PageQuery;
import pigi.framework.general.views.query.Query;
import pigi.framework.general.views.results.IterableResult;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.DataObject;

/**
 * Interface of index  classes
 */
public interface Index<T extends DataObject> {

	public String getName();

	public void insert(T object) throws IndexException;

	public void update(T oldObject, T newObject) throws IndexException;

	public void delete(T object) throws IndexException;

	public PageQueryResult<T> getPage(PageQuery<T> query) throws IndexException;

	public IterableResult<T> getAll(Query<T> query) throws IndexException;
	
	public int pageSize();
}
