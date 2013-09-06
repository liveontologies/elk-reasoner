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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.reasoner.indexing.OntologyIndex;
import org.semanticweb.elk.reasoner.indexing.hierarchy.DirectIndex.ContextRootInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass.OwlThingContextInitializationRule;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Propagation;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;

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

	private final OntologyIndex index_;
	private final ClassExpressionValidator iceValidator_ = new ClassExpressionValidator();
	private final ContextValidator contextValidator_ = new ContextValidator();
	private final ContextRuleValidator ruleValidator_ = new ContextRuleValidator();

	public SaturationGraphValidationStage(final AbstractReasonerState reasoner) {
		index_ = reasoner.ontologyIndex;
	}

	@Override
	public String getName() {
		return "Saturation graph validation";
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

	/**
	 * 
	 */
	private class ClassExpressionValidator {

		private final Queue<IndexedClassExpression> toValidate_ = new LinkedList<IndexedClassExpression>();

		private final Set<IndexedClassExpression> cache_ = new ArrayHashSet<IndexedClassExpression>();

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
			LinkRule<Context> rule = ice.getCompositionRuleHead();

			while (rule != null) {
				rule.accept(ruleValidator_, null, null);
				rule = rule.next();
			}
		}
	}

	/**
	 * 
	 * 
	 */
	private class ContextValidator {

		private final Queue<Context> toValidate_ = new LinkedList<Context>();

		private final Set<Context> cache_ = new ArrayHashSet<Context>();

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
				for (Context linkedContext : context
						.getBackwardLinksByObjectProperty().get(prop)) {
					add(linkedContext);
				}
			}

			// validating backward link rules
			LinkRule<BackwardLink> rule = context.getBackwardLinkRuleHead();

			while (rule != null) {
				rule.accept(ruleValidator_, null, null);
				rule = rule.next();
			}
		}
	}

	/**
	 * 
	 */
	private class ContextRuleValidator implements RuleApplicationVisitor {

		@Override
		public void visit(
				OwlThingContextInitializationRule owlThingContextInitializationRule,
				BasicSaturationStateWriter writer, Context context) {
		}

		@Override
		public void visit(
				IndexedDisjointnessAxiom.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			for (IndexedDisjointnessAxiom axiom : thisCompositionRule
					.getDisjointnessAxioms()) {
				if (!axiom.occurs()) {
					LOGGER_.error("Dead disjointness axiom: " + axiom);
				}

				for (IndexedClassExpression ice : axiom.getDisjointMembers()) {
					iceValidator_.checkNew(ice);
				}
			}

		}

		@Override
		public void visit(
				IndexedObjectComplementOf.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			iceValidator_.checkNew(thisCompositionRule.getNegation());

		}

		@Override
		public void visit(
				IndexedObjectIntersectionOf.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			for (Map.Entry<IndexedClassExpression, IndexedObjectIntersectionOf> entry : thisCompositionRule
					.getConjunctionsByConjunct().entrySet()) {
				iceValidator_.checkNew(entry.getKey());
				iceValidator_.checkNew(entry.getValue());
			}
		}

		@Override
		public void visit(
				IndexedSubClassOfAxiom.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			for (IndexedClassExpression ice : thisCompositionRule
					.getToldSuperclasses()) {
				iceValidator_.checkNew(ice);
			}

		}

		@Override
		public void visit(
				IndexedObjectSomeValuesFrom.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			for (IndexedClassExpression ice : thisCompositionRule
					.getNegativeExistentials()) {
				iceValidator_.checkNew(ice);
			}
		}

		@Override
		public void visit(
				IndexedObjectUnionOf.ThisCompositionRule thisCompositionRule,
				BasicSaturationStateWriter writer, Context context) {
			for (IndexedClassExpression ice : thisCompositionRule
					.getDisjunctions()) {
				iceValidator_.checkNew(ice);
			}
		}

		@Override
		public void visit(
				IndexedDisjointnessAxiom.ThisContradictionRule thisContradictionRule,
				BasicSaturationStateWriter writer, Context context) {
		}

		@Override
		public void visit(
				ForwardLink.ThisBackwardLinkRule thisBackwardLinkRule,
				BasicSaturationStateWriter writer, BackwardLink backwardLink) {
			for (IndexedPropertyChain prop : thisBackwardLinkRule
					.getForwardLinksByObjectProperty().keySet()) {
				for (Context context : thisBackwardLinkRule
						.getForwardLinksByObjectProperty().get(prop)) {
					contextValidator_.add(context);
				}
			}
		}

		@Override
		public void visit(
				Propagation.ThisBackwardLinkRule thisBackwardLinkRule,
				BasicSaturationStateWriter writer, BackwardLink backwardLink) {
			for (IndexedPropertyChain prop : thisBackwardLinkRule
					.getPropagationsByObjectProperty().keySet()) {
				for (IndexedClassExpression ice : thisBackwardLinkRule
						.getPropagationsByObjectProperty().get(prop)) {
					iceValidator_.checkNew(ice);
				}
			}
		}

		@Override
		public void visit(
				Contradiction.ContradictionBackwardLinkRule bottomBackwardLinkRule,
				BasicSaturationStateWriter writer, BackwardLink backwardLink) {
		}

		@Override
		public void visit(ContextRootInitializationRule rootInitRule,
				BasicSaturationStateWriter writer, Context context) {
		}

	}

}
