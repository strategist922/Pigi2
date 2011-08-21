package pigi.framework.general.views.results;

import java.util.List;

import pigi.framework.general.vo.DataObject;

/**
 * Page query result (for getPage queries)
 * @param <T> iterated VO class
 */
public class PageQueryResult <T extends DataObject> {
	private int pageNo;
	private int maxPage;
	private List<T> objects;

	/**
	 * represents result of the page query
	 * 
	 * @param page
	 * @param maxPage
	 * @param objects
	 */
	public PageQueryResult(int page, int maxPage, List<T> objects) {
		this.pageNo = page;
		this.maxPage = maxPage;
		this.objects = objects;
	}

	
	public PageQueryResult(PageQueryResult<T> obj){
		this.pageNo = obj.getPageNo();
		this.maxPage = obj.maxPage;
		this.objects = obj.getObjects();
	}
	
	/**
	 * get VOobjects
	 * 
	 * @return
	 */
	public List<T> getObjects() {
		return objects;
	}

	/**
	 * get page nr
	 * 
	 * @return
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * returns max page for the pages range
	 * 
	 * @return
	 */
	public int getMaxPage() {
		return maxPage;
	}

}
