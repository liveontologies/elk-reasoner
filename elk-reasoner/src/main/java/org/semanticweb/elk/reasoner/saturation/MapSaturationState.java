package org.semanticweb.elk.reasoner.saturation;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.OntologyIndex;

/**
 * A {@link SaturationState} backed by a map from {@link IndexedClassExpression}
 * s to {@link ExtendedContext}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class MapSaturationState<EC extends ExtendedContext> extends
		AbstractSaturationState<EC> {

	private final ConcurrentHashMap<IndexedContextRoot, EC> contextAssignment_;

	public MapSaturationState(OntologyIndex index, ContextFactory<EC> factory,
			int expectedSize) {
		super(index, factory);
		this.contextAssignment_ = new ConcurrentHashMap<IndexedContextRoot, EC>(
				expectedSize);
	}

	public MapSaturationState(OntologyIndex index, ContextFactory<EC> factory) {
		super(index, factory);
		this.contextAssignment_ = new ConcurrentHashMap<IndexedContextRoot, EC>(
				index.getClassExpressions().size());
	}

	@Override
	public Collection<EC> getContexts() {
		return contextAssignment_.values();
	}

	@Override
	public EC getContext(IndexedContextRoot root) {
		return contextAssignment_.get(root);
	}

	@Override
	void resetContexts() {
		contextAssignment_.clear();
	}

	@Override
	EC setIfAbsent(EC context) {
		return contextAssignment_.putIfAbsent(context.getRoot(), context);
	}

}
