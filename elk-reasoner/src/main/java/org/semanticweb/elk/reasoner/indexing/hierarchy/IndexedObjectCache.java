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
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;
import org.semanticweb.elk.util.collections.entryset.KeyEntryFactory;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;

/**
 * A cache of all indexed objects in the ontology backed by a
 * {@link KeyEntryHashSet}. It uses indexed {@link KeyEntry}s to compare object
 * with respect to structural equality. Supports (non-recursive) addition,
 * removal, and retrieval of single indexed objects. The recursion for indexing
 * subobjects is in the {@link IndexObjectConverter}.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @seenElkObjectIndexerVisitor
 * 
 */
public class IndexedObjectCache implements IndexedObjectFilter {

	protected final KeyEntryHashSet<IndexedClassExpression> indexedClassExpressionLookup;
	protected final KeyEntryHashSet<IndexedPropertyChain> indexedPropertyChainLookup;
	protected final KeyEntryHashSet<IndexedAxiom> indexedAxiomLookup;
	protected int indexedClassCount = 0;
	protected int indexedIndividualCount = 0;
	protected int indexedObjectPropertyCount = 0;

	protected IndexedObjectCache() {
		indexedClassExpressionLookup = new KeyEntryHashSet<IndexedClassExpression>(
				new IndexedClassExpressionViewFactory(), 1024);
		indexedPropertyChainLookup = new KeyEntryHashSet<IndexedPropertyChain>(
				new IndexedPropertyChainViewFactory(), 128);
		indexedAxiomLookup = new KeyEntryHashSet<IndexedAxiom>(
				new IndexedAxiomViewFactory(), 1024);
	}

	protected void clear() {
		indexedClassExpressionLookup.clear();
		indexedPropertyChainLookup.clear();
		indexedClassCount = 0;
		indexedIndividualCount = 0;
		indexedObjectPropertyCount = 0;
	}

	private static <T> T resolveCache(T cached, T element) {
		return cached == null ? element : cached;
	}

	@Override
	public IndexedClass visit(IndexedClass element) {
		return resolveCache(
				(IndexedClass) indexedClassExpressionLookup.get(element),
				element);
	}

	@Override
	public IndexedIndividual visit(IndexedIndividual element) {
		return resolveCache(
				(IndexedIndividual) indexedClassExpressionLookup.get(element),
				element);
	}

	@Override
	public IndexedObjectIntersectionOf visit(IndexedObjectIntersectionOf element) {
		return resolveCache(
				(IndexedObjectIntersectionOf) indexedClassExpressionLookup
						.get(element),
				element);
	}

	@Override
	public IndexedObjectSomeValuesFrom visit(IndexedObjectSomeValuesFrom element) {
		return resolveCache(
				(IndexedObjectSomeValuesFrom) indexedClassExpressionLookup
						.get(element),
				element);
	}

	@Override
	public IndexedDataHasValue visit(IndexedDataHasValue element) {
		return resolveCache(
				(IndexedDataHasValue) indexedClassExpressionLookup.get(element),
				element);
	}

	@Override
	public IndexedObjectProperty visit(IndexedObjectProperty element) {
		return resolveCache(
				(IndexedObjectProperty) indexedPropertyChainLookup.get(element),
				element);
	}

	@Override
	public IndexedBinaryPropertyChain visit(IndexedBinaryPropertyChain element) {
		return resolveCache(
				(IndexedBinaryPropertyChain) indexedPropertyChainLookup
						.get(element),
				element);
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
		// caching not supported
		return axiom;
	}

	@Override
	public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
		return resolveCache(
				(IndexedDisjointnessAxiom) indexedAxiomLookup.get(axiom), axiom);
	}

	protected final IndexedObjectVisitor<Boolean> inserter = new IndexedObjectVisitor<Boolean>() {
		@Override
		public Boolean visit(IndexedClass element) {
			if (indexedClassExpressionLookup.add(element)) {
				indexedClassCount++;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			if (indexedClassExpressionLookup.add(element)) {
				indexedIndividualCount++;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			if (indexedPropertyChainLookup.add(element)) {
				indexedObjectPropertyCount++;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedBinaryPropertyChain element) {
			return indexedPropertyChainLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedSubClassOfAxiom axiom) {
			// caching not supported
			return false;
		}

		@Override
		public Boolean visit(IndexedDisjointnessAxiom axiom) {
			return indexedAxiomLookup.add(axiom);
		}
	};

	protected final IndexedObjectVisitor<Boolean> deletor = new IndexedObjectVisitor<Boolean>() {
		@Override
		public Boolean visit(IndexedClass element) {
			if (indexedClassExpressionLookup.remove(element)) {
				indexedClassCount--;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			if (indexedClassExpressionLookup.remove(element)) {
				indexedIndividualCount--;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			return indexedClassExpressionLookup.remove(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			return indexedClassExpressionLookup.remove(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			return indexedClassExpressionLookup.remove(element);
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			if (indexedPropertyChainLookup.remove(element)) {
				indexedObjectPropertyCount--;
				return true;
			} else
				return false;
		}

		@Override
		public Boolean visit(IndexedBinaryPropertyChain element) {
			return indexedPropertyChainLookup.remove(element);
		}

		@Override
		public Boolean visit(IndexedSubClassOfAxiom axiom) {
			// caching not supported
			return false;
		}

		@Override
		public Boolean visit(IndexedDisjointnessAxiom axiom) {
			return indexedAxiomLookup.remove(axiom);
		}
	};

	private class IndexedAxiomViewFactory implements
			KeyEntryFactory<IndexedAxiom> {

		final IndexedEntryConverter<IndexedAxiom> converter = new IndexedEntryConverter<IndexedAxiom>();

		@Override
		public KeyEntry<IndexedAxiom, ? extends IndexedAxiom> createEntry(
				IndexedAxiom key) {
			return key.accept(converter);
		}

	}

	private class IndexedClassExpressionViewFactory implements
			KeyEntryFactory<IndexedClassExpression> {

		final IndexedEntryConverter<IndexedClassExpression> converter = new IndexedEntryConverter<IndexedClassExpression>();

		@Override
		public KeyEntry<IndexedClassExpression, ? extends IndexedClassExpression> createEntry(
				IndexedClassExpression key) {
			return key.accept(converter);
		}

	}

	private class IndexedPropertyChainViewFactory implements
			KeyEntryFactory<IndexedPropertyChain> {

		final IndexedEntryConverter<IndexedPropertyChain> converter = new IndexedEntryConverter<IndexedPropertyChain>();

		@Override
		public KeyEntry<IndexedPropertyChain, ? extends IndexedPropertyChain> createEntry(
				IndexedPropertyChain key) {
			return key.accept(converter);
		}

	}

}
