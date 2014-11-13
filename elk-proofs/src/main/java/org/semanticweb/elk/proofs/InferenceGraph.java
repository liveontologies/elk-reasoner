/**
 * 
 */
package org.semanticweb.elk.proofs;

import java.util.Collection;

import org.semanticweb.elk.proofs.expressions.derived.DerivedExpression;
import org.semanticweb.elk.proofs.inferences.Inference;

/**
 * A simple hypergraph-like representation of traced inferences. Nodes are
 * expressions and hyperedges are inferences. Edges are directed with a single
 * head which stands for conclusion.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface InferenceGraph {

	/**
	 * 
	 * @return
	 */
	public Collection<DerivedExpression> getRootExpressions();
	
	public Collection<DerivedExpression> getExpressions();
	
	/**
	 * 
	 * @param expression
	 * @return
	 */
	public Collection<Inference> getInferencesForPremise(DerivedExpression expression);
}
