package pigi.framework.general.views.query;

import java.util.Map;

import pigi.framework.general.indexes.Index;
import pigi.framework.general.indexes.IndexException;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.DataObject;

/**
 * Page query
 */
public class PageQuery<T extends DataObject> extends Query<T> {
	private int pageNo;
	
    public PageQuery(Index<T> index, Map<String, String> properties, int pageNo) {
		super(index, properties);
		this.pageNo = pageNo;	
	}

	public int getPageNo() {
		return pageNo;
	}

    public PageQueryResult<T> getPage() throws IndexException {
        return index.getPage(this);
    }


}
