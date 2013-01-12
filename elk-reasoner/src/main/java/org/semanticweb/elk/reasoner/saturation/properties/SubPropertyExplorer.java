package org.semanticweb.elk.reasoner.saturation.properties;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedBinaryPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainVisitor;
import org.semanticweb.elk.util.collections.ArrayHashSet;

class SubPropertyExplorer implements IndexedPropertyChainVisitor<Void> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(SubPropertyExplorer.class);

	final private Set<IndexedPropertyChain> allSubProperties_ = new ArrayHashSet<IndexedPropertyChain>();
	final private Queue<IndexedPropertyChain> toDoSubProperties_ = new LinkedList<IndexedPropertyChain>();
	final private Set<IndexedPropertyChain> relevantSubProperties_ = new ArrayHashSet<IndexedPropertyChain>();

	SubPropertyExplorer(IndexedPropertyChain element) {
		toDo(element);
	}

	@Override
	public Void visit(IndexedObjectProperty element) {
		for (IndexedPropertyChain sub : element.getToldSubProperties())
			toDo(sub);
		return null;
	}

	@Override
	public Void visit(IndexedBinaryPropertyChain element) {
		IndexedPropertyChain left = element.getLeftProperty();
		IndexedPropertyChain right = element.getRightProperty();
		SaturatedPropertyChain leftSaturation = left.getSaturated();
		SaturatedPropertyChain rightSaturation = right.getSaturated();
		if (leftSaturation != null && leftSaturation.isDerivedReflexive())
			toDo(right);
		if (rightSaturation != null && rightSaturation.isDerivedReflexive())
			toDo(left);
		return null;
	}

	private void toDo(IndexedPropertyChain element) {
		if (allSubProperties_.add(element)) {
			toDoSubProperties_.add(element);
			if (SaturatedPropertyChain.isRelevant(element)) {
				relevantSubProperties_.add(element);
			}
		}
	}

	private void doAll() {
		for (;;) {
			IndexedPropertyChain next = toDoSubProperties_.poll();
			if (next == null)
				break;
			next.accept(this);
		}
	}

	private Set<IndexedPropertyChain> getRelevantSubProperties() {
		doAll();
		return relevantSubProperties_;
	}

	static Set<IndexedPropertyChain> getSetRelevantSubProperties(
			IndexedPropertyChain element) {
		return element.accept(SUB_PROPERTY_FINDER_);
	}

	static Set<IndexedPropertyChain> getSetRelevantSubProperties(
			IndexedObjectProperty element) {
		SaturatedPropertyChain saturation = SaturatedPropertyChain
				.getCreate(element);
		synchronized (saturation) {
			if (saturation.derivedSubProperties == null)
				saturation.derivedSubProperties = getRelevantSubProperties(element);
		}
		return saturation.derivedSubProperties;
	}

	static Set<IndexedPropertyChain> getRelevantSubProperties(
			IndexedPropertyChain property) {
		Set<IndexedPropertyChain> result = new SubPropertyExplorer(property)
				.getRelevantSubProperties();
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(property + " relevant subproperties: " + result);
		return result;
	}

	private final static IndexedPropertyChainVisitor<Set<IndexedPropertyChain>> SUB_PROPERTY_FINDER_ = new IndexedPropertyChainVisitor<Set<IndexedPropertyChain>>() {

		@Override
		public Set<IndexedPropertyChain> visit(IndexedObjectProperty element) {
			return getSetRelevantSubProperties(element);
		}

		@Override
		public Set<IndexedPropertyChain> visit(
				IndexedBinaryPropertyChain element) {
			return getRelevantSubProperties(element);
		}
	};

}
