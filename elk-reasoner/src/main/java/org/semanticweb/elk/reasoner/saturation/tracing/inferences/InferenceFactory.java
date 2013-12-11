/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Creates instances of {@link Inference}.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferenceFactory {

	public Inference createInitializationInference() {
		return new ClassInitializationInference();
	}
	
	public Inference createSubsumptionInference(Conclusion premise) {
		return new SubClassOfInference(premise);
	}
	
	public Inference createCompositionInference(Context context, ForwardLink forwardLink, IndexedPropertyChain backwardLinkChain, Context linkSource) {
		return new PropertyChainInference(context, backwardLinkChain, linkSource, forwardLink.getRelation());
	}
	
	public Inference createCompositionInference(Context context, BackwardLink backwardLink, IndexedPropertyChain forwardLinkChain) {
		return new PropertyChainInference(context, backwardLink.getRelation(), backwardLink.getSource(), forwardLinkChain);
	}
	
	/**
	 * A => R some B, B => C, thus A => R some C
	 * 
	 * @param context B (where the inference is made)
	 * @param bwLink R
	 * @param subsumer C
	 * @return
	 */
	public Inference createExistentialInference(Context context, BackwardLink bwLink, IndexedClassExpression subsumer) {
		return new ExistentialInference(context, subsumer, bwLink.getRelation(), bwLink.getSource());
	}
	
	/**
	 * 
	 * @param context B
	 * @param subsumer C 
	 * @param linkRelation R
	 * @param linkTarget A
	 * @return
	 */
	public Inference createExistentialInference(Context context, IndexedClassExpression subsumer, IndexedPropertyChain linkRelation, Context linkSource) {
		return new ExistentialInference(context, subsumer, linkRelation, linkSource);
	}
	
	public Inference createConjunctionInference(Conclusion subsumer, IndexedClassExpression conjunct) {
		return new ConjunctionCompositionInference(subsumer, conjunct);
	}
	
	public Inference createConjunctInference(IndexedObjectIntersectionOf conjunction) {
		return new ConjunctionDecompositionInference(conjunction);
	}
	
	public Inference createReflexiveInference(IndexedPropertyChain reflexiveChain) {
		return new ReflexiveInference(reflexiveChain);
	}
	
	public Inference createBridgeInference(Conclusion previous) {
		return new BridgeInference(previous);
	}

}
