package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

import org.semanticweb.elk.reasoner.saturation.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjunctionComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;

public interface SubsumerInferenceVisitor<I, O> {

	public O visit(ComposedConjunction inference, I input);

	public O visit(DecomposedConjunction inference, I input);

	public O visit(DisjunctionComposition inference, I input);

	public O visit(InitializationSubsumer<?> inference, I parameter);

	public O visit(PropagatedSubsumer inference, I input);

	public O visit(ReflexiveSubsumer<?> inference, I input);

	public O visit(SubClassOfSubsumer<?> inference, I parameter);

}
