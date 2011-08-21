package pigi.framework.general.views.query;

import java.util.Map;

import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.views.results.IterableResult;
import pigi.framework.general.vo.DataObject;

/**
 * View query (with index name but without page number)
 */
public class IndexViewQuery<T extends DataObject> extends Query<T> {
	public IndexViewQuery(Index<T> index, Map<String, String> properties){
		super(index, properties);
	}
	
	public IterableResult<T> getAll() throws IndexException {
        return index.getAll(this);
    }
}
