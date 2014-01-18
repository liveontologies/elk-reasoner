/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class AvoidTrivialPropagationReader implements TraceStore.Reader {

	private final TraceStore.Reader reader_;
	
	public AvoidTrivialPropagationReader(TraceStore.Reader r) {
		reader_ = r;
	}

	@Override
	public void accept(final Context context, final Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		//final Collection<TracedConclusion> inferences = new LinkedList<TracedConclusion>();
		
		reader_.accept(context, conclusion, new BaseTracedConclusionVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(TracedConclusion inference, Void ignored) {
				inference.acceptTraced(visitor, null);
				//inferences.add(inference);
				
				return null;
			}

			@Override
			public Void visit(PropagatedSubsumer propagated, Void ignored) {
				if (!isTrivialPropagation(propagated, context)) {
					propagated.acceptTraced(visitor, null);
				}
				
				return null;
			}
			
		});
	}

	boolean isTrivialPropagation(PropagatedSubsumer propagated, Context context) {
		// a propagation is trivial if two conditions are met:
		// 1) the root is propagated (not one of its subsumers)
		// 2) the backward link has been derived by decomposing the existential
		// (which is the same as the propagation carry)
		BackwardLink link = propagated.getBackwardLink();
		Propagation propagation = propagated.getPropagation();
		Context inferenceContext = propagated.getInferenceContext(context);
		
		if (inferenceContext.getRoot() != propagation.getCarry().getFiller()) {
			return false;
		}
		
		final MutableBoolean linkProducedByDecomposition = new MutableBoolean(false);
		
		reader_.accept(inferenceContext, link, new BaseTracedConclusionVisitor<Boolean, Void>(){

			@Override
			public Boolean visit(DecomposedExistential conclusion, 	Void ignored) {
				linkProducedByDecomposition.set(true);
				
				return true;
			}
			
		});
		
		return linkProducedByDecomposition.get();
	}

}
