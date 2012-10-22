/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.PositiveSuperClassExpression;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.chains.Chain;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.ReferenceFactory;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * Defines the disjointness inference rule for indexed class expressions
 * 
 * @author Frantisek Simancik
 * @author Pavel Klinov
 * 
 */
public class IndexedDisjointnessAxiom extends IndexedAxiom {

	private final List<IndexedClassExpression> members_;
	private final IndexedClassExpression first_;
	private final IndexedClassExpression second_;

	IndexedDisjointnessAxiom(List<IndexedClassExpression> members) {
		members_ = members;
		first_ = second_ = null;
	}

	IndexedDisjointnessAxiom(IndexedClassExpression first,
			IndexedClassExpression second) {
		first_ = first;
		second_ = second;
		members_ = null;
	}

	public List<IndexedClassExpression> getMembers() {
		return members_ != null ? members_ : Arrays.asList(first_, second_);
	}

	@Override
	protected void updateOccurrenceNumbers(final IndexUpdater indexUpdater, final int increment) {
		if (increment > 0) {
			registerCompositionRule(indexUpdater);
		} else {
			deregisterCompositionRule(indexUpdater);
		}
	}

	public void registerCompositionRule(IndexUpdater indexUpdater) {
		if (members_ == null) {
			indexUpdater.add(first_, new ThisBinaryRegistrationRule(second_));
			indexUpdater.add(second_, new ThisBinaryRegistrationRule(first_));
		}
		else {
			ThisNaryRegistrationRule regRule = new ThisNaryRegistrationRule(); 
			
			for (IndexedClassExpression ice : members_) {
				indexUpdater.add(ice, regRule);
			}
		}
	}

	public void deregisterCompositionRule(IndexUpdater indexUpdater) {
		if (members_ == null) {
			indexUpdater.remove(first_, new ThisBinaryRegistrationRule(second_));
			indexUpdater.remove(second_, new ThisBinaryRegistrationRule(first_));
		} else {
			ThisNaryRegistrationRule regRule = new ThisNaryRegistrationRule();
			
			for (IndexedClassExpression ice : members_) {
				indexUpdater.remove(ice, regRule);
			}
		}
	}

	
	/**
	 * That's the actual disjointness rule which is registered as a context rule
	 * for class expressions.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class ThisCompositionRule extends ContextRules {

		/**
		 * {@link IndexedClassExpression} that appear in binary disjointess
		 * axioms with this object.
		 */
		private Set<IndexedClassExpression> disjointClasses_;

		/**
		 * List of all larger (non-binary) disjointness axioms in which this
		 * object appears.
		 * 
		 */
		private List<IndexedDisjointnessAxiom> disjointnessAxioms_;

		public ThisCompositionRule(ContextRules tail) {
			super(tail);
		}

		@Override
		public void apply(SaturationState state, Context context) {

			// System.err.println("Disjointness rule: " +
			// IndexedClassExpression.this + " -> " + context.getRoot());

			if (disjointClasses_ != null
					&& !new LazySetIntersection<IndexedClassExpression>(
							disjointClasses_,
							context.getSuperClassExpressions()).isEmpty()) {

				state.produce(context, new PositiveSuperClassExpression(
						state.getOwlNothing()));
				return;
			}

			if (disjointnessAxioms_ != null)
				for (IndexedDisjointnessAxiom disAxiom : disjointnessAxioms_)
					if (!context.addDisjointnessAxiom(disAxiom))
						state.produce(
								context,
								new PositiveSuperClassExpression(state
										.getOwlNothing()));

		}

		protected boolean isEmpty() {
			return disjointClasses_ == null && disjointnessAxioms_ == null;
		}

		protected boolean addDisjointClass(IndexedClassExpression disjointClass) {
			if (disjointClasses_ == null) {
				disjointClasses_ = new ArrayHashSet<IndexedClassExpression>();
			}

			return disjointClasses_.add(disjointClass);
		}

		/**
		 * @param disjointClass
		 * @return true if successfully removed
		 */
		protected boolean removeDisjointClass(
				IndexedClassExpression disjointClass) {
			boolean success = false;
			if (disjointClasses_ != null) {
				success = disjointClasses_.remove(disjointClass);

				if (disjointClasses_.isEmpty()) {
					disjointClasses_ = null;
				}
			}
			
			return success;
		}

		protected boolean addDisjointnessAxiom(
				IndexedDisjointnessAxiom disjointnessAxiom) {
			if (disjointnessAxioms_ == null) {
				disjointnessAxioms_ = new LinkedList<IndexedDisjointnessAxiom>();
			}

			return disjointnessAxioms_.add(disjointnessAxiom);
		}

