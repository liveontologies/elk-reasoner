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

/**
 * TODO
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingConclusionFactory implements ConclusionFactory {

	@Override
	public PositiveSubsumer createSubsumer(IndexedClassExpression ice) {
		//no premise, must be an initialization inference
		return new InitializationSubsumer(ice);
		//return new TracedPositiveSubsumer(inferenceFactory_.createInitializationInference(), conclusionFactory_.createSubsumer(ice));
	}

	@Override
	public PositiveSubsumer createSubsumer(Conclusion premise,
			IndexedClassExpression subsumer) {
		//a subsumption inference
		/*Inference subsumption = inferenceFactory_.createSubsumptionInference(premise);
		
		return new TracedPositiveSubsumer(subsumption, conclusionFactory_.createSubsumer(premise, subsumer));*/
		return new SubClassOfSubsumer(premise, subsumer);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain,
			Context backwardLinkSource) {
		//Inference compositionInference = inferenceFactory_.createCompositionInference(context, forwardLink, backwardLinkChain, backwardLinkSource);
		
		//return new TracedBackwardLink(compositionInference, conclusionFactory_.createComposedBackwardLink(context, forwardLink, backwardLinkChain, chain, backwardLinkSource));
		return new ComposedBackwardLink(chain, context, forwardLink, backwardLinkChain, backwardLinkSource);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, BackwardLink backwardLink,
			IndexedPropertyChain forwardRelation, Context forwardTarget, IndexedPropertyChain chain) {
		//Inference compositionInference = inferenceFactory_.createCompositionInference(context, backwardLink, forwardRelation, forwardTarget);
		
		//return new TracedBackwardLink(compositionInference, conclusionFactory_.createComposedBackwardLink(context, backwardLink, forwardRelation, forwardTarget, chain));
		return new ComposedBackwardLink(chain, context, backwardLink, forwardRelation, forwardTarget);
	}

	@Override
	public ForwardLink createForwardLink(BackwardLink backwardLink, Context target) {
		//Inference bridge = inferenceFactory_.createBridgeInference(backwardLink, target);
		
		//return new TracedForwardLink(bridge, conclusionFactory_.createForwardLink(backwardLink, target));
		return new ReversedBackwardLink(backwardLink, target);
	}

	@Override
	public BackwardLink createBackwardLink(
			IndexedObjectSomeValuesFrom subsumer, Context source) {
		//Inference subsumerInference = inferenceFactory_.createBridgeInference(TracingUtils.getSubsumerWrapper(subsumer), source);
		
		//return new TracedBackwardLink(subsumerInference, conclusionFactory_.createBackwardLink(subsumer, source));
		return new DecomposedExistential(subsumer, source);
	}

	@Override
	public Propagation createPropagation(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		// propagation is not an independent inference, it's a part of a larger existential inference
		//return conclusionFactory_.createPropagation(premise, chain, carry);
		return new TracedPropagation(chain, carry);
	}

	@Override
	public NegativeSubsumer createPropagatedSubsumer(BackwardLink bwLink,
			IndexedObjectSomeValuesFrom carry, Context context) {
		//Inference existentialInference = inferenceFactory_.createExistentialInference(context, bwLink, carry.getFiller());
		
		//return new TracedNegativeSubsumer(existentialInference, conclusionFactory_.createPropagatedSubsumer(bwLink, carry, context));
		return new PropagatedSubsumer(context, bwLink, carry);
	}
	
	@Override
	public NegativeSubsumer createPropagatedSubsumer(Propagation propagation, IndexedPropertyChain linkSource, Context linkTarget, Context context) {
		//Inference existentialInference = inferenceFactory_.createExistentialInference(context, propagation.getCarry().getFiller(), linkRelation, linkTarget);
		
		//return new TracedNegativeSubsumer(existentialInference, conclusionFactory_.createPropagatedSubsumer(propagation, linkRelation, linkTarget, context));
		return new PropagatedSubsumer(context, propagation, linkSource, linkTarget);
	}

	@Override
	public NegativeSubsumer createdComposedConjunction(Conclusion subsumer,
			IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		//Inference conjunctionInference = inferenceFactory_.createConjunctionInference(subsumer, conjunct);
		
		//return new TracedNegativeSubsumer(conjunctionInference, conclusionFactory_.createdComposedConjunction(subsumer, conjunct, conjunction));
		return new ComposedConjunction(subsumer, conjunct, conjunction);
	}

	@Override
	public PositiveSubsumer createConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		//Inference conjunctionInference = inferenceFactory_.createConjunctInference(conjunction);
		
		//return new TracedPositiveSubsumer(conjunctionInference, conclusionFactory_.createConjunct(conjunction, conjunct));
		return new DecomposedConjunction(conjunction, conjunct);
	}

	@Override
	public NegativeSubsumer createReflexiveSubsumer(
			IndexedObjectSomeValuesFrom existential) {
		//Inference reflexiveInference = inferenceFactory_.createReflexiveInference(existential.getRelation());
		
		//return new TracedNegativeSubsumer(reflexiveInference, conclusionFactory_.createReflexiveSubsumer(existential));
		return new ReflexiveSubsumer(existential);
	}
}
