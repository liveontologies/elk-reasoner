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
package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import java.util.List;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

public class RuleExistentialPlus<C extends ContextElClassSaturation> implements
		InferenceRule<C> {

	public void apply(BackwardLink<C> argument, C context,
			RuleApplicationEngine engine) {
		IndexedPropertyChain linkRelation = argument.getRelation();
		Context target = argument.getTarget();

		// start deriving propagations
		if (!context.derivePropagations) {
			context.setDerivePropagations(true);

			for (IndexedClassExpression ice : context.superClassExpressions)
				if (ice.getNegExistentials() != null)
					for (IndexedObjectSomeValuesFrom e : ice
							.getNegExistentials())
						addPropagation(e.getRelation(),
								new NegativeSuperClassExpression<C>(e),
								context, engine);
		}

		// apply all propagations over the link
		Multimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>> props = context.propagationsByObjectProperty;
		if (props == null)
			return;

		for (IndexedPropertyChain propRelation : new LazySetIntersection<IndexedPropertyChain>(
				linkRelation.getSaturated().getSuperProperties(),
				props.keySet()))

			for (Queueable<?> carry : props.get(propRelation))
				engine.enqueue(target, carry);
	}

	public void apply(SuperClassExpression<C> argument, C context,
			RuleApplicationEngine engine) {
		List<IndexedObjectSomeValuesFrom> exists = argument.getExpression()
				.getNegExistentials();

		if (!context.derivePropagations || exists == null)
			return;

		for (IndexedObjectSomeValuesFrom e : exists)
			addPropagation(e.getRelation(),
					new NegativeSuperClassExpression<C>(e), context, engine);
	}

	private void addPropagation(IndexedPropertyChain propRelation,
			Queueable<C> carry, C context, RuleApplicationEngine engine) {

		if (context.propagationsByObjectProperty == null) {
			context.initPropagationsByProperty();
			// TODO initializeDerivationOfBackwardLinks();
		}

		if (context.propagationsByObjectProperty.add(propRelation, carry)) {

			// propagate over all backward links
			Multimap<IndexedPropertyChain, ContextElClassSaturation> backLinks = context.backwardLinksByObjectProperty;
			if (context.backwardLinksByObjectProperty == null)
				return; // this should never happen

			for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
					propRelation.getSaturated().getSubProperties(),
					backLinks.keySet()))

				for (Context target : backLinks.get(linkRelation))
					engine.enqueue(target, carry);
		}
	}

}
