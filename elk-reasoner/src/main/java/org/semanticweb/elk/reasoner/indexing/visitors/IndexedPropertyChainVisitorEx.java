/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.visitors;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface IndexedPropertyChainVisitorEx<O, P> {

	public O visit(IndexedObjectProperty property, P parameter);
	
	public O visit(IndexedBinaryPropertyChain chain, P parameter);
}
