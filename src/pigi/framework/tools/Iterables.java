package pigi.framework.tools;

import java.util.Iterator;

public class Iterables {

    public static <T> Iterable<T> iterable(final Iterator<T> iterator) {
        return new Iterable<T>() {

            public Iterator<T> iterator() {
                return iterator;
            }
        };
        
    }
}
