package pigi.framework.general.indexes;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pigi.framework.general.descriptors.DataDescriptor;
import pigi.framework.general.descriptors.IndexDescriptor;
import pigi.framework.general.idgenerators.GeneralFieldsCountersIdGenerator;
import pigi.framework.general.idgenerators.GeneralFieldsIndexIdGenerator;
import pigi.framework.general.idgenerators.GeneralFieldsPagesIdGenerator;
import pigi.framework.general.idgenerators.IdGeneratorForIndexes;
import pigi.framework.general.views.ExtendedIndexIterator;
import pigi.framework.general.views.query.PageQuery;
import pigi.framework.general.views.results.PageQueryResult;
import pigi.framework.general.vo.DataObject;
import pigi.framework.tools.RowToolException;
import pigi.framework.tools.DataRow;
import pigi.framework.tools.VScanner;

/**
 * Implementation of extended index  (contains indexed data within index)
 */
public class ExtendedIndex<T extends DataObject>  extends AbstractIndex<T> {

	private static final String[] DATA_COLS = new String[] { "data" };

    public ExtendedIndex(IndexDescriptor<T> indexDescriptor, DataDescriptor<T> dataDescriptor) throws IndexException{
		super(indexDescriptor, dataDescriptor);
	}
	
	@Override
	Map<String, String> prepareIndexFields(DataObject object) throws IndexException {
		Serializer serializationTool = new Serializer();
		Map<String, String> map = new HashMap<String, String>();
		map.put("dataId", object.getId());
		map.put("data", serializationTool.serializeFields(object));
		return map;
	}

    public void update(T oldObject, T newObject) throws IndexException {
		delete(oldObject);
		insert(newObject);

//TODO: wywalic to jezeli jest niepotrzebne!!		
//		GeneralObject old = new GeneralObjectVO(newObjectVO.getId());
//		try {
//			old.setFields(tableTool.getRow(dataTable, newObjectVO.getId()));
//			delete(old);
//			insert(newObjectVO);
//		} catch (RowToolException e) {
//			throw new IndexException(e);
//		}			
	}

    @Override
    protected Iterator<T> resultIterator(String firstId, String lastId) throws RowToolException {
        return new ExtendedIndexIterator<T>(tableTool.getVScanner(indexTable, firstId, lastId, DATA_COLS), dataDescriptor);
    }

	public PageQueryResult<T> getPage(PageQuery query) throws IndexException {
		int page = query.getPageNo();
		try {
			String[] dataCols = new String[] { "dataId", "data" };

			IdGeneratorForIndexes indexIdGenerator = new GeneralFieldsIndexIdGenerator<T>(dataDescriptor.getIndexDescriptor(getName()));
			GeneralFieldsPagesIdGenerator<T> pagesIdGenerator = new GeneralFieldsPagesIdGenerator<T>(dataDescriptor.getIndexDescriptor(getName()), query.getProperties());
			GeneralFieldsCountersIdGenerator<T> countersIdGenerator = new GeneralFieldsCountersIdGenerator<T>(dataDescriptor.getIndexDescriptor(getName()));

			// get pageSize and lastPageId
			String countersId = countersIdGenerator.getId(query.getProperties());
			Map<String, String> row = tableTool.getRow(countersTable, countersId, new String[] { "childrenCount", "pageSize", "lastPageId" });
			Integer pageSize = indexDescriptor.pageSize();
			//TODO: wywalic to jezeli jest niepotrzebne!!			
			// Integer pageSize = new Integer(row.get("pageSize"));
			// Integer childrenCount = new Integer(row.get("childrenCount")); // czy jak jest == null to nie powinno byc 0 ?
			String lastPageId = row.get("lastPageId");

			if ((lastPageId == null) || (pagesIdGenerator.getOrderNo(lastPageId) < page)) {
				page = 0;
			}

			// determine firstId
			String firstId;
			if (page == 0) {
				firstId = indexIdGenerator.getFirstPossibleId(query.getProperties());
			} else {
				firstId = tableTool.get(pagesTable, pagesIdGenerator.getIdByOrderNo(page), "firstId");
				if (firstId == null) {
					throw new IndexException(" null in firstId for index: " + getName() + " :: " + pagesIdGenerator.getIdByOrderNo(page));
				}
			}

			// get (pageSize + 1) elements
			int i = 0;
			String correctNextPageFirstId = null;
			List<String> result = new ArrayList<String>();
			String lastId = indexIdGenerator.getLastPossibleId(query.getProperties());
			VScanner scanner = tableTool.newVScanner(indexTable, firstId, lastId, pageSize.longValue() + 1, dataCols);

			for (DataRow rowResult : scanner) {
				i++;
				if (i <= pageSize) {
					result.add(rowResult.getCols().get("data"));
				}
				if (i > pageSize) {
					correctNextPageFirstId = rowResult.getId();
				}
			}
			scanner.close();

			// get firstId of next page (if exists)
			// optional corrections in pages
			if ((lastPageId != null) && (pagesIdGenerator.getOrderNo(lastPageId) > page)) { // jesli strona o ktora pytamy nie jest ostatnia
				String nextPageFirstId = tableTool.get(pagesTable, pagesIdGenerator.getIdByOrderNo(page + 1), "firstId");
				if (correctNextPageFirstId == null) {
					logger.debug(" there are no page - but it should be there !");
					String newLastPageId = pagesIdGenerator.getPrevId(lastPageId);
					logger.debug(" lastpage correction in counters ! ");
					Map<String, String> map = new HashMap<String, String>();
					map.clear();
					map.put("lastPageId", newLastPageId);
					tableTool.update(countersTable, countersId, map);
					logger.debug(" remove page ");
					tableTool.delete(pagesTable, lastPageId);
				} else if (!correctNextPageFirstId.equals(nextPageFirstId)) {
                    logger.debug(" next page first id correction ! ");
                    // correcting entry for nextPage i set correct firstId
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("firstId", correctNextPageFirstId);
                    tableTool.update(pagesTable, pagesIdGenerator.getIdByOrderNo(page + 1), map);
				}
			} else if (correctNextPageFirstId != null) { // if this is last page and there are some elements after it - then add last page ...
                // insert to pages
                logger.debug(" last page first id correction - create page ! ");
                String pageId = pagesIdGenerator.getIdByOrderNo(page + 1);
                Map<String, String> map = new HashMap<String, String>();
                map.put("firstId", correctNextPageFirstId);
                tableTool.update(pagesTable, pageId, map);
                // insert to counters
                logger.debug(" last page first id correction in counters ! ");
                map.clear();
                map.put("lastPageId", pageId);
                tableTool.update(countersTable, countersId, map);
			}

			// generating (deserializing) objects
			Serializer serializationTool = new Serializer();
			List<T> objects = new ArrayList<T>();
			for (String data : result) {
				objects.add(dataDescriptor.buildData(serializationTool.deserialize(data)));
			}

			int maxPage = 0;
			if ((lastPageId != null) && (lastPageId != "")) {
				int lastPageNo = pagesIdGenerator.getOrderNo(lastPageId);
				if (lastPageNo > page) {
					maxPage = lastPageNo;
				} else {
					if (correctNextPageFirstId != null) {
						maxPage = page + 1;
					} else {
						maxPage = page;
					}
				}
			} else if (correctNextPageFirstId != null) {
				maxPage = page + 1;
			}
			return new PageQueryResult<T>(page, maxPage, objects);

		} catch (RowToolException e) {
			throw new IndexException(e);
		}
	}

}
