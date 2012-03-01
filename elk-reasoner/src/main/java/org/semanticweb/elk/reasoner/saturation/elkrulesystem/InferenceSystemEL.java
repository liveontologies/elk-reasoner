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

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.rulesystem.InferenceSystem;
import org.semanticweb.elk.reasoner.saturation.rulesystem.RuleApplicationEngine;

public final class InferenceSystemEL extends InferenceSystem<ContextEl> {
	
	public static AtomicInteger contextNo = new AtomicInteger(0);

	public InferenceSystemEL() {
		add(new RuleSubsumption<ContextEl>());
		add(new RuleConjunctionPlus<ContextEl>());
		add(new RuleExistentialPlus<ContextEl>());
		add(new RuleDecomposition<ContextEl>());
	}

	public final boolean createAndInitializeContext(IndexedClassExpression root, RuleApplicationEngine engine) {
		ContextEl sce = new ContextEl(root);
		if (root.setContext(sce)) {
//			if (LOGGER_.isTraceEnabled()) {
//				LOGGER_.trace(root + ": context created");
//			}
			contextNo.incrementAndGet();
			engine.enqueue(sce, new PositiveSuperClassExpression<ContextEl>(root));

			if (engine.owlThing.occursNegatively())
				engine.enqueue(sce, new PositiveSuperClassExpression<ContextEl>(engine.owlThing));
			
			return true;
		}
		return false;
	}
	
}
