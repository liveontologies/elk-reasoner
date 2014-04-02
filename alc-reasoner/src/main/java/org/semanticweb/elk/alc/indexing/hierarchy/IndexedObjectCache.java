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
package org.semanticweb.elk.alc.indexing.hierarchy;

import org.semanticweb.elk.alc.indexing.entries.IndexedEntryConverter;
import org.semanticweb.elk.alc.indexing.visitors.IndexedAxiomFilter;
import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectFilter;
import org.semanticweb.elk.alc.indexing.visitors.IndexedObjectVisitor;
import org.semanticweb.elk.util.collections.entryset.KeyEntry;
import org.semanticweb.elk.util.collections.entryset.KeyEntryFactory;
import org.semanticweb.elk.util.collections.entryset.KeyEntryHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A cache of {@link IndexedObject}s in the ontology backed by a
 * {@link KeyEntryHashSet}. It uses indexed {@link KeyEntry}s to compare object
 * with respect to structural equality. Supports (non-recursive) addition,
 * removal, and retrieval of single indexed objects. The recursion for indexing
 * subobjects is in the {@link IndexObjectConverter}. Not all
 * {@link IndexedObject}s are cached but only those whose uniqueness modulo
 * structural equivalence is important for index updating to work correctly.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class IndexedObjectCache implements IndexedObjectFilter, IndexedAxiomFilter {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectCache.class);

	protected final KeyEntryHashSet<IndexedClassExpression> indexedClassExpressionLookup;
	protected final KeyEntryHashSet<IndexedObjectProperty> indexedPropertyLookup;
	protected final KeyEntryHashSet<IndexedAxiom> indexedAxiomLookup;
	protected int indexedAxiomCount = 0;
	protected int indexedClassCount = 0;
	protected int indexedObjectPropertyCount = 0;

	public IndexedObjectCache() {
		LOGGER_.trace("Creating new cache");
		indexedClassExpressionLookup = new KeyEntryHashSet<IndexedClassExpression>(
				new IndexedClassExpressionViewFactory(), 1024);
		indexedPropertyLookup = new KeyEntryHashSet<IndexedObjectProperty>(
				new IndexedObjectPropertyViewFactory(), 32);
		indexedAxiomLookup = new KeyEntryHashSet<IndexedAxiom>(
				new IndexedAxiomViewFactory(), 128);
	}

	public void clear() {
		LOGGER_.trace("Clear cache");

		indexedClassExpressionLookup.clear();
		indexedPropertyLookup.clear();
		indexedAxiomLookup.clear();
		indexedClassCount = 0;
		indexedObjectPropertyCount = 0;
		indexedAxiomCount = 0;
	}

	public boolean isEmpty() {
		return indexedClassExpressionLookup.isEmpty() && indexedPropertyLookup.isEmpty() && indexedAxiomLookup.isEmpty();
	}

	@Override
	public IndexedClass visit(IndexedClass element) {
		return (IndexedClass) resolveCache(indexedClassExpressionLookup, element);
	}

	@Override
	public IndexedObjectIntersectionOf visit(IndexedObjectIntersectionOf element) {
		return (IndexedObjectIntersectionOf) resolveCache(indexedClassExpressionLookup, element);
	}

	@Override
	public IndexedObjectUnionOf visit(IndexedObjectUnionOf element) {
		return (IndexedObjectUnionOf) resolveCache(indexedClassExpressionLookup, element);
	}

	@Override
	public IndexedObjectSomeValuesFrom visit(IndexedObjectSomeValuesFrom element) {
		return (IndexedObjectSomeValuesFrom) resolveCache(indexedClassExpressionLookup, element);
	}

	@Override
	public IndexedObjectProperty visit(IndexedObjectProperty element) {
		return resolveCache(indexedPropertyLookup, element);
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
		// caching not supported
		return axiom;
	}
	
	@Override
	public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
		return (IndexedDisjointnessAxiom) resolveCache(indexedAxiomLookup, axiom);
	}

	private <T extends IndexedObject> T resolveCache(KeyEntryHashSet<T> cache, T element) {
		T cached = cache.get(element);
		
		return cached == null ? element : cached;
	}
	
	final IndexedObjectVisitor<Boolean> inserter = new IndexedObjectVisitor<Boolean>() {

		public Boolean commonVisit(IndexedClassExpression element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}
		
		public Boolean commonVisit(IndexedObjectProperty element) {
			LOGGER_.trace("Adding {}", element);
			return indexedPropertyLookup.add(element);
		}

		public Boolean commonVisit(IndexedAxiom axiom) {
			LOGGER_.trace("Adding {}", axiom);
			return indexedAxiomLookup.add(axiom);
		}

		@Override
		public Boolean visit(IndexedClass element) {
			if (commonVisit(element)) {
				indexedClassCount++;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectUnionOf element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			if (commonVisit(element)) {
				indexedObjectPropertyCount++;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedSubClassOfAxiom axiom) {
			// caching not supported
			return true;
		}

		@Override
		public Boolean visit(IndexedDisjointnessAxiom axiom) {
			if (commonVisit(axiom)) {
				indexedAxiomCount++;
				return true;
			}
			// else
			return false;
		}

	};

	final IndexedObjectVisitor<Boolean> deletor = new IndexedObjectVisitor<Boolean>() {

		public Boolean commonVisit(IndexedClassExpression element) {
			LOGGER_.trace("Removing {}", element);
			return (indexedClassExpressionLookup.removeEntry(element) != null);
		}
		
		public Boolean commonVisit(IndexedObjectProperty element) {
			LOGGER_.trace("Removing {}", element);
			return (indexedPropertyLookup.removeEntry(element) != null);
		}

		public Boolean commonVisit(IndexedAxiom element) {
			LOGGER_.trace("Removing {}", element);
			return (indexedAxiomLookup.removeEntry(element) != null);
		}

		@Override
		public Boolean visit(IndexedClass element) {
			if (commonVisit(element)) {
				indexedClassCount--;
				return true;
			}
			// else
			return false;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectUnionOf element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			return commonVisit(element);
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			if (commonVisit(element)) {
				indexedObjectPropertyCount--;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedSubClassOfAxiom axiom) {
			// caching not supported
			return true;
		}

		@Override
		public Boolean visit(IndexedDisjointnessAxiom axiom) {
			if (commonVisit(axiom)) {
				indexedAxiomCount--;
				return true;
			}
			// else
			return false;
		}

	};

	private class IndexedClassExpressionViewFactory implements
			KeyEntryFactory<IndexedClassExpression> {

		final IndexedEntryConverter<IndexedClassExpression> converter = new IndexedEntryConverter<IndexedClassExpression>();

		@Override
		public KeyEntry<IndexedClassExpression, ? extends IndexedClassExpression> createEntry(
				IndexedClassExpression key) {
			return key.accept(converter);
		}

	}

	private class IndexedObjectPropertyViewFactory implements
			KeyEntryFactory<IndexedObjectProperty> {

		final IndexedEntryConverter<IndexedObjectProperty> converter = new IndexedEntryConverter<IndexedObjectProperty>();

		@Override
		public KeyEntry<IndexedObjectProperty, ? extends IndexedObjectProperty> createEntry(
				IndexedObjectProperty key) {
			return key.accept(converter);
		}

	}

	private class IndexedAxiomViewFactory implements
			KeyEntryFactory<IndexedAxiom> {

		final IndexedEntryConverter<IndexedAxiom> converter = new IndexedEntryConverter<IndexedAxiom>();

		@Override
		public KeyEntry<IndexedAxiom, ? extends IndexedAxiom> createEntry(
				IndexedAxiom key) {
			return key.accept(converter);
		}

	}

}
