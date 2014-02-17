/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;

/**
 * Reads all inferences for the given conclusion except that propagations which
 * propagate the context's root over a backward link which has been created by
 * decomposing the propagated subsumer.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class AvoidTrivialPropagationReader extends DelegatingTraceReader {

	public AvoidTrivialPropagationReader(TraceStore.Reader r) {
		super(r);
	}

	@Override
	public void accept(final IndexedClassExpression root, final Conclusion conclusion, final TracedConclusionVisitor<?, ?> visitor) {
		reader.accept(root, conclusion, new BaseTracedConclusionVisitor<Void, Void>() {

			@Override
			protected Void defaultTracedVisit(TracedConclusion inference, Void ignored) {
				inference.acceptTraced(visitor, null);
				
				return null;
			}

			@Override
			public Void visit(PropagatedSubsumer propagated, Void ignored) {
				if (!isTrivialPropagation(propagated, root.getContext())) {
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
		
		reader.accept(inferenceContext.getRoot(), link, new BaseTracedConclusionVisitor<Boolean, Void>(){

			@Override
			public Boolean visit(DecomposedExistential conclusion, 	Void ignored) {
				linkProducedByDecomposition.set(true);
				
				return true;
			}
			
		});
		
		return linkProducedByDecomposition.get();
	}

}
