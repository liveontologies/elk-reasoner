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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceRule;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.collections.Multimap;

/**
 * @author Frantisek Simancik
 *
 */
public class RuleRoleComposition<C extends ContextElClassSaturation>  extends RuleWithBackwardLinks<C> 
		implements InferenceRule<C> {

	public void apply(BackwardLink<C> argument, C context,
			RuleApplicationEngine engine) {

		final IndexedPropertyChain linkRelation = argument.getRelation();
		final C target = argument.getTarget();

		/*
		 * if composeBackwardLinks, then add a forward copy of the link
		 * to consider the link in property compositions
		 */
		if (context.composeBackwardLinks
				&& linkRelation.getSaturated().getCompositionsByLeftSubProperty() != null)
			engine.enqueue(target, new ForwardLink<C>(linkRelation, context));

		/* compose the link with all forward links */
		final Multimap<IndexedPropertyChain, IndexedBinaryPropertyChain> comps =
			linkRelation.getSaturated().getCompositionsByRightSubProperty();
		final Multimap<IndexedPropertyChain, ? extends ContextElClassSaturation> forwLinks =
			context.forwardLinksByObjectProperty;
		
		if (comps != null && forwLinks != null) {
			for (IndexedPropertyChain forwardRelation : new LazySetIntersection<IndexedPropertyChain>(
					comps.keySet(), forwLinks.keySet())) {

				Collection<IndexedBinaryPropertyChain> compositions = comps.get(forwardRelation);
				Collection<? extends ContextElClassSaturation> forwardTargets = forwLinks.get(forwardRelation);

				for (IndexedPropertyChain composition : compositions)
					for (ContextElClassSaturation forwardTarget : forwardTargets)
						engine.enqueue(forwardTarget, new BackwardLink<C>(composition, target));
			}
		}


	}

	public void apply(ForwardLink<C> argument, C context,
			RuleApplicationEngine engine) {

		// start deriving backward links for composition
		initializeCompositionOfBackwardLinks(context, engine);
		
		final IndexedPropertyChain linkRelation = argument.getRelation();
		final C target = argument.getTarget();

		/* compose the link with all backward links */
		final Multimap<IndexedPropertyChain, IndexedBinaryPropertyChain> comps =
			linkRelation.getSaturated().getCompositionsByLeftSubProperty();
		final Multimap<IndexedPropertyChain, ? extends ContextElClassSaturation> backLinks =
			context.backwardLinksByObjectProperty;
		
		//		assert comps != null
		if (backLinks != null) {
			for (IndexedPropertyChain backwardRelation : new LazySetIntersection<IndexedPropertyChain>(
					comps.keySet(), backLinks.keySet())) {

				Collection<IndexedBinaryPropertyChain> compositions = comps.get(backwardRelation);
				Collection<? extends ContextElClassSaturation> backwardTargets = backLinks.get(backwardRelation);

				for (IndexedPropertyChain composition : compositions)
					for (ContextElClassSaturation backwardTarget : backwardTargets)
						engine.enqueue(target, new BackwardLink<ContextElClassSaturation> (composition, backwardTarget));
			}
		}
	}
	
	
}
