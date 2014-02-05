/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ForwardLinkFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.PropagationFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradicitonCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.NonReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.ReflexivePropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromDisjointnessRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectUnionFromDisjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRule;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inspects all class expressions that are reachable via context rules or
 * backward link rules to make sure they all exist in the index
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SaturationGraphValidationStage extends BasePostProcessingStage {

	/**
	 * 
	 */
	private class ClassExpressionValidator {

		private final Set<IndexedClassExpression> cache_ = new ArrayHashSet<IndexedClassExpression>();

		private final Queue<IndexedClassExpression> toValidate_ = new LinkedList<IndexedClassExpression>();

		boolean add(IndexedClassExpression ice) {
			if (cache_.add(ice)) {
				toValidate_.add(ice);
				return true;
			}
			return false;
		}

		void checkNew(IndexedClassExpression ice) {
			if (add(ice)) {
				LOGGER_.error("Unexpected reachable class expression: " + ice
						+ ", " + ice.printOccurrenceNumbers());
			}
		}

		/**
		 * @return {@code true} if something new has been validated, otherwise
		 *         returns {@code false}
		 */
		boolean validate() {
			if (toValidate_.isEmpty())
				return false;
			for (;;) {
				IndexedClassExpression ice = toValidate_.poll();
				if (ice == null)
					break;
				validate(ice);
			}
			return true;
		}

		private void validate(IndexedClassExpression ice) {
			LOGGER_.trace("Validating class expression {}", ice);

			// this is the main check
			if (!ice.occurs()) {
				LOGGER_.error("Dead class expression: {}", ice);
			}

			// validating context
			Context context = ice.getContext();
			if (context != null) {
				contextValidator_.add(context);
			}

			// validating context rules
			LinkedSubsumerRule rule = ice.getCompositionRuleHead();

			while (rule != null) {
				rule.accept(ruleValidator_, ice, null, null);
				rule = rule.next();
			}
		}
	}

	/**
	 * 
	 */
	private class ContextRuleValidator implements RuleVisitor {

		@Override
		public void visit(BackwardLinkChainFromBackwardLinkRule rule,
				BackwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (IndexedPropertyChain prop : rule
					.getForwardLinksByObjectProperty().keySet()) {
				for (IndexedClassExpression target : rule
						.getForwardLinksByObjectProperty().get(prop)) {
					iceValidator_.checkNew(target);
				}
			}
		}

		@Override
		public void visit(ContradicitonCompositionRule rule,
				DisjointSubsumer premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(
				ContradictionFromDisjointnessRule thisContradictionRule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(ContradictionFromNegationRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			iceValidator_.checkNew(rule.getNegation());

		}

		@Override
		public void visit(ContradictionFromOwlNothingRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(
				ContradictionOverBackwardLinkRule bottomBackwardLinkRule,
				BackwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(ContradictionPropagationRule rule,
				Contradiction premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(DisjointSubsumerFromMemberRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (IndexedDisjointnessAxiom axiom : rule.getDisjointnessAxioms()) {
				if (!axiom.occurs()) {
					LOGGER_.error("Dead disjointness axiom: " + axiom);
				}

				for (IndexedClassExpression ice : axiom.getDisjointMembers()) {
					iceValidator_.checkNew(ice);
				}
			}

		}

		@Override
		public void visit(ForwardLinkFromBackwardLinkRule rule,
				BackwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(IndexedObjectComplementOfDecomposition rule,
				IndexedObjectComplementOf premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(IndexedObjectIntersectionOfDecomposition rule,
				IndexedObjectIntersectionOf premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(IndexedObjectSomeValuesFromDecomposition rule,
				IndexedObjectSomeValuesFrom premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(NonReflexiveBackwardLinkCompositionRule rule,
				ForwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(NonReflexivePropagationRule rule,
				Propagation premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(ObjectIntersectionFromConjunctRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : rule
					.getConjunctionsByConjunct().entrySet()) {
				iceValidator_.checkNew(entry.getKey());
				iceValidator_.checkNew(entry.getValue());
			}
		}

		@Override
		public void visit(ObjectUnionFromDisjunctRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getDisjunctions()) {
				iceValidator_.checkNew(ice);
			}
		}

		@Override
		public void visit(OwlThingContextInitRule rule,
				ContextInitialization premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(PropagationFromBackwardLinkRule rule,
				BackwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(PropagationFromExistentialFillerRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getNegativeExistentials()) {
				iceValidator_.checkNew(ice);
			}
		}

		@Override
		public void visit(ReflexiveBackwardLinkCompositionRule rule,
				ForwardLink premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(ReflexivePropagationRule rule, Propagation premise,
				ContextPremises premises, ConclusionProducer producer) {
			// nothing is stored in the rule

		}

		@Override
		public void visit(RootContextInitializationRule rule,
				ContextInitialization premise, ContextPremises premises,
				ConclusionProducer producer) {
			// nothing is stored in the rule
		}

		@Override
		public void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
				ContextPremises premises, ConclusionProducer producer) {
			for (IndexedPropertyChain prop : rule
					.getPropagationsByObjectProperty().keySet()) {
				for (IndexedClassExpression ice : rule
						.getPropagationsByObjectProperty().get(prop)) {
					iceValidator_.checkNew(ice);
				}
			}
		}

		@Override
		public void visit(SuperClassFromSubClassRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getToldSuperclasses()) {
				iceValidator_.checkNew(ice);
			}

		}

	}

	/**
	 * 
	 * 
	 */
	private class ContextValidator {

		private final Set<Context> cache_ = new ArrayHashSet<Context>();

		private final Queue<Context> toValidate_ = new LinkedList<Context>();

		void add(Context context) {
			if (cache_.add(context)) {
				toValidate_.add(context);
			}
		}

		/**
		 * @return {@code true} if something new has been validated, otherwise
		 *         returns {@code false}
		 */
		boolean validate() {
			if (toValidate_.isEmpty())
				return false;
			for (;;) {
				Context context = toValidate_.poll();
				if (context == null)
					break;
				validate(context);
			}
			return true;
		}

		private void validate(Context context) {

			if (LOGGER_.isTraceEnabled()) {
				LOGGER_.trace("Validating context for " + context.getRoot());
			}

			// validating the root
			IndexedClassExpression root = context.getRoot();
			iceValidator_.checkNew(root);
			if (root.getContext() != context)
				LOGGER_.error("Invalid root for " + context);

			// validating subsumers recursively
			for (IndexedClassExpression subsumer : context.getSubsumers()) {
				iceValidator_.checkNew(subsumer);
			}

			// validating backward links
			for (IndexedPropertyChain prop : context
					.getBackwardLinksByObjectProperty().keySet()) {
				for (IndexedClassExpression source : context
						.getBackwardLinksByObjectProperty().get(prop)) {
					iceValidator_.checkNew(source);
				}
			}

			// validating backward link rules
			LinkableBackwardLinkRule rule = context.getBackwardLinkRuleHead();

			while (rule != null) {
				rule.accept(ruleValidator_, null, null, null);
				rule = rule.next();
			}
		}
	}

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationGraphValidationStage.class);
	private final ContextValidator contextValidator_ = new ContextValidator();

	private final ClassExpressionValidator iceValidator_ = new ClassExpressionValidator();

	private final OntologyIndex index_;

	private final ContextRuleValidator ruleValidator_ = new ContextRuleValidator();

	public SaturationGraphValidationStage(final AbstractReasonerState reasoner) {
		index_ = reasoner.ontologyIndex;
	}

	@Override
	public void execute() {
		// starting from indexed class expressions
		for (IndexedClassExpression ice : index_.getIndexedClassExpressions()) {
			iceValidator_.add(ice);
		}
		for (;;) {
			if (iceValidator_.validate() || contextValidator_.validate())
				continue;
			break;
		}
	}

	@Override
	public String getName() {
		return "Saturation graph validation";
	}

}
