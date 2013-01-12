package org.semanticweb.elk.reasoner.saturation.rules;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.logging.CachedTimeThread;

public class DecompositionRuleApplicationTimerVisitor implements
		DecompositionRuleApplicationVisitor {

	/**
	 * the visitor whose methods to be timed
	 */
	private final DecompositionRuleApplicationVisitor visitor_;

	/**
	 * timer used to time the visitor
	 */
	private final DecompositionRuleApplicationTimer timer_;

	/**
	 * Creates a new {@link DecompositionRuleApplicationVisitor} that executes
	 * the corresponding methods of the given
	 * {@link DecompositionRuleApplicationVisitor} and measures the time spent
	 * within the corresponding methods using the given
	 * {@link DecompositionRuleApplicationTimer}.
	 * 
	 * @param visitor
	 *            the {@link DecompositionRuleApplicationVisitor} used to
	 *            execute the methods
	 * @param timer
	 *            the {@link DecompositionRuleApplicationTimer} used to mesure
	 *            the time spent within the methods
	 */
	public DecompositionRuleApplicationTimerVisitor(
			DecompositionRuleApplicationVisitor visitor,
			DecompositionRuleApplicationTimer timer) {
		this.timer_ = timer;
		this.visitor_ = visitor;
	}

	@Override
	public void visit(IndexedClass ice, Context context) {
		timer_.timeIndexedClass -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, context);
		timer_.timeIndexedClass += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedObjectIntersectionOf ice,
			Context context) {
		timer_.timeIndexedObjectIntersectionOf -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, context);
		timer_.timeIndexedObjectIntersectionOf += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedObjectSomeValuesFrom ice,
			Context context) {
		timer_.timeIndexedObjectSomeValuesFrom -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, context);
		timer_.timeIndexedObjectSomeValuesFrom += CachedTimeThread.currentTimeMillis;
	}

	@Override
	public void visit(IndexedDataHasValue ice, Context context) {
		timer_.timeIndexedDataHasValue -= CachedTimeThread.currentTimeMillis;
		visitor_.visit(ice, context);
		timer_.timeIndexedDataHasValue += CachedTimeThread.currentTimeMillis;
	}

}
