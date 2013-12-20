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
	}

	@Override
	public PositiveSubsumer createSubsumer(Conclusion premise,
			IndexedClassExpression subsumer) {
		//a subsumption inference
		return new SubClassOfSubsumer(premise, subsumer);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain,
			Context backwardLinkSource) {
		return new ComposedBackwardLink(chain, context, forwardLink, backwardLinkChain, backwardLinkSource);
	}

	@Override
	public BackwardLink createComposedBackwardLink(Context context, BackwardLink backwardLink,
			IndexedPropertyChain forwardRelation, Context forwardTarget, IndexedPropertyChain chain) {
		return new ComposedBackwardLink(chain, context, backwardLink, forwardRelation, forwardTarget);
	}

	@Override
	public ForwardLink createForwardLink(BackwardLink backwardLink, Context target) {
		return new ReversedBackwardLink(backwardLink, target);
	}

	@Override
	public BackwardLink createBackwardLink(
			IndexedObjectSomeValuesFrom subsumer, Context source) {
		return new DecomposedExistential(subsumer, source);
	}

	@Override
	public Propagation createPropagation(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		return new TracedPropagation(chain, carry);
	}

	@Override
	public NegativeSubsumer createPropagatedSubsumer(BackwardLink bwLink,
			IndexedObjectSomeValuesFrom carry, Context context) {
		return new PropagatedSubsumer(context, bwLink, carry);
	}
	
	@Override
	public NegativeSubsumer createPropagatedSubsumer(Propagation propagation, IndexedPropertyChain linkSource, Context linkTarget, Context context) {
		return new PropagatedSubsumer(context, propagation, linkSource, linkTarget);
	}

	@Override
	public NegativeSubsumer createdComposedConjunction(Conclusion subsumer,
			IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		return new ComposedConjunction(subsumer, conjunct, conjunction);
	}

	@Override
	public PositiveSubsumer createConjunct(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		return new DecomposedConjunction(conjunction, conjunct);
	}

	@Override
	public NegativeSubsumer createReflexiveSubsumer(
			IndexedObjectSomeValuesFrom existential) {
		return new ReflexiveSubsumer(existential);
	}
}