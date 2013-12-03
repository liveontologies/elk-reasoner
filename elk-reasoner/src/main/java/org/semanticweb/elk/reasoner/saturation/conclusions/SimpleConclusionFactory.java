/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SimpleConclusionFactory implements ConclusionFactory {

	@Override
	public PositiveSubsumer classInitialization(IndexedClassExpression ice) {
		return new PositiveSubsumerImpl(ice);
	}

	@Override
	public PositiveSubsumer subsumptionInference(Conclusion premise,
			IndexedClassExpression subsumer) {
		return new PositiveSubsumerImpl(subsumer);
	}

	@Override
	public BackwardLink chainInference(ForwardLink forwardLink, IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain, Context target) {
		return new BackwardLinkImpl(target, chain);
	}
	
	@Override
	public ForwardLink forwardLinkInference(BackwardLink backwardLink, Context target) {
		return new ForwardLinkImpl(backwardLink.getRelation(), target);
	}
	
	@Override
	public BackwardLink chainInference(BackwardLink backwardLink, IndexedPropertyChain forwardLinkChain, IndexedPropertyChain chain, Context target) {
		return new BackwardLinkImpl(target, chain);
	}
	
	@Override
	public BackwardLink backwardLinkInference(IndexedObjectSomeValuesFrom ice, Context target) {
		return new BackwardLinkImpl(target, ice.getRelation());
	}

	@Override
	public Propagation existentialInference(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		return new PropagationImpl(chain, carry);
	}

	@Override
	public NegativeSubsumer existentialInference(BackwardLink bwLink, IndexedObjectSomeValuesFrom carry) {
		return new NegativeSubsumerImpl(carry);
	}

	@Override
	public NegativeSubsumer conjunctionComposition(Conclusion premise,
			IndexedClassExpression conjunct, IndexedObjectIntersectionOf conjunction) {
		return new NegativeSubsumerImpl(conjunction);
	}

	@Override
	public PositiveSubsumer conjunctionDecomposition(IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		return new PositiveSubsumerImpl(conjunct);
	}

	@Override
	public NegativeSubsumer reflexiveInference(
			IndexedObjectSomeValuesFrom existential) {
		return new NegativeSubsumerImpl(existential);
	}

}
