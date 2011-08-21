package pigi.framework.general.indexes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hbase.client.HTable;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.idgenerators.GeneralFieldsCountersIdGenerator;
import pigi.framework.general.idgenerators.GeneralFieldsIndexIdGenerator;
import pigi.framework.general.idgenerators.GeneralFieldsPagesIdGenerator;
import pigi.framework.general.idgenerators.IdGeneratorForIndexes;
import pigi.framework.general.views.SimpleIndexIterator;
import pigi.framework.general.views.query.PageQuery;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.HTableFactoryException;
import pigi.framework.tools.RowToolException;
import pigi.framework.tools.DataRow;
import pigi.framework.tools.VScanner;

/**
 * Implementation of simple index
 */
public class SimpleIndex<T extends DataObject> extends AbstractIndex<T> {
	private static final String[] DATAID_COLUMN = new String[] { "dataId" };
	String name;
    private HTable dataTable;

	public SimpleIndex(IndexDescriptor<T> indexDescriptor, DataDescriptor<T> objectDescriptor) throws IndexException {
		super(indexDescriptor, objectDescriptor);
		this.name = indexDescriptor.getName();
		try {
			this.dataTable = tableFactory.getHTable(objectDescriptor.tableName());
		} catch (HTableFactoryException e) {
			throw new IndexException(e);
		}
	}

	@Override
	Map<String, String> prepareIndexFields(DataObject object) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataId", object.getId());
		return map;
	}

	public void update(T oldObject, T newObject) throws IndexException {
		
		List<String> changedFields = new ArrayList<String>();
		for(String fieldName : newObject.getFields().keySet()){
			if (oldObject.getFields().containsKey(fieldName)){
				if (!oldObject.getField(fieldName).equals(newObject.getField(fieldName))){
					changedFields.add(fieldName);
				}
			} else {				
				changedFields.add(fieldName);
			}			
		}
		
		Boolean update = false;
		for(String fieldName : changedFields) {			
			logger.debug(" changed field : " + fieldName);
			if (indexDescriptor.hasField(fieldName)) {
				logger.debug(" index : " + indexDescriptor.getName() + " need update " + fieldName);
				update = true;
				break;
			}
		}
		
		if (update) {
			// TODO optimize
			delete(oldObject);
			insert(newObject);
		}
		else {
			// do nothing
		}		
		
	}

	@Override
    protected Iterator<T> resultIterator(String firstId, String lastId)
            throws RowToolException {
        VScanner scanner = tableTool.getVScanner(indexTable, firstId, lastId, DATAID_COLUMN);
        Iterator<T> iterator = new SimpleIndexIterator<T>(scanner, dataTable, dataDescriptor);
        return iterator;
    }

	// TODO(vlad): figure out how come it returns GeneralObjects, not specific ones.
	public PageQueryResult<T> getPage(PageQuery<T> query) throws IndexException {
		int pageNo = query.getPageNo();
		try {
			String[] dataCols = DATAID_COLUMN;

			IdGeneratorForIndexes indexIdGenerator = new GeneralFieldsIndexIdGenerator<T>(dataDescriptor.getIndexDescriptor(name));
			GeneralFieldsPagesIdGenerator<T> pagesIdGenerator = new GeneralFieldsPagesIdGenerator<T>(dataDescriptor.getIndexDescriptor(name), query.getProperties());
			GeneralFieldsCountersIdGenerator<T> countersIdGenerator = new GeneralFieldsCountersIdGenerator<T>(dataDescriptor.getIndexDescriptor(name));

			// wyciagamy pageSize i lastPageId
			String countersId = countersIdGenerator.getId(query.getProperties());
			Map<String, String> row = tableTool.getRow(countersTable, countersId, "childrenCount", "pageSize", "lastPageId");
			int pageSize = indexDescriptor.pageSize();
			// Integer pageSize = new Integer(row.get("pageSize"));
			// Integer childrenCount = new Integer(row.get("childrenCount")); //czy jak jest == null to nie powinno byc 0 ?
			String lastPageId = row.get("lastPageId");

			if ((lastPageId == null) || (pagesIdGenerator.getOrderNo(lastPageId) < pageNo)) {
				pageNo = 0;
			}

			// ustalamy firstId
			String firstId;
			if (pageNo == 0) {
				firstId = indexIdGenerator.getFirstPossibleId(query.getProperties());
			} else {
				firstId = tableTool.get(pagesTable, pagesIdGenerator.getIdByOrderNo(pageNo), "firstId");
				if (firstId == null) {
					throw new IndexException(" null in firstId for index: " + name + " :: " + pagesIdGenerator.getIdByOrderNo(pageNo));
				}
			}

			// get (pageSize + 1) elements
			String correctNextPageFirstId = null;
			List<String> result = new ArrayList<String>();
			String lastId = indexIdGenerator.getLastPossibleId(query.getProperties());
			VScanner scanner = tableTool.newVScanner(indexTable, firstId, lastId, pageSize + 1, dataCols);

            int i = 0;
			for (DataRow rowResult : scanner) {
				i++;
				if (i > pageSize) {
                    correctNextPageFirstId = rowResult.getId();
                    break;
				}
				result.add(rowResult.getCols().get("dataId"));
			}
			scanner.close();

			// get firstId of next page (if exists)
			// optional corrections in pages
			if ((lastPageId != null) 
			   && (pagesIdGenerator.getOrderNo(lastPageId) > pageNo)) { // jesli strona o ktora pytamy nie jest ostatnia
				String nextPageFirstId = tableTool.get(pagesTable, pagesIdGenerator.getIdByOrderNo(pageNo + 1), "firstId");
				if (correctNextPageFirstId == null) {
					logger.debug("no page found, while expecting one");
					String newLastPageId = pagesIdGenerator.getPrevId(lastPageId);
					Map<String, String> map = new HashMap<String, String>();
					map.clear();
					map.put("lastPageId", newLastPageId);
					tableTool.update(countersTable, countersId, map);
					logger.debug(" remove page ");
					tableTool.delete(pagesTable, lastPageId);
				} else if (!correctNextPageFirstId.equals(nextPageFirstId)) {
				    logger.debug(" next page first id correction ! ");
					// correcting entry for nextPage and set correct firstId
					Map<String, String> map = new HashMap<String, String>();
					map.put("firstId", correctNextPageFirstId);
					tableTool.update(pagesTable, pagesIdGenerator.getIdByOrderNo(pageNo + 1), map);
				}
			} else if (correctNextPageFirstId != null) {
				// insert to pages
				logger.debug(" last page first id correction - create page ! ");
				String pageId = pagesIdGenerator.getIdByOrderNo(pageNo + 1);
				Map<String, String> map = new HashMap<String, String>();
				map.put("firstId", correctNextPageFirstId);
				tableTool.update(pagesTable, pageId, map);
				// insert to counters
				logger.debug("last page first id being fixed from " + map.get("lastPageId") + " to " + pageId);
				map.clear();
				map.put("lastPageId", pageId);
				tableTool.update(countersTable, countersId, map);
			}

			// generating objects
			List<T> objects = new ArrayList<T>();
			for (String oid : result) {
				// odtwarzamy obiekt z id
				Map<String, String> fields = tableTool.getRow(dataTable, oid);
				objects.add(dataDescriptor.buildData(new DataObject(oid, fields)));
			}

			int maxPage = 0;
			if ((lastPageId != null) && (lastPageId != "")) {
				int lastPageNo = pagesIdGenerator.getOrderNo(lastPageId);
				if (lastPageNo > pageNo) {
					maxPage = lastPageNo;
				} else if (correctNextPageFirstId != null) {
					maxPage = pageNo + 1;
				} else {
					maxPage = pageNo;
				}
			} else if (correctNextPageFirstId != null) {
				maxPage = pageNo + 1;
			}
			return new PageQueryResult<T>(pageNo, maxPage, objects);

		} catch (RowToolException e) {
			throw new IndexException(e);
		}
	}

}
