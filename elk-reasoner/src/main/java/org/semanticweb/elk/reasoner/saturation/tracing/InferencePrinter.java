/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;

import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class InferencePrinter implements TracedConclusionVisitor<String, Void> {

	@Override
	public String visit(InitializationSubsumer conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassOfSubsumer conclusion, Void parameter) {
		return "SubClassOf, premise: " + conclusion.getPremise();
	}

	@Override
	public String visit(ComposedConjunction conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstConjunct() + " and " + conclusion.getSecondConjunct();

	}

	@Override
	public String visit(DecomposedConjunction conclusion, Void parameter) {
		return "Decomposing " + conclusion.getExpression();

	}

	@Override
	public String visit(PropagatedSubsumer conclusion, Void parameter) {
		return "Existential inference from "
				+ conclusion.getInferenceContext(null) + " => "
				+ conclusion.getSubsumer() + " and "
				+ conclusion.getBackwardLink().getSource() + " => "
				+ conclusion.getBackwardLink().getRelation() + " some "
				+ conclusion.getInferenceContext(null);
	}

	@Override
	public String visit(ReflexiveSubsumer conclusion, Void parameter) {
		return "Reflexive inference: owl:Thing => " + conclusion.getRelation() + " some owl:Thing";
	}

	@Override
	public String visit(ComposedBackwardLink conclusion, Void parameter) {
		BackwardLink bwLink = conclusion.getBackwardLink();
		ForwardLink fwLink = conclusion.getForwardLink();

		return "Property chain inference: " + bwLink.getSource() + " => "
				+ bwLink.getRelation() + " o " + fwLink.getRelation()
				+ " some " + fwLink.getTarget();
	}

	@Override
	public String visit(ReversedBackwardLink conclusion, Void parameter) {
		return "Reversing backward link " + conclusion;
	}

	@Override
	public String visit(DecomposedExistential conclusion, Void parameter) {
		return "Creating backward link from " + conclusion.getExistential();
	}

	@Override
	public String visit(TracedPropagation conclusion, Void parameter) {
		return "Creating propagation from " + conclusion.getPremise();
	}

}
