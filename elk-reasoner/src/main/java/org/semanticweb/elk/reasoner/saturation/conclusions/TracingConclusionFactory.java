/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.conclusions;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.tracing.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.Tracer;

/**
 * Not just creates conclusions but also saves information on how the conclusion
 * was produced. That information is represented using instances of
 * {@link Inference} and is saved using a {@link Tracer}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingConclusionFactory implements ConclusionFactory {

	@Override
	public PositiveSubsumer classInitialization(IndexedClassExpression ice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PositiveSubsumer subsumptionInference(Conclusion premise,
			IndexedClassExpression subsumer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BackwardLink chainInference(ForwardLink forwardLink,
			IndexedPropertyChain backwardLinkChain, IndexedPropertyChain chain,
			Context target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BackwardLink chainInference(BackwardLink backwardLink,
			IndexedPropertyChain forwardLinkChain, IndexedPropertyChain chain,
			Context target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ForwardLink forwardLinkInference(BackwardLink backwardLink,
			Context target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BackwardLink backwardLinkInference(
			IndexedObjectSomeValuesFrom subsumer, Context target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Propagation existentialInference(Conclusion premise,
			IndexedPropertyChain chain, IndexedObjectSomeValuesFrom carry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegativeSubsumer existentialInference(BackwardLink bwLink,
			IndexedObjectSomeValuesFrom carry) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegativeSubsumer conjunctionComposition(Conclusion premise,
			IndexedClassExpression conjunct,
			IndexedObjectIntersectionOf conjunction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PositiveSubsumer conjunctionDecomposition(
			IndexedObjectIntersectionOf conjunction,
			IndexedClassExpression conjunct) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NegativeSubsumer reflexiveInference(
			IndexedObjectSomeValuesFrom existential) {
		// TODO Auto-generated method stub
		return null;
	}

}
