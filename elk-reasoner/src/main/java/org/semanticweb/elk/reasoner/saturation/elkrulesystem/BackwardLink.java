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
package org.semanticweb.elk.reasoner.saturation.elkrulesystem;

import java.util.concurrent.atomic.AtomicInteger;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Context;
import org.semanticweb.elk.reasoner.saturation.rulesystem.Queueable;
import org.semanticweb.elk.util.collections.Pair;

/**
 * @author Frantisek Simancik
 * 
 */
public class BackwardLink<C extends ContextEl> extends
		Pair<IndexedPropertyChain, C> implements Queueable<C> {
	
	public static AtomicInteger backLinkNo = new AtomicInteger(0);
	public static AtomicInteger backLinkInfNo = new AtomicInteger(0);

	public BackwardLink(IndexedPropertyChain relation, C target) {
		super(relation, target);
	}

	public IndexedPropertyChain getRelation() {
		return first;
	}

	public Context getTarget() {
		return second;
	}

	public boolean storeInContext(C context) {
		backLinkInfNo.incrementAndGet();

		if (context.backwardLinksByObjectProperty == null)
			context.initBackwardLinksByProperty();

		if (context.backwardLinksByObjectProperty.add(first, second)) {
			backLinkNo.incrementAndGet();
			return true;
		}
		return false;
	}

}
