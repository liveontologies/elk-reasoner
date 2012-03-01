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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import org.semanticweb.elk.reasoner.indexing.entries.IndexedEntryConverter;
import org.semanticweb.elk.util.collections.entryset.KeyEntryFactory;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;

public class IndexedObjectCache implements IndexedObjectFilter {

	protected final KeyEntryHashSet<IndexedClassExpression> indexedClassExpressionLookup;
	protected final KeyEntryHashSet<IndexedPropertyChain> indexedPropertyChainLookup;
	protected int indexedClassCount = 0;
	protected int indexedObjectPropertyCount = 0;

	protected IndexedObjectCache() {
		indexedClassExpressionLookup = new KeyEntryHashSet<IndexedClassExpression>(
				indexedClassExpressionViewFactory, 1024);
		indexedPropertyChainLookup = new KeyEntryHashSet<IndexedPropertyChain>(
				indexedPropertyChainViewFactory, 128);
	}

	public IndexedClassExpression filter(IndexedClassExpression ice) {
		IndexedClassExpression result = indexedClassExpressionLookup.get(ice);
		if (result == null)
			return ice;
		else
			return result;
	}

	public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
		IndexedPropertyChain result = indexedPropertyChainLookup.get(ipc);
		if (result == null)
			return ipc;
		else
			return result;
	}

	protected void add(IndexedClassExpression ice) {
		indexedClassExpressionLookup.merge(ice);
		if (ice instanceof IndexedClass)
			indexedClassCount++;
	}

	protected void add(IndexedPropertyChain ipc) {
		indexedPropertyChainLookup.merge(ipc);
		if (ipc instanceof IndexedObjectProperty)
			indexedObjectPropertyCount++;
	}

	protected void remove(IndexedClassExpression ice) {
		indexedClassExpressionLookup.remove(ice);
		if (ice instanceof IndexedClass)
			indexedClassCount--;
	}

	protected void remove(IndexedPropertyChain ipc) {
		indexedPropertyChainLookup.remove(ipc);
		if (ipc instanceof IndexedObjectProperty)
			indexedObjectPropertyCount--;
	}

	class IndexedClassExpressionViewFactory implements
			KeyEntryFactory<IndexedClassExpression> {

		final IndexedEntryConverter<IndexedClassExpression> converter = new IndexedEntryConverter<IndexedClassExpression>();

		public KeyEntry<IndexedClassExpression, ? extends IndexedClassExpression> createEntry(
				IndexedClassExpression key) {
			return key.accept(converter);
		}

	}

	IndexedClassExpressionViewFactory indexedClassExpressionViewFactory = new IndexedClassExpressionViewFactory();

	class IndexedPropertyChainViewFactory implements
			KeyEntryFactory<IndexedPropertyChain> {

		final IndexedEntryConverter<IndexedPropertyChain> converter = new IndexedEntryConverter<IndexedPropertyChain>();

		public KeyEntry<IndexedPropertyChain, ? extends IndexedPropertyChain> createEntry(
				IndexedPropertyChain key) {
			return key.accept(converter);
		}

	}

	IndexedPropertyChainViewFactory indexedPropertyChainViewFactory = new IndexedPropertyChainViewFactory();

}
