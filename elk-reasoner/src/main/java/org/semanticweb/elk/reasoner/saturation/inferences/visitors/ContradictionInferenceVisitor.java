package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedContradiction;

public interface ContradictionInferenceVisitor<I, O> {

	public O visit(ContradictionFromDisjointSubsumers inference, I input);

	public O visit(ContradictionFromInconsistentDisjointnessAxiom inference,
			I input);

	public O visit(ContradictionFromNegation inference, I input);

	public O visit(ContradictionFromOwlNothing inference, I input);

	public O visit(PropagatedContradiction inference, I input);

}
