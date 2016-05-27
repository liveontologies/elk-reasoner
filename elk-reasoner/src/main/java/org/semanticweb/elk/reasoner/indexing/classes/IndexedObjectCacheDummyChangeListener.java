package org.semanticweb.elk.reasoner.indexing.classes;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;

/**
 * An {@link IndexedObjectCache.ChangeListener} that does nothing; can be used
 * as a prototype to implement other listeners
 * 
 * @author Yevgeny Kazakov
 *
 */
public class IndexedObjectCacheDummyChangeListener
		implements IndexedObjectCache.ChangeListener {

	@Override
	public void classAddition(IndexedClass cls) {
		// does nothing by default
	}

	@Override
	public void classRemoval(IndexedClass cls) {
		// does nothing by default
	}

	@Override
	public void individualAddition(IndexedIndividual ind) {
		// does nothing by default
	}

	@Override
	public void individualRemoval(IndexedIndividual ind) {
		// does nothing by default
	}

	@Override
	public void objectPropertyAddition(IndexedObjectProperty prop) {
		// does nothing by default
	}

	@Override
	public void objectPropertyRemoval(IndexedObjectProperty prop) {
		// does nothing by default
	}

	@Override
	public void classExpressionAddition(IndexedClassExpression expr) {
		// does nothing by default
	}

	@Override
	public void classExpressionRemoval(IndexedClassExpression expr) {
		// does nothing by default
	}

	@Override
	public void propertyChainAddition(IndexedPropertyChain chain) {
		// does nothing by default
	}

	@Override
	public void propertyChainRemoval(IndexedPropertyChain chain) {
		// does nothing by default
	}

}
