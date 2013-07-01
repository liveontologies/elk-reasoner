package org.semanticweb.elk.reasoner.datatypes.index.itree;


/**
 *
 * @author Pospishnyi Oleksandr
 */
public interface Interval<T extends Comparable> extends Comparable<Interval<T>> {

	public T getMin();

	public T getMax();
}
