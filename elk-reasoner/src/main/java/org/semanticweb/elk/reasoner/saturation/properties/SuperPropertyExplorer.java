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

class SuperPropertyExplorer implements IndexedPropertyChainVisitor<Void> {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(SuperPropertyExplorer.class);

	final private Set<IndexedPropertyChain> allSuperProperties_ = new ArrayHashSet<IndexedPropertyChain>();
	final private Queue<IndexedPropertyChain> toDoSuperProperties_ = new LinkedList<IndexedPropertyChain>();
	final private Set<IndexedPropertyChain> relevantSuperProperties_ = new ArrayHashSet<IndexedPropertyChain>();

	SuperPropertyExplorer(IndexedPropertyChain element) {
		toDo(element);
	}

	@Override
	public Void visit(IndexedObjectProperty element) {
		doCommon(element);
		for (IndexedBinaryPropertyChain chain : element.getLeftChains()) {
			SaturatedPropertyChain rightSaturation = chain.getRightProperty()
					.getSaturated();
			if (rightSaturation != null && rightSaturation.isDerivedReflexive())
				toDo(chain);
		}
		return null;
	}

	@Override
	public Void visit(IndexedBinaryPropertyChain element) {
		doCommon(element);
		return null;
	}

	public void doCommon(IndexedPropertyChain element) {
		for (IndexedPropertyChain sup : element.getToldSuperProperties())
			toDo(sup);
		for (IndexedBinaryPropertyChain chain : element.getRightChains()) {
			SaturatedPropertyChain leftSaturation = chain.getLeftProperty()
					.getSaturated();
			if (leftSaturation != null && leftSaturation.isDerivedReflexive())
				toDo(chain);
		}
	}

	private void toDo(IndexedPropertyChain element) {
		if (allSuperProperties_.add(element)) {
			toDoSuperProperties_.add(element);
			if (SaturatedPropertyChain.isRelevant(element))
				relevantSuperProperties_.add(element);
		}
	}

	private void doAll() {
		for (;;) {
			IndexedPropertyChain next = toDoSuperProperties_.poll();
			if (next == null)
				break;
			next.accept(this);
		}
	}

	private Set<IndexedPropertyChain> getRelevantSuperProperties() {
		doAll();
		return relevantSuperProperties_;
	}

	static Set<IndexedPropertyChain> getRelevantSuperProperties(
			IndexedPropertyChain property) {
		Set<IndexedPropertyChain> result = new SuperPropertyExplorer(property)
				.getRelevantSuperProperties();
		if (LOGGER_.isTraceEnabled())
			LOGGER_.trace(property + " relevant superproperties: " + result);
		return result;
	}

}
