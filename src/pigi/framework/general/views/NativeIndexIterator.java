package pigi.framework.general.views;

import java.util.Iterator;

import pigi.framework.general.vo.DataObject;

/**
 * Native index iterator
 * @param <T> iterated data class
 */
public class NativeIndexIterator <T extends DataObject> implements Iterator<T> {
	private Iterator<T> iterator;
	private Class<T> targetClass;
	
	public NativeIndexIterator(Class<T> targetClass, Iterator<T> iterator){
		this.iterator = iterator;
		this.targetClass = targetClass;
	}	

	public boolean hasNext() {		
		return iterator.hasNext();
	}

	public T next() {
		return iterator.next().turnTo(targetClass);
	}

    public void remove() {
		iterator.remove();
	}
	
	
}
