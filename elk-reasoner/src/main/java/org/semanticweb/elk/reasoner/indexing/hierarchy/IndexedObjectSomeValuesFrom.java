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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectSomeValuesFromVisitor;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.PropagationImpl;
import org.semanticweb.elk.reasoner.saturation.context.ContextPremises;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rules.ConclusionProducer;
import org.semanticweb.elk.reasoner.saturation.rules.subsumers.PropagationFromExistentialFillerRule;

/**
 * Represents occurrences of an {@link ElkObjectSomeValuesFrom} in an ontology.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public interface IndexedObjectSomeValuesFrom extends IndexedClassExpression {

	/**
	 * @return The representation of the {@link ElkObjectProperty} that is a
	 *         property of the {@link ElkObjectSomeValuesFrom} represented by
	 *         this {@link IndexedObjectSomeValuesFrom}.
	 * 
	 * @see ElkObjectSomeValuesFrom#getProperty()
	 */
	public IndexedObjectProperty getProperty();

	/**
	 * @return The representation of the {@link ElkClassExpression} that is a
	 *         filler of the {@link ElkObjectSomeValuesFrom} represented by this
	 *         {@link IndexedObjectSomeValuesFrom}.
	 * 
	 * @see ElkObjectSomeValuesFrom#getFiller()
	 */
	public IndexedClassExpression getFiller();

	/**
	 * @return The {@link IndexedRangeFiller} corresponding to this
	 *         {@link IndexedObjectSomeValuesFrom}, i.e., having the same
	 *         property and filler.
	 */
	public IndexedRangeFiller getRangeFiller();

	public <O> O accept(IndexedObjectSomeValuesFromVisitor<O> visitor);

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
				ContextPremises premises, ConclusionProducer producer) {
			for (IndexedClassExpression ice : premises.getSubsumers()) {
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
			if (propertySaturation.getRanges().isEmpty())
				// filler is sufficient
				return existential.getFiller();
			// else we also need to take the property into account
			return existential.getRangeFiller();
		}

	}

}
