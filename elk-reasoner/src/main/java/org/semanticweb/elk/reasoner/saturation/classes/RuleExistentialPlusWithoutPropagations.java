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
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * TODO: documentation
 * 
 * @author Frantisek Simancik
 * 
 * @param <C>
 *            the type of contexts that can be used with this inference rule
 */
public class RuleExistentialPlusWithoutPropagations<C extends ContextElClassSaturation>
		extends RuleWithBackwardLinks<C> implements InferenceRuleSCE<C> {

	public void apply(BackwardLink<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		final IndexedPropertyChain linkRelation = argument.getRelation();
		final Context target = argument.getTarget();

		for (IndexedClassExpression ice : context.superClassExpressions)
			if (ice.getNegExistentials() != null)
				for (IndexedObjectSomeValuesFrom e : ice.getNegExistentials())
					if (linkRelation.getSaturated().getSuperProperties()
							.contains(e.getRelation())) {
						Queueable<C> carry = InferenceSystemElClassSaturation.OPTIMIZE_DECOMPOSITIONS ? new NegativeSuperClassExpression<C>(
								e) : new PositiveSuperClassExpression<C>(e);
						engine.enqueue(target, carry);
					}
	}

	@Override
	public void applySCE(SuperClassExpression<C> argument, C context,
			RuleApplicationFactory.Engine engine) {
		final Collection<IndexedObjectSomeValuesFrom> exists = argument
				.getExpression().getNegExistentials();

		if (exists == null)
			return;

		initializeCompositionOfBackwardLinks(context, engine);

		final Multimap<IndexedPropertyChain, ContextElClassSaturation> backLinks = context
				.getBackwardLinksByObjectProperty();

		if (backLinks == null)
			return;

		for (IndexedObjectSomeValuesFrom e : exists) {
			Queueable<C> carry = InferenceSystemElClassSaturation.OPTIMIZE_DECOMPOSITIONS ? new NegativeSuperClassExpression<C>(
					e) : new PositiveSuperClassExpression<C>(e);

			for (IndexedPropertyChain linkRelation : new LazySetIntersection<IndexedPropertyChain>(
					e.getRelation().getSaturated().getSubProperties(),
					backLinks.keySet()))

				for (Context target : backLinks.get(linkRelation))
					engine.enqueue(target, carry);
		}

	}

}
