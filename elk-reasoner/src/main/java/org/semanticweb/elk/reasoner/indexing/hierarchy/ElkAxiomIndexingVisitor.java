/**
 * 
 */
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;

/**
 * A simple intersection of the two interfaces
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ElkAxiomIndexingVisitor extends ElkAxiomVisitor<Void>, ElkAxiomIndexer {

	public int getMultiplicity();
}
