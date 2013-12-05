/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public interface ContextTracer {

	public Iterable<Inference> getInference(Conclusion conclusion);
	
	public Iterable<Inference> getSubsumerInferences(IndexedClassExpression conclusion);
	
	public Iterable<Inference> getBackwardLinkInferences(IndexedPropertyChain linkRelation, Context linkSource);
	
	public boolean addInference(Conclusion conclusion, Inference inference);
}