		/**
		 * @param disjointnessAxiom
		 * @return true if successfully removed
		 */
		protected boolean removeDisjointnessAxiom(
				IndexedDisjointnessAxiom disjointnessAxiom) {
			boolean success = false;

			if (disjointnessAxioms_ != null) {
				Iterator<IndexedDisjointnessAxiom> i = disjointnessAxioms_
						.iterator();
				while (i.hasNext())
					if (i.next().getMembers()
							.equals(disjointnessAxiom.getMembers())) {
						i.remove();
						break;
					}

				if (disjointnessAxioms_.isEmpty()) {
					disjointnessAxioms_ = null;
				}
			}

			return success;
		}

		private static Matcher<ContextRules, ThisCompositionRule> MATCHER_ = new SimpleTypeBasedMatcher<ContextRules, ThisCompositionRule>(
				ThisCompositionRule.class);

		private static ReferenceFactory<ContextRules, ThisCompositionRule> FACTORY_ = new ReferenceFactory<ContextRules, ThisCompositionRule>() {
			@Override
			public ThisCompositionRule create(ContextRules tail) {
				return new ThisCompositionRule(tail);
			}
		};

		@Override
		public boolean addTo(Chain<ContextRules> ruleChain) {
			return disjointClasses_ != null ? addTo(ruleChain, disjointClasses_) : addTo(ruleChain, disjointnessAxioms_);
		}

		@Override
		public boolean removeFrom(Chain<ContextRules> ruleChain) {
			return disjointClasses_ != null ? removeFrom(ruleChain, disjointClasses_) : removeFrom(ruleChain, disjointnessAxioms_);
		}
		
		public static boolean addTo(Chain<ContextRules> ruleChain, Set<IndexedClassExpression> classes) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			boolean changed = false;
			
			for (IndexedClassExpression disjoint : classes) {
				changed |= rule.addDisjointClass(disjoint);
			}
			
			return changed;
		}
		
		public static boolean addTo(Chain<ContextRules> ruleChain, List<IndexedDisjointnessAxiom> axioms) {
			ThisCompositionRule rule = ruleChain.getCreate(MATCHER_, FACTORY_);
			boolean changed = false;
			
			for (IndexedDisjointnessAxiom axiom : axioms) {
				changed |= rule.addDisjointnessAxiom(axiom);
			}
			
			return changed;
		}		

		public static boolean removeFrom(Chain<ContextRules> ruleChain, Set<IndexedClassExpression> classes) {
			ThisCompositionRule rule = ruleChain.find(ThisCompositionRule.MATCHER_);
			boolean changed = false;
			
			if (rule != null) {
				for (IndexedClassExpression disjoint : classes) {
					changed |= rule.removeDisjointClass(disjoint);
				}
				
				if (rule.isEmpty()) {
					ruleChain.remove(ThisCompositionRule.MATCHER_);
					
					return true;
				}				
			}
			
			return changed;
		}		
		
		public static boolean removeFrom(Chain<ContextRules> ruleChain, List<IndexedDisjointnessAxiom> axioms) {
			ThisCompositionRule rule = ruleChain.find(ThisCompositionRule.MATCHER_);
			boolean changed = false;
			
			if (rule != null) {
				for (IndexedDisjointnessAxiom axiom : axioms) {
					changed |= rule.removeDisjointnessAxiom(axiom);
				}
				
				if (rule.isEmpty()) {
					ruleChain.remove(ThisCompositionRule.MATCHER_);
					
					return true;
				}				
			}
			
			return changed;
		}
	}
	
	/**
	 */
	private static class ThisBinaryRegistrationRule extends ContextRules {

		private final IndexedClassExpression disjointClass_;
		
		public ThisBinaryRegistrationRule(IndexedClassExpression ice) {
			super(null);
			disjointClass_ = ice;
		}

		@Override
		public void apply(SaturationState state, Context element) {}

		@Override
		public boolean addTo(Chain<ContextRules> ruleChain) {
			return ThisCompositionRule.addTo(ruleChain, Collections.singleton(disjointClass_));
		}

		@Override
		public boolean removeFrom(Chain<ContextRules> ruleChain) {
			return ThisCompositionRule.removeFrom(ruleChain, Collections.singleton(disjointClass_));
		}
	}
	
	/**
	 * 
	 */
	private class ThisNaryRegistrationRule extends ContextRules {

		public ThisNaryRegistrationRule() {
			super(null);
		}

		@Override
		public void apply(SaturationState state, Context element) {}

		@Override
		public boolean addTo(Chain<ContextRules> ruleChain) {
			return ThisCompositionRule.addTo(ruleChain, Collections.singletonList(IndexedDisjointnessAxiom.this));
		}

		@Override
		public boolean removeFrom(Chain<ContextRules> ruleChain) {
			return ThisCompositionRule.removeFrom(ruleChain, Collections.singletonList(IndexedDisjointnessAxiom.this));
		}
	}	
}