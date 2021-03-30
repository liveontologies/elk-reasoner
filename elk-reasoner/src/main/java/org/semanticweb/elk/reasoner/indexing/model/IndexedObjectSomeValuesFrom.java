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
package org.semanticweb.elk.reasoner.indexing.model;

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ClassInferenceProducer;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;

/**
 * Represents occurrences of an {@link ElkObjectSomeValuesFrom} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedObjectSomeValuesFrom extends IndexedComplexClassExpression {

	/**
	 * @return The representation of the {@link ElkObjectProperty} that is a
	 *         property of the {@link ElkObjectSomeValuesFrom} represented by
	 *         this {@link IndexedObjectSomeValuesFrom}.
	 * 
	 * @see ElkObjectSomeValuesFrom#getProperty()
	 */
	IndexedObjectProperty getProperty();

	/**
	 * @return The representation of the {@link ElkClassExpression} that is a
	 *         filler of the {@link ElkObjectSomeValuesFrom} represented by this
	 *         {@link IndexedObjectSomeValuesFrom}.
	 * 
	 * @see ElkObjectSomeValuesFrom#getFiller()
	 */
	IndexedClassExpression getFiller();

	/**
	 * @return The {@link IndexedRangeFiller} corresponding to this
	 *         {@link IndexedObjectSomeValuesFrom}, i.e., having the same
	 *         property and filler.
	 */
	IndexedRangeFiller getRangeFiller();

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {
		
		O visit(IndexedObjectSomeValuesFrom element);
		
	}

	class Helper {

		/**
		 * Generates {@link PropagationImpl}s for the {@link ContextPremises}
		 * that apply for the given {@link IndexedObjectProperty}
		 * 
		 * @param property
		 * @param premises
		 * @param producer
		 */
		public static void generatePropagations(IndexedObjectProperty property,
				ContextPremises premises, ClassInferenceProducer producer) {
			for (IndexedClassExpression ice : premises.getComposedSubsumers()) {
				PropagationFromExistentialFillerRule.applyForProperty(
						ice.getCompositionRuleHead(), property, premises,
						producer);
			}
		}

		/**
		 * @param existential
		 * @return the {@link IndexedContextRoot} that is required for
		 *         decomposition of the given
		 *         {@link IndexedObjectSomeValuesFrom}, taking into account the
		 *         property ranges, if necessary
		 */
		public static IndexedContextRoot getTarget(
				IndexedObjectSomeValuesFrom existential) {
			SaturatedPropertyChain propertySaturation = existential
					.getProperty().getSaturated();
			if (propertySaturation.getRanges().isEmpty()) {
				// filler is sufficient
				return existential.getFiller();
			}	
			// else we also need to take the property into account
			return existential.getRangeFiller();
		}

		public static void produceDecomposedExistentialLink(
				ClassInferenceProducer producer, IndexedContextRoot root,
				IndexedObjectSomeValuesFrom subsumer) {
			producer.produce(
					new BackwardLinkOfObjectSomeValuesFrom(root, subsumer));
			if (!subsumer.getProperty().getSaturated()
					.getNonRedundantCompositionsByLeftSubProperty().isEmpty()) {
				producer.produce(
						new ForwardLinkOfObjectSomeValuesFrom(root, subsumer));
			}
		}

		public static void produceComposedLink(ClassInferenceProducer producer,
				IndexedContextRoot source,
				IndexedObjectProperty backwardRelation,
				IndexedContextRoot inferenceRoot,
				IndexedPropertyChain forwardRelation, IndexedContextRoot target,
				IndexedComplexPropertyChain composition) {

			if (composition.getSaturated()
					.getNonRedundantCompositionsByLeftSubProperty().isEmpty()
					&& composition.getSaturated()
							.getRedundantCompositionsByLeftSubProperty()
							.isEmpty()) {
				// composition cannot be composed further, even using redundant
				// compositions (tracing); create only backward links
				List<IndexedObjectProperty> toldSuperProperties = composition
						.getToldSuperProperties();
				List<ElkAxiom> toldSuperPropertiesReasons = composition
						.getToldSuperPropertiesReasons();
				for (int i = 0; i < toldSuperProperties.size(); i++) {
					producer.produce(new BackwardLinkComposition(source,
							backwardRelation, inferenceRoot, forwardRelation,
							target, composition, toldSuperProperties.get(i),
							toldSuperPropertiesReasons.get(i)));
				}
			} else {
				producer.produce(new ForwardLinkComposition(source,
						backwardRelation, inferenceRoot, forwardRelation,
						target, composition));
			}
		}

	}

}
