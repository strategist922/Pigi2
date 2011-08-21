package pigi.framework.general;

import pigi.framework.general.idgenerators.Keys;


/**
 * Order - describe index ordering
 * @author acure, vpatryshev
 *
 */
public enum Direction {
	ASC( "a", Keys.FIRST_KEY, Keys.LAST_KEY),
	DESC("d", Keys.LAST_KEY,  Keys.FIRST_KEY);
	
	public final String key;
	public final String first;
	public final String last;

    private Direction(String key, String first, String last) {
	    this.key = key;
	    this.first = first;
	    this.last = last;
	}
	
    @Override
    public String toString() {
        return key;
    }

}
