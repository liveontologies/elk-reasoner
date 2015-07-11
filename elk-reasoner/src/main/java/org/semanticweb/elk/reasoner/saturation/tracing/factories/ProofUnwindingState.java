package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferencePremiseVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;

class ProofUnwindingState<I extends Conclusion, J extends ProofUnwindingJob<I>> {

	public final static ConclusionVisitor<ProofUnwindingState<?, ?>, Void> CONCLUSION_INSERTION_VISITOR = new ConclusionInsertionVisitor();

	public final static ClassInferenceVisitor<ProofUnwindingState<?, ?>, Void> PREMISE_INSERTION_VISITOR = new ClassInferencePremiseVisitor<ProofUnwindingState<?, ?>, Void>(
			CONCLUSION_INSERTION_VISITOR);

	final J initiatorJob;

	final Set<ClassInference> processedInferences;

	final Queue<Conclusion> todoConclusions;

	final Queue<ClassInference> todoInferences;

	ProofUnwindingState(J initiatorJob) {
		this.initiatorJob = initiatorJob;
		this.processedInferences = new ArrayHashSet<ClassInference>();
		this.todoInferences = new LinkedList<ClassInference>();
		this.todoConclusions = new LinkedList<Conclusion>();
		todoConclusions.add(initiatorJob.getInput());
	}

	private static class ConclusionInsertionVisitor extends
			AbstractConclusionVisitor<ProofUnwindingState<?, ?>, Void> {

		@Override
		protected Void defaultVisit(Conclusion conclusion,
				ProofUnwindingState<?, ?> input) {
			input.todoConclusions.add(conclusion);
			return null;
		}

	}

}
