/**
 * 
 */
package org.semanticweb.elk.proofs.inferences.mapping;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface SatisfiabilityChecker {

	public boolean isSatisfiable(IndexedClassExpression ice);
}
