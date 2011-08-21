package pigi.framework.general.views.results;

import java.util.Iterator;

import pigi.framework.general.vo.DataObject;

/**
 * Iterable query result (for getAll queries)
 * @param <T> iterated VO class
 */
public class IterableResult <T extends DataObject> implements Iterable<T> {
	private Iterator<T> iterator;

	public IterableResult(Iterator<T> iterator) { 
		this.iterator = iterator;
	}

	public Iterator<T> iterator() {
		return iterator;
	}

}
