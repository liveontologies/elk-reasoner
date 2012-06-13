/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.saturation.classes;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationFactory;
import org.semanticweb.elk.util.collections.Pair;

/**
 * @author Frantisek Simancik
 * 
 */
public class BackwardLink<C extends ContextElClassSaturation> extends
		Pair<IndexedPropertyChain, C> implements Queueable<C> {

	public BackwardLink(IndexedPropertyChain relation, C target) {
		super(relation, target);
	}

	public IndexedPropertyChain getRelation() {
		return first;
	}

	public C getTarget() {
		return second;
	}

	@Override
	public boolean storeInContext(C context, RuleApplicationFactory.Engine engine) {
		RuleStatistics statistics = engine.getStatistics();
		statistics.incrementBackLinkInfNo();

		if (context.backwardLinksByObjectProperty == null)
			context.initBackwardLinksByProperty();

		if (context.backwardLinksByObjectProperty.add(first, second)) {
			statistics.incrementBackLinNo();
			return true;
		}
		return false;
	}

}
