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

/**
 * A cache of all indexed objects in the ontology backed by a KeyEntryHashSet.
 * It uses indexed Entries to compare object with respect to structural
 * equality. Supports (non-recursive) addition, removal, and retrieval of single
 * indexed objects. The recursion for indexing subobjects is in the 
 * ElkObjectIndexerVisitor.
 * 
 * @author Frantisek Simancik
 * 
 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectFilter#filter
	 * (org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression)
	 * 
	 * Returns the indexed canonical representative of the argument.
	 */
	@Override
	public IndexedClassExpression filter(IndexedClassExpression ice) {
		IndexedClassExpression result = indexedClassExpressionLookup.get(ice);
		if (result == null)
			return ice;
		else
			return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectFilter#filter
	 * (org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain)
	 * 
	 * Returns the indexed canonical representative of the argument.
	 */
	@Override
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

	/**
	 * The factory used in indexedClassExpressionLookup for wrapping indexed
	 * class expressions in the corresponding entries to use structural
	 * equality.
	 */
	class IndexedClassExpressionViewFactory implements
			KeyEntryFactory<IndexedClassExpression> {

		final IndexedEntryConverter<IndexedClassExpression> converter = new IndexedEntryConverter<IndexedClassExpression>();

		@Override
		public KeyEntry<IndexedClassExpression, ? extends IndexedClassExpression> createEntry(
				IndexedClassExpression key) {
			return key.accept(converter);
		}

	}

	IndexedClassExpressionViewFactory indexedClassExpressionViewFactory = new IndexedClassExpressionViewFactory();

	/**
	 * The factory used in indexedPropertyChainLookup for wrapping indexed class
	 * expressions in the corresponding entries to use structural equality.
	 */
	class IndexedPropertyChainViewFactory implements
			KeyEntryFactory<IndexedPropertyChain> {

		final IndexedEntryConverter<IndexedPropertyChain> converter = new IndexedEntryConverter<IndexedPropertyChain>();

		@Override
		public KeyEntry<IndexedPropertyChain, ? extends IndexedPropertyChain> createEntry(
				IndexedPropertyChain key) {
			return key.accept(converter);
		}

	}

	IndexedPropertyChainViewFactory indexedPropertyChainViewFactory = new IndexedPropertyChainViewFactory();

}
