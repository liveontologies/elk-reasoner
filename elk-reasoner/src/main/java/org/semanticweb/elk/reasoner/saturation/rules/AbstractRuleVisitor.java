package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ForwardLinkFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.PropagationFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;

/**
 * A skeleton for implementation of {@link RuleVisitor}s using a common
 * (default) methods
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public abstract class AbstractRuleVisitor implements RuleVisitor {

	abstract <P> void defaultVisit(Rule<P> rule, P premise, Context context,
			ConclusionProducer producer);

	@Override
	public void visit(BackwardLinkChainFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(BackwardLinkCompositionRule rule, ForwardLink premise,
			Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradicitonCompositionRule rule,
			DisjointSubsumer premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);

	}

	@Override
	public void visit(ContradictionFromDisjointnessRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionFromNegationRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionFromOwlNothingRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionOverBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ContradictionPropagationRule rule, Contradiction premise,
			Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);

	}

	@Override
	public void visit(DisjointSubsumerFromMemberRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ForwardLinkFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(IndexedObjectComplementOfDecomposition rule,
			IndexedObjectComplementOf premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(IndexedObjectIntersectionOfDecomposition rule,
			IndexedObjectIntersectionOf premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(IndexedObjectSomeValuesFromDecomposition rule,
			IndexedObjectSomeValuesFrom premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ObjectIntersectionFromConjunctRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(ObjectUnionFromDisjunctRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(OwlThingContextInitRule rule, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, null, context, producer);
	}

	@Override
	public void visit(PropagationFromBackwardLinkRule rule,
			BackwardLink premise, Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(PropagationFromExistentialFillerRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(RootContextInitializationRule rule, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, null, context, producer);
	}

	@Override
	public void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
			Context context, ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

	@Override
	public void visit(SuperClassFromSubClassRule rule,
			IndexedClassExpression premise, Context context,
			ConclusionProducer producer) {
		defaultVisit(rule, premise, context, producer);
	}

}
