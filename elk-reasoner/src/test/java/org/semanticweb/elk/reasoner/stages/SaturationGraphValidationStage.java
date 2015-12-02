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

import org.semanticweb.elk.reasoner.indexing.classes.DummyIndexedContextRootVisitor;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.ClassConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.BackwardLinkChainFromBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.ContradictionOverBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.LinkableBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.backwardlinks.SubsumerBackwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.OwlThingContextInitRule;
import org.semanticweb.elk.reasoner.saturation.rules.contextinit.RootContextInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.contradiction.ContradictionPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.disjointsubsumer.ContradictionCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.BackwardLinkFromForwardLinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.NonReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.forwardlink.ReflexiveBackwardLinkCompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.propagations.SubsumerPropagationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subcontextinit.PropagationInitializationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.AbstractObjectIntersectionFromConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ComposedFromDecomposedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromNegationRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ContradictionFromOwlNothingRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.DisjointSubsumerFromMemberRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassDecompositionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedClassFromDefinitionRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectComplementOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectHasSelfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectIntersectionOfDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.IndexedObjectSomeValuesFromDecomposition;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromFirstConjunctRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.ObjectIntersectionFromSecondConjunctRule;
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

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(SaturationGraphValidationStage.class);
	private final ContextValidator contextValidator_ = new ContextValidator();

	private final ClassExpressionValidator iceValidator_ = new ClassExpressionValidator();

	private final ObjectPropertyValidator iopValidator_ = new ObjectPropertyValidator();

	private final IndexedContextRootValidator rootValidator_ = new IndexedContextRootValidator();

	private final OntologyIndex index_;

	private final ContextRuleValidator ruleValidator_ = new ContextRuleValidator();

	private final SaturationState<?> saturationState_;

	public SaturationGraphValidationStage(final AbstractReasonerState reasoner) {
		index_ = reasoner.ontologyIndex;
		saturationState_ = reasoner.saturationState;
	}

	@Override
	public String getName() {
		return "Saturation graph validation";
	}

	@Override
	public void execute() {
		// starting from indexed class expressions
		for (IndexedClassExpression ice : index_.getClassExpressions()) {
			iceValidator_.add(ice);
		}
		for (;;) {
			if (iceValidator_.validate() || contextValidator_.validate())
				continue;
			break;
		}
	}

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
			Context context = saturationState_.getContext(ice);
			if (context != null) {
				contextValidator_.add(context);
			}
			if (ice instanceof IndexedObjectSomeValuesFrom) {
				IndexedContextRoot root = ((IndexedObjectSomeValuesFrom) ice)
						.getRangeFiller();
				context = saturationState_.getContext(root);
				if (context != null) {
					contextValidator_.add(context);
				}
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
	private class ObjectPropertyValidator {

		private final Set<IndexedObjectProperty> cache_ = new ArrayHashSet<IndexedObjectProperty>();

		private final Queue<IndexedObjectProperty> toValidate_ = new LinkedList<IndexedObjectProperty>();

		boolean add(IndexedObjectProperty property) {
			if (cache_.add(property)) {
				toValidate_.add(property);
				return true;
			}
			return false;
		}

		void checkNew(IndexedObjectProperty property) {
			if (add(property)) {
				LOGGER_.error("Unexpected reachable object property: "
						+ property);
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
				IndexedObjectProperty property = toValidate_.poll();
				if (property == null)
					break;
				validate(property);
			}
			return true;
		}

		private void validate(IndexedObjectProperty property) {
			LOGGER_.trace("Validating object property {}", property);

			// told ranges
			for (IndexedClassExpression ice : property.getToldRanges()) {
				iceValidator_.checkNew(ice);
			}

			// told super properties
			for (IndexedObjectProperty iop : property.getToldSuperProperties()) {
				iopValidator_.checkNew(iop);
			}

		}
	}

	private class IndexedContextRootValidator extends
			DummyIndexedContextRootVisitor<Void> implements
			IndexedContextRoot.Visitor<Void> {

		@Override
		protected Void defaultVisit(IndexedClassExpression element) {
			iceValidator_.checkNew(element);
			return null;
		}

		@Override
		public Void visit(IndexedRangeFiller element) {
			iceValidator_.checkNew(element.getFiller());
			return null;
		}
	}

	/**
	 * 
	 */
	private class ContextRuleValidator implements RuleVisitor<Void> {

		@Override
		public Void visit(BackwardLinkChainFromBackwardLinkRule rule,
				BackwardLink premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedPropertyChain prop : rule
					.getForwardLinksByObjectProperty().keySet()) {
				for (IndexedContextRoot target : rule
						.getForwardLinksByObjectProperty().get(prop)) {
					target.accept(rootValidator_);
				}
			}
			return null;
		}

		@Override
		public Void visit(ContradictionCompositionRule rule,
				DisjointSubsumer premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(ContradictionFromNegationRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			iceValidator_.checkNew(rule.getNegation());
			return null;
		}

		@Override
		public Void visit(ContradictionFromOwlNothingRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(
				ContradictionOverBackwardLinkRule bottomBackwardLinkRule,
				BackwardLink premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(ContradictionPropagationRule rule,
				Contradiction premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(DisjointSubsumerFromMemberRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedClassExpressionList disjoint : rule
					.getDisjointnessAxioms()) {
				if (!disjoint.occurs()) {
					LOGGER_.error("Dead disjointness axiom: " + disjoint);
				}

				for (IndexedClassExpression ice : disjoint.getElements()) {
					iceValidator_.checkNew(ice);
				}
			}
			return null;
		}

		@Override
		public Void visit(IndexedObjectComplementOfDecomposition rule,
				IndexedObjectComplementOf premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(IndexedObjectIntersectionOfDecomposition rule,
				IndexedObjectIntersectionOf premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(IndexedObjectSomeValuesFromDecomposition rule,
				IndexedObjectSomeValuesFrom premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(IndexedObjectHasSelfDecomposition rule,
				IndexedObjectHasSelf premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(NonReflexiveBackwardLinkCompositionRule rule,
				ForwardLink premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(SubsumerPropagationRule rule, Propagation premise,
				ContextPremises premises, ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		void validate(AbstractObjectIntersectionFromConjunctRule rule) {
			for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : rule
					.getConjunctionsByConjunct().entrySet()) {
				iceValidator_.checkNew(entry.getKey());
				iceValidator_.checkNew(entry.getValue());
			}

		}

		@Override
		public Void visit(ObjectIntersectionFromFirstConjunctRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			validate(rule);
			return null;
		}

		@Override
		public Void visit(ObjectIntersectionFromSecondConjunctRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			validate(rule);
			return null;
		}

		@Override
		public Void visit(ObjectUnionFromDisjunctRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getDisjunctions()) {
				iceValidator_.checkNew(ice);
			}
			return null;
		}

		@Override
		public Void visit(OwlThingContextInitRule rule,
				ContextInitialization premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(PropagationFromExistentialFillerRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getNegativeExistentials()) {
				iceValidator_.checkNew(ice);
			}
			return null;
		}

		@Override
		public Void visit(ReflexiveBackwardLinkCompositionRule rule,
				ForwardLink premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(RootContextInitializationRule rule,
				ContextInitialization premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(SubsumerBackwardLinkRule rule, BackwardLink premise,
				ContextPremises premises, ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(SuperClassFromSubClassRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getToldSubsumers()) {
				iceValidator_.checkNew(ice);
			}
			return null;
		}

		@Override
		public Void visit(PropagationInitializationRule rule,
				SubContextInitialization premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(BackwardLinkFromForwardLinkRule rule,
				ForwardLink premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(ComposedFromDecomposedSubsumerRule rule,
				IndexedClassEntity premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(IndexedClassDecompositionRule rule, IndexedClass premise,
				ContextPremises premises, ClassConclusionProducer producer) {
			// nothing is stored in the rule
			return null;
		}

		@Override
		public Void visit(IndexedClassFromDefinitionRule rule,
				IndexedClassExpression premise, ContextPremises premises,
				ClassConclusionProducer producer) {
			for (IndexedClassExpression ice : rule.getDefinedClasses()) {
				iceValidator_.checkNew(ice);
			}
			return null;
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
			IndexedContextRoot root = context.getRoot();
			root.accept(rootValidator_);
			if (saturationState_.getContext(root) != context)
				LOGGER_.error("Invalid root for " + context);

			// validating subsumers recursively
			for (IndexedClassExpression composedSubsumer : context
					.getComposedSubsumers()) {
				iceValidator_.checkNew(composedSubsumer);
			}
			for (IndexedClassExpression decomposedSubsumer : context
					.getDecomposedSubsumers()) {
				iceValidator_.checkNew(decomposedSubsumer);
			}

			// validating sub-contexts
			for (SubContextPremises subContext : context
					.getSubContextPremisesByObjectProperty().values()) {
				for (IndexedContextRoot linkedRoot : subContext
						.getLinkedRoots()) {
					linkedRoot.accept(rootValidator_);
				}
				for (IndexedContextRoot propagation : subContext
						.getLinkedRoots()) {
					propagation.accept(rootValidator_);
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

}
