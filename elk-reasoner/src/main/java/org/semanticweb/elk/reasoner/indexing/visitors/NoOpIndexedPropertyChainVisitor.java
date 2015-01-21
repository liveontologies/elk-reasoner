package org.semanticweb.elk.reasoner.indexing.visitors;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * An {@link IndexedPropertyChainVisitor} that always returns {@code null}.
 * 
 * @author "Yevgeny Kazakov"
 *
 * @param <O>
 */
public class NoOpIndexedPropertyChainVisitor<O> implements
		IndexedPropertyChainVisitor<O> {

	@SuppressWarnings("unused")
	protected O defaultVisit(IndexedPropertyChain element) {
		return null;
	}

	@Override
	public O visit(IndexedObjectProperty element) {
		return defaultVisit(element);
	}

	@Override
	public O visit(IndexedComplexPropertyChain element) {
		return defaultVisit(element);
	}

}
