/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDisjointnessAxiomWithBinding;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectPropertyWithBinding;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.LinkedSubsumerRule;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.SuperClassFromSubClassRuleWithAxiomBinding;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.TracedPropagation;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ObjectPropertyInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ReflexiveToldSubObjectProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.ToldReflexiveProperty;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.properties.TopDownPropertySubsumptionInference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.AbstractObjectPropertyInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ObjectPropertyInferenceVisitor;
import org.semanticweb.elk.util.collections.chains.Matcher;
import org.semanticweb.elk.util.collections.chains.SimpleTypeBasedMatcher;

/**
 * Given a {@link ClassInference} or a {@link ObjectPropertyInference} tries to
 * look up its side condition, if one exists, using the axiom binding.
 * 
 * TODO make it a visitor over class and object property inferences,
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class SideConditionLookup {

	private ClassInferenceVisitor<Void, ElkAxiom> classAxiomGetter = new AbstractClassInferenceVisitor<Void, ElkAxiom>() {

		@Override
		protected ElkAxiom defaultTracedVisit(ClassInference conclusion,
				Void ignored) {
			// by default rules aren't bound to axioms, only some are
			return null;
		}

		@Override
		public ElkAxiom visit(SubClassOfSubsumer<?> inference,
				Void ignored) {
			// looking for a super class rule
			SuperClassFromSubClassRuleWithAxiomBinding ruleWithBinding = find(
					inference.getPremise().getExpression().getCompositionRuleHead(),
					new SimpleTypeBasedMatcher<LinkedSubsumerRule, SuperClassFromSubClassRuleWithAxiomBinding>(
							SuperClassFromSubClassRuleWithAxiomBinding.class));
			// if we found a rule with axiom binding, we can then look for the
			// asserted axiom which corresponds to this derived subsumer
			if (ruleWithBinding != null) {
				return ruleWithBinding.getAxiomForConclusion(inference.getExpression());
			}

			return null;
		}

		@Override
		public ElkAxiom visit(DisjointSubsumerFromSubsumer inference,
				Void input) {
			IndexedDisjointnessAxiom indexedAxiom = inference.getAxiom();
			
			return indexedAxiom instanceof IndexedDisjointnessAxiomWithBinding 
					? ((IndexedDisjointnessAxiomWithBinding) indexedAxiom).getAssertedAxiom() 
					: null;
		}
		
		@Override
		public ElkAxiom visit(ContradictionFromDisjointSubsumers inference,
				Void input) {
			IndexedDisjointnessAxiom indexedAxiom = inference.getAxiom();
			
			return indexedAxiom instanceof IndexedDisjointnessAxiomWithBinding 
					? ((IndexedDisjointnessAxiomWithBinding) indexedAxiom).getAssertedAxiom() 
					: null;
		}

		@Override
		public ElkAxiom visit(ContradictionFromInconsistentDisjointnessAxiom inference,
				Void input) {
			IndexedDisjointnessAxiom indexedAxiom = inference.getAxiom();
			
			return indexedAxiom instanceof IndexedDisjointnessAxiomWithBinding 
					? ((IndexedDisjointnessAxiomWithBinding) indexedAxiom).getAssertedAxiom() 
					: null;
		}

		@Override
		public ElkAxiom visit(ComposedBackwardLink inference, Void input) {
			if (inference.getRelation() instanceof IndexedObjectPropertyWithBinding) {
				IndexedObjectPropertyWithBinding propertyWithBinding = (IndexedObjectPropertyWithBinding) inference.getRelation();
				IndexedPropertyChain subChain = inference.getSubPropertyChain().getSubPropertyChain();
				
				if (subChain != inference.getRelation()) {
					ElkObjectPropertyAxiom axiom = propertyWithBinding.getSubChainAxiom(inference.getSubPropertyChain().getSubPropertyChain());
				
					return axiom;
				}
			}
			
			return null;
		}

		@Override
		public ElkAxiom visit(ReversedForwardLink inference, Void input) {
			if (inference.getRelation() instanceof IndexedObjectPropertyWithBinding) {
				IndexedObjectPropertyWithBinding propertyWithBinding = (IndexedObjectPropertyWithBinding) inference.getRelation();
				IndexedPropertyChain subChain = inference.getSubPropertyChain().getSubPropertyChain();
				
				if (subChain != inference.getRelation()) {
					ElkObjectPropertyAxiom axiom = propertyWithBinding.getSubChainAxiom(inference.getSubPropertyChain().getSubPropertyChain());
				
					return axiom;
				}
			}
			
			return null;
		}

		@Override
		public ElkAxiom visit(TracedPropagation inference, Void input) {
			IndexedObjectProperty superProperty = inference.getCarry().getRelation();
			
			if (superProperty instanceof IndexedObjectPropertyWithBinding) {
				IndexedObjectPropertyWithBinding propertyWithBinding = (IndexedObjectPropertyWithBinding) superProperty;
				IndexedPropertyChain subProperty = inference.getRelation();
				
				if (subProperty != propertyWithBinding) {
					ElkObjectPropertyAxiom axiom = propertyWithBinding.getSubChainAxiom(subProperty);
				
					return axiom;
				}
			}
			
			return null;
		}
		
	};
	
	private ObjectPropertyInferenceVisitor<Void, ElkObjectPropertyAxiom> propertyAxiomGetter = new AbstractObjectPropertyInferenceVisitor<Void, ElkObjectPropertyAxiom>() {

		@Override
		protected ElkObjectPropertyAxiom defaultTracedVisit(
				ObjectPropertyInference inference, Void input) {
			// no side conditions by default
			return null;
		}

		@Override
		public ElkObjectPropertyAxiom visit(ToldReflexiveProperty inference,
				Void input) {
			IndexedObjectPropertyWithBinding property = withBinding(inference.getPropertyChain());
			
			if (property != null) {
				return property.getReflexivityAxiom();
			}
			
			return null;
		}

		@Override
		public ElkObjectPropertyAxiom visit(
				ReflexiveToldSubObjectProperty inference, Void input) {
			IndexedObjectPropertyWithBinding property = withBinding(inference.getPropertyChain());
			
			if (property != null) {
				return property.getSubChainAxiom(inference.getSubProperty().getPropertyChain());
			}
			
			return null;
		}

		@Override
		public ElkObjectPropertyAxiom visit(
				TopDownPropertySubsumptionInference inference, Void input) {
			IndexedObjectPropertyWithBinding property = withBinding(inference.getPremise().getSubPropertyChain());
			
			if (property != null) {
				return property.getSubChainAxiom(inference.getSubPropertyChain());
			}
			
			return null;
		}

	};
	
	private IndexedObjectPropertyWithBinding withBinding(IndexedObjectProperty property) {
		return property instanceof IndexedObjectPropertyWithBinding ? (IndexedObjectPropertyWithBinding) property : null;
	}

	public ElkAxiom lookup(ClassInference inference) {
		return inference.acceptTraced(classAxiomGetter, null);
	}

	public ElkObjectPropertyAxiom lookup(ObjectPropertyInference inference) {
		return inference.acceptTraced(propertyAxiomGetter, null);
	}

	// FIXME Why can't we have this for any Link<LinkRule>?
	private static <O> O find(LinkedSubsumerRule link,
			Matcher<LinkedSubsumerRule, O> matcher) {
		LinkedSubsumerRule candidate = link;
		for (;;) {
			if (candidate == null)
				return null;
			O match = matcher.match(candidate);
			if (match != null)
				return match;
			candidate = candidate.next();
		}
	}
}
