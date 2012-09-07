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
package org.semanticweb.elk.reasoner.saturation.classes;

import java.util.Collection;
import java.util.Set;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * TODO: documentation
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <C>
 *            the type of contexts that can be used with this inference rule
 */
public class RuleExistentialPlus<C extends ContextElClassSaturation> implements
		InferenceRuleSCE<C> {

	public void apply(BackwardLink<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		final IndexedPropertyChain linkRelation = argument.getRelation();
		final Context target = argument.getTarget();

		// apply all propagations over the link
		final Multimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>> props = context
				.getPropagationsByObjectProperty();
		if (props == null)
			return;

		Collection<Queueable<? extends ContextElClassSaturation>> carrys = props
				.get(linkRelation);

		if (carrys == null)
			return;

		for (Queueable<?> carry : carrys)
			engine.enqueue(target, carry);
	}

	@Override
	public void applySCE(SuperClassExpression<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		final Collection<IndexedObjectSomeValuesFrom> exists = argument
				.getExpression().getNegExistentials();

		final Set<IndexedPropertyChain> candidatePropagationProperties = context
				.getRoot().getPosPropertiesInExistentials();

		if (exists == null || candidatePropagationProperties == null)
			return;

		for (IndexedObjectSomeValuesFrom e : exists) {
			IndexedPropertyChain relation = e.getRelation();
			// creating propagations for relevant sub-properties of the relation
			for (IndexedPropertyChain property : new LazySetIntersection<IndexedPropertyChain>(
					candidatePropagationProperties, relation.getSaturated()
							.getSubProperties())) {
				addPropagation(property,
						new NegativeSuperClassExpression<C>(e), context, engine);
			}

			// creating propagations for relevant sub-compositions of the
			// relation
			for (IndexedPropertyChain property : relation.getSaturated()
					.getSubCompositions()) {
				SaturatedPropertyChain propertySaturation = property
						.getSaturated();
				if (!new LazySetIntersection<IndexedPropertyChain>(
						candidatePropagationProperties,
						propertySaturation.getRightSubProperties()).isEmpty()) {
					addPropagation(property,
							new NegativeSuperClassExpression<C>(e), context,
							engine);
				}
			}

			// propagating to this context if relation is reflexive
			if (relation.getSaturated().isReflexive())
				engine.enqueue(context, new NegativeSuperClassExpression<C>(e));
		}

	}

	private void addPropagation(IndexedPropertyChain propRelation,
			Queueable<C> carry, C context, RuleApplicationFactory.Engine engine) {

		if (context.propagationsByObjectProperty == null) {
			context.initPropagationsByProperty();
		}

		if (context.propagationsByObjectProperty.add(propRelation, carry)) {

			// propagate over all backward links
			final Multimap<IndexedPropertyChain, ContextElClassSaturation> backLinks = context
					.getBackwardLinksByObjectProperty();

			if (backLinks == null) // this should never happen
				return;

			Collection<ContextElClassSaturation> targets = backLinks
					.get(propRelation);

			if (targets == null)
				return;

			for (Context target : targets)
				engine.enqueue(target, carry);
		}
	}

}
