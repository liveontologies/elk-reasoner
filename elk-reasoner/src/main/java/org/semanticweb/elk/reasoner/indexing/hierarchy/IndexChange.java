/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

/**
 * Represents a single change to the index
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface IndexChange {

	public boolean add(IndexedClassExpression target);
	
	public boolean remove(IndexedClassExpression target);
}
