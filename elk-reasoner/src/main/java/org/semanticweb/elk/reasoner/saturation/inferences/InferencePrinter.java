/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInherited;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainExpandedSubObjectPropertyOf;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainTautology;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;

/**
 * TODO: improve and extend to all inferences
 * 
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter
		implements
			ClassInference.Visitor<String>,
			ObjectPropertyInference.Visitor<String> {

	private static InferencePrinter DEFAULT_PRINTER_ = new InferencePrinter(
			new ConclusionBaseFactory());

	public static String print(ClassInference inference) {
		return inference.accept(DEFAULT_PRINTER_);
	}

	private final Conclusion.Factory factory_;

	public InferencePrinter(Conclusion.Factory factory) {
		this.factory_ = factory;
	}

	@Override
	public String visit(BackwardLinkComposition inference) {
		BackwardLink bwLink = inference.getFirstPremise(factory_);
		ForwardLink fwLink = inference.getThirdPremise(factory_);
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(BackwardLinkOfObjectHasSelf inference) {
		return "Creating forward link from " + inference.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		return "Creating backward link from " + inference.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkReversed inference) {
		return "Reversing forward link " + inference.getPremise(factory_);
	}

	@Override
	public String visit(BackwardLinkReversedExpanded inference) {
		return "Reversing forward link " + inference.getFirstPremise(factory_)
				+ " and unfolding under "
				+ inference.getSecondPremise(factory_);
	}

	@Override
	public String visit(ContextInitializationNoPremises inference) {
		return "Init (" + inference.getDestination() + ")";
	}

	@Override
	public String visit(ContradictionOfDisjointSubsumers inference) {
		return inference.toString();
	}

	@Override
	public String visit(ContradictionOfObjectComplementOf inference) {
		return "Contradiction due to derived "
				+ inference.getFirstPremise(factory_) + " and "
				+ inference.getSecondPremise(factory_);
	}

	@Override
	public String visit(ContradictionOfOwlNothing inference) {
		return inference.toString();
	}

	@Override
	public String visit(ContradictionPropagated inference) {
		return "Contradiction propagated over "
				+ inference.getFirstPremise(factory_);
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer inference) {
		return "Disjoint subsumer " + inference + " derived from "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkComposition inference) {
		BackwardLink bwLink = inference.getFirstPremise(factory_);
		ForwardLink fwLink = inference.getThirdPremise(factory_);
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ForwardLinkOfObjectHasSelf inference) {
		return "Creating forward link from " + inference.getPremise(factory_);
	}

	@Override
	public String visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		return "Creating forward link from " + inference.getPremise(factory_);
	}

	@Override
	public String visit(PropagationGenerated inference) {
		return "Creating propagation from "
				+ inference.getSecondPremise(factory_);
	}

	@Override
	public String visit(PropertyRangeInherited inference) {
		return "Property Range Inherited (" + inference.getProperty() + " : "
				+ inference.getRange() + ", premise: "
				+ inference.getFirstPremise(factory_) + ", reason: "
				+ inference.getReason();
	}

	@Override
	public String visit(SubClassInclusionComposedDefinedClass inference) {
		return "Composed definition " + inference.getSuperExpression()
				+ " from " + inference.getFirstPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedEntity inference) {
		return "Composed decomposition " + inference.getSuperExpression();
	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectIntersectionOf inference) {
		return "Conjuncting " + inference.getFirstPremise(factory_) + " and "
				+ inference.getSecondPremise(factory_);

	}

	@Override
	public String visit(
			SubClassInclusionComposedObjectSomeValuesFrom inference) {
		return "Existential inference from "
				+ inference.getSecondPremise(factory_) + " and "
				+ inference.getFirstPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionComposedObjectUnionOf inference) {
		return "Composed disjunction " + inference.getSuperExpression()
				+ " from " + inference.getPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionDecomposedFirstConjunct inference) {
		return "Decomposing " + inference.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionDecomposedSecondConjunct inference) {
		return "Decomposing " + inference.getPremise(factory_);

	}

	@Override
	public String visit(SubClassInclusionExpandedDefinition inference) {
		return "Decomposed definition " + inference.getSuperExpression()
				+ " of " + inference.getFirstPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionExpandedSubClassOf inference) {
		return "SubClassOf( " + inference.getFirstPremise(factory_) + " "
				+ inference.getSuperExpression() + " )";
	}

	@Override
	public String visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		return "Property range of " + inference.getFirstPremise(factory_);
	}

	@Override
	public String visit(SubClassInclusionOwlThing inference) {
		return "SubClassOf( " + inference.getDestination() + " "
				+ inference.getSuperExpression() + " ) [owl:Thing]";
	}

	@Override
	public String visit(SubClassInclusionRange inference) {
		return "SubClassOf( " + inference.getDestination() + " "
				+ inference.getSuperExpression() + " ) [PropertyRange]";
	}

	@Override
	public String visit(SubClassInclusionTautology inference) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubContextInitializationNoPremises inference) {
		return "Init (" + inference.getDestination() + ":"
				+ inference.getDestinationSubRoot() + ")";
	}

	@Override
	public String visit(SubPropertyChainExpandedSubObjectPropertyOf inference) {
		return "Expanded sub-chain: " + inference.getSubChain() + " => "
				+ inference.getSuperChain() + ", premise: "
				+ inference.getFirstPremise(factory_) + ", reason: "
				+ inference.getReason();
	}

	@Override
	public String visit(SubPropertyChainTautology inference) {
		return "Initialization (" + inference.getChain() + " => "
				+ inference.getSuperChain() + ")";
	}

}
