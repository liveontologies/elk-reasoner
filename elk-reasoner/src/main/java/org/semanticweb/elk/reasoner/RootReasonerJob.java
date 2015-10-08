package org.semanticweb.elk.reasoner;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;

/**
 * A {@link ReasonerOutput} that is computed from given root and sub-root
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <R>
 *            the type of the root input
 * @param <SR>
 *            the type of the sub-root input
 * @param <O>
 *            the type of the output
 */
public class RootReasonerJob<R extends IndexedContextRoot, SR extends IndexedObjectProperty, O>
		extends ReasonerOutput<O> {

	private final R root_;

	private final SR subRoot_;

	public RootReasonerJob(R root, SR subRoot) {
		this.root_ = root;
		this.subRoot_ = subRoot;
	}

	public final R getRoot() {
		return this.root_;
	}

	public final SR getSubRoot() {
		return this.subRoot_;
	}

	@Override
	public String toString() {
		return root_.toString() + (subRoot_ == null ? "" : subRoot_.toString());
	}
}
