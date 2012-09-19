/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassEntityVisitor;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.BackwardLinkRules;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.collections.Multimap;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * Represents all occurrences of an ElkClass in an ontology.
 * 
 * @author Frantisek Simancik
 * 
 */
public class IndexedClass extends IndexedClassEntity {

	/**
	 * The indexed ElkClass
	 */
	protected final ElkClass elkClass;

	/**
	 * This counts how many times this object occurred in the ontology. Because
	 * of declaration axioms, this number might differ from the sum of the
	 * negative and the positive occurrences counts
	 */
	protected int occurrenceNo = 0;

	/**
	 * Creates an object representing the given ElkClass.
	 */
	protected IndexedClass(ElkClass elkClass) {
		this.elkClass = elkClass;
	}

	/**
	 * @return The represented ElkClass.
	 */
	public ElkClass getElkClass() {
		return elkClass;
	}

	public <O> O accept(IndexedClassVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(IndexedClassEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	protected void updateOccurrenceNumbers(int increment,
			int positiveIncrement, int negativeIncrement) {
		occurrenceNo += increment;
		positiveOccurrenceNo += positiveIncrement;
		negativeOccurrenceNo += negativeIncrement;
	}

	@Override
	public boolean occurs() {
		return occurrenceNo > 0;
	}

	@Override
	public void applyDecompositionRule(RuleEngine ruleEngine, Context context) {

		RulesTimer timer = ruleEngine.getRulesTimer();

		timer.timeClassDecompositionRule -= CachedTimeThread
				.currentTimeMillis();

		try {
			if (this.equals(ruleEngine.getOwlNothing())) {
				context.setInconsistent();

				// propagating bottom to the predecessors
				final Multimap<IndexedPropertyChain, Context> backLinks = context
						.getBackwardLinksByObjectProperty();

				Conclusion carry = new PositiveSuperClassExpression(this);

				for (IndexedPropertyChain propRelation : backLinks.keySet()) {

					Collection<Context> targets = backLinks.get(propRelation);

					for (Context target : targets)
						ruleEngine.produce(target, carry);
				}

				// register the backward link rule for propagation of bottom
				context.getBackwardLinkRulesChain().getCreate(
						BottomBackwardLinkRule.MATCHER_,
						BottomBackwardLinkRule.FACTORY_);
			}
		} finally {
			timer.timeClassDecompositionRule += CachedTimeThread
					.currentTimeMillis();
		}
	}

	@Override
	public String toString() {
		return '<' + getElkClass().getIri().getFullIriAsString() + '>';
	}

	private static class BottomBackwardLinkRule extends BackwardLinkRules {

		BottomBackwardLinkRule(BackwardLinkRules tail) {
			super(tail);
		}

		@Override
		public void apply(RuleEngine ruleEngine, BackwardLink link) {
			RulesTimer timer = ruleEngine.getRulesTimer();

			timer.timeClassBottomBackwardLinkRule -= CachedTimeThread
					.currentTimeMillis();

			try {
				ruleEngine.produce(
						link.getSource(),
						new PositiveSuperClassExpression(ruleEngine
								.getOwlNothing()));
			} finally {
				timer.timeClassBottomBackwardLinkRule += CachedTimeThread
						.currentTimeMillis();
			}
		}

		private static Matcher<BackwardLinkRules, BottomBackwardLinkRule> MATCHER_ = new SimpleTypeBasedMatcher<BackwardLinkRules, BottomBackwardLinkRule>(BottomBackwardLinkRule.class);
		
		private static ReferenceFactory<BackwardLinkRules, BottomBackwardLinkRule> FACTORY_ = new ReferenceFactory<BackwardLinkRules, BottomBackwardLinkRule>() {
			@Override
			public BottomBackwardLinkRule create(BackwardLinkRules tail) {
				return new BottomBackwardLinkRule(tail);
			}
		};

	}

}