/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.util;

import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDecomposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedDisjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedExistential;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.GeneratedPropagation;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ObjectHasSelfPropertyRangeSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.properties.SubPropertyChainInit;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ClassInferenceVisitor;

/**
 * A utility to pretty-print {@link ClassInference}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class InferencePrinter
		implements
			ClassInferenceVisitor<Void, String>,
			ObjectPropertyInferenceVisitor<Void, String> {

	private static InferencePrinter DEFAULT_PRINTER_ = new InferencePrinter(
			new ConclusionBaseFactory());

	private final Conclusion.Factory factory_;

	public InferencePrinter(Conclusion.Factory factory) {
		this.factory_ = factory;
	}

	public static String print(ClassInference conclusion) {
		return conclusion.accept(DEFAULT_PRINTER_, null);
	}

	@Override
	public String visit(InitializationSubsumer conclusion, Void parameter) {
		return "Root Initialization";
	}

	@Override
	public String visit(SubClassOfSubsumer conclusion, Void parameter) {
		return "SubClassOf( " + conclusion.getPremise(factory_) + " "
				+ conclusion.getExpression() + " )";
	}

	@Override
	public String visit(ComposedConjunction conclusion, Void parameter) {
		return "Conjuncting " + conclusion.getFirstPremise(factory_) + " and "
				+ conclusion.getSecondPremise(factory_);

	}

	@Override
	public String visit(DecomposedFirstConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(DecomposedSecondConjunct conclusion, Void parameter) {
		return "Decomposing " + conclusion.getPremise(factory_);

	}

	@Override
	public String visit(ComposedExistential conclusion, Void parameter) {
		return "Existential inference from "
				+ conclusion.getSecondPremise(factory_) + " and "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(ComposedBackwardLink conclusion, Void parameter) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed backward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ComposedForwardLink conclusion, Void input) {
		BackwardLink bwLink = conclusion.getFirstPremise(factory_);
		ForwardLink fwLink = conclusion.getThirdPremise(factory_);
		return "Composed forward link from " + bwLink + " and " + fwLink;
	}

	@Override
	public String visit(ReversedForwardLink conclusion, Void parameter) {
		return "Reversing forward link " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SuperReversedForwardLink conclusion, Void input) {
		return "Reversing forward link " + conclusion.getFirstPremise(factory_)
				+ " and unfolding under "
				+ conclusion.getSecondPremise(factory_);
	}

	@Override
	public String visit(DecomposedExistentialBackwardLink conclusion,
			Void parameter) {
		return "Creating backward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(DecomposedReflexiveBackwardLink conclusion,
			Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(DecomposedExistentialForwardLink conclusion,
			Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(DecomposedReflexiveForwardLink conclusion, Void input) {
		return "Creating forward link from " + conclusion.getPremise(factory_);
	}

	@Override
	public String visit(GeneratedPropagation conclusion, Void parameter) {
		return "Creating propagation from "
				+ conclusion.getFirstPremise(factory_);
	}

	@Override
	public String visit(ContradictionFromDisjointSubsumers conclusion,
			Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(ContradictionFromNegation conclusion, Void input) {
		return "Contradiction due to derived " + conclusion.getPremise(factory_)
				+ " and " + conclusion.getNegatedPremise(factory_);
	}

	@Override
	public String visit(ContradictionFromOwlNothing conclusion, Void input) {
		return conclusion.toString();
	}

	@Override
	public String visit(PropagatedContradiction conclusion, Void input) {
		return "Contradiction propagated over "
				+ conclusion.getLinkPremise(factory_);
	}

	@Override
	public String visit(DisjointSubsumerFromSubsumer conclusion, Void input) {
		return "Disjoint subsumer " + conclusion + " derived from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(ComposedDisjunction conclusion, Void input) {
		return "Composed disjunction " + conclusion.getExpression() + " from "
				+ conclusion.getPremise(factory_);
	}

	@Override
	public String visit(SubPropertyChainInit inference, Void input) {
		return "Initialization (" + inference.getChain() + " => "
				+ inference.getSuperChain() + ")";
	}

	@Override
	public String visit(SubPropertyChainExpanded inference, Void input) {
		return "Told sub-chain: " + inference.getSubChain() + " => "
				+ inference.getSuperChain() + ", premise: "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(ObjectHasSelfPropertyRangeSubsumer inference,
			Void input) {
		return "Property range of " + inference.getPremise(factory_);
	}

	@Override
	public String visit(ComposedDecomposition inference, Void input) {
		return "Composed decomposition " + inference.getExpression();
	}

	@Override
	public String visit(ComposedDefinition inference, Void input) {
		return "Composed definition " + inference.getExpression() + " from "
				+ inference.getPremise(factory_);
	}

	@Override
	public String visit(DecomposedDefinition inference, Void input) {
		return "Decomposed definition " + inference.getExpression() + " of "
				+ inference.getPremise(factory_);
	}

}
