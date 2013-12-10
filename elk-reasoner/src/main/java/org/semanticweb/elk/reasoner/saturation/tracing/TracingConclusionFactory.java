/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.NegativeSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.InferenceFactory;

/**
 * Not just creates conclusions but also saves information on how the conclusion
 * was produced. That information is represented using instances of
 * {@link Inference} and is saved using a {@link TraceStore}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingConclusionFactory implements ConclusionFactory {

	private final ConclusionFactory conclusionFactory_;
	
	private final InferenceFactory inferenceFactory_;
	
	public TracingConclusionFactory(ConclusionFactory conclusionFactory, InferenceFactory inferenceFactory) {
		conclusionFactory_ = conclusionFactory;
		inferenceFactory_ = inferenceFactory;
	}
	
	@Override
	public PositiveSubsumer createSubsumer(IndexedClassExpression ice) {
		//no premise, must be an initialization inference
		return new TracedPositiveSubsumer(inferenceFactory_.createInitializationInference(), conclusionFactory_.createSubsumer(ice));
	}

	@Override
	public PositiveSubsumer createSubsumer(Conclusion premise,
			IndexedClassExpression subsumer) {
		//a subsumption inference
		Inference subsumption = inferenceFactory_.createSubsumptionInference(premise);
		
		return new TracedPositiveSubsumer(subsumption, conclusionFactory_.createSubsumer(premise, subsumer));
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain,
			Context backwardLinkSource) {
		Inference compositionInference = inferenceFactory_.createCompositionInference(context, forwardLink, backwardLinkChain, backwardLinkSource);
		
		return new TracedBackwardLink(compositionInference, conclusionFactory_.createComposedBackwardLink(context, forwardLink, backwardLinkChain, chain, backwardLinkSource));
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, BackwardLink backwardLink,
			IndexedPropertyChain forwardLinkChain, IndexedPropertyChain chain) {
		Inference compositionInference = inferenceFactory_.createCompositionInference(context, backwardLink, forwardLinkChain);
		
		return new TracedBackwardLink(compositionInference, conclusionFactory_.createComposedBackwardLink(context, backwardLink, forwardLinkChain, chain));
	}

	@Override
	public ForwardLink createForwardLink(BackwardLink backwardLink,
			Context target) {
		// we can always recover the inference for this forward link by the corresponding backward link
		return conclusionFactory_.createForwardLink(backwardLink, target);

	}

	@Override
	public BackwardLink createBackwardLink(
			IndexedObjectSomeValuesFrom subsumer, Context target) {
		Inference subsumerInference = inferenceFactory_.createBridgeInference(subsumer);
		
		return new TracedBackwardLink(subsumerInference, conclusionFactory_.createBackwardLink(subsumer, target));
	}

	@Override
	public Propagation createPropagation(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		// propagation is not an independent inference, it's a part of a larger existential inference
		return conclusionFactory_.createPropagation(premise, chain, carry);
	}

	@Override
	public NegativeSubsumer createPropagatedSubsumer(BackwardLink bwLink,
			IndexedObjectSomeValuesFrom carry, Context context) {
		Inference existentialInference = inferenceFactory_.createExistentialInference(context, bwLink, carry.getFiller());
		
		return new TracedNegativeSubsumer(existentialInference, conclusionFactory_.createPropagatedSubsumer(bwLink, carry, context));
	}
	
	@Override
	public NegativeSubsumer createPropagatedSubsumer(Propagation propagation, IndexedPropertyChain linkRelation, Context linkTarget, Context context) {
		Inference existentialInference = inferenceFactory_.createExistentialInference(context, propagation.getCarry().getFiller(), linkRelation, linkTarget);
		
		return new TracedNegativeSubsumer(existentialInference, conclusionFactory_.createPropagatedSubsumer(propagation, linkRelation, linkTarget, context));
	}

	@Override
	public NegativeSubsumer createdComposedConjunction(Conclusion subsumer,
			IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		Inference conjunctionInference = inferenceFactory_.createConjunctionInference(subsumer, conjunct);
		
		return new TracedNegativeSubsumer(conjunctionInference, conclusionFactory_.createdComposedConjunction(subsumer, conjunct, conjunction));
	}

	@Override
	public PositiveSubsumer createConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		Inference conjunctionInference = inferenceFactory_.createConjunctInference(conjunction);
		
		return new TracedPositiveSubsumer(conjunctionInference, conclusionFactory_.createConjunct(conjunction, conjunct));
	}

	@Override
	public NegativeSubsumer createReflexiveSubsumer(
			IndexedObjectSomeValuesFrom existential) {
		Inference reflexiveInference = inferenceFactory_.createReflexiveInference(existential.getRelation());
		
		return new TracedNegativeSubsumer(reflexiveInference, conclusionFactory_.createReflexiveSubsumer(existential));
	}

}
