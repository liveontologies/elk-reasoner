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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationShared;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Frantisek Simancik
 * 
 */
public class RuleExistentialPlus<C extends ContextElClassSaturation> extends
		RuleWithBackwardLinks<C> implements InferenceRuleSCE<C> {

	public void apply(BackwardLink<C> argument, C context,
			RuleApplicationShared engine) {
		final IndexedPropertyChain linkRelation = argument.getRelation();
		final Context target = argument.getTarget();

		// start deriving propagations
		if (!context.derivePropagations) {
			initializePropagations(context, engine);
			// the above already applies all propagations, can return
			return;
		}

		// apply all propagations over the link
		final Multimap<IndexedPropertyChain, Queueable<? extends ContextElClassSaturation>> props = context
				.getPropagationsByObjectProperty();
		if (props == null)
			return;

		for (IndexedPropertyChain propRelation : new LazySetIntersection<IndexedPropertyChain>(
				linkRelation.getSaturated().getSuperProperties(),
				props.keySet()))

			for (Queueable<?> carry : props.get(propRelation))
				engine.enqueue(target, carry);
	}

	@Override
	public void applySCE(SuperClassExpression<C> argument, C context,
			RuleApplicationShared engine) {
		final Collection<IndexedObjectSomeValuesFrom> exists = argument
				.getExpression().getNegExistentials();

		if (!context.derivePropagations || exists == null)
			return;

		for (IndexedObjectSomeValuesFrom e : exists)
			addPropagation(e.getRelation(),
					new NegativeSuperClassExpression<C>(e), context, engine);
	}

	private void initializePropagations(C context, RuleApplicationShared engine) {
		context.setDerivePropagations(true);

		for (IndexedClassExpression ice : context.superClassExpressions)
			if (ice.getNegExistentials() != null)
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					addPropagation(e.getRelation(),
							new NegativeSuperClassExpression<C>(e), context,
							engine);
	}

	private void addPropagation(IndexedPropertyChain propRelation,
			Queueable<C> carry, C context, RuleApplicationShared engine) {

		if (context.propagationsByObjectProperty == null) {
			context.initPropagationsByProperty();
			initializeCompositionOfBackwardLinks(context, engine);
		}

		if (context.propagationsByObjectProperty.add(propRelation, carry)) {

			// propagate over all backward links
			final Multimap<IndexedPropertyChain, ContextElClassSaturation> backLinks = context
					.getBackwardLinksByObjectProperty();

			if (backLinks == null) // this should never happen
				return;

			for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
					propRelation.getSaturated().getSubProperties(),
					backLinks.keySet()))

				for (Context target : backLinks.get(linkRelation))
					engine.enqueue(target, carry);
		}
	}

}
