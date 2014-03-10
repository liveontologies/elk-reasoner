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
public class IndexedObjectCache implements IndexedObjectFilter {

	// logger for this class
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(IndexedObjectCache.class);

	protected final KeyEntryHashSet<IndexedObject> indexedObjectLookup;
	protected int indexedClassCount = 0;
	protected int indexedObjectPropertyCount = 0;

	public IndexedObjectCache() {
		LOGGER_.trace("Creating new cache");
		indexedObjectLookup = new KeyEntryHashSet<IndexedObject>(
				new IndexedObjectViewFactory(), 1024);
	}

	public void clear() {
		LOGGER_.trace("Clear cache");

		indexedObjectLookup.clear();
		indexedClassCount = 0;
		indexedObjectPropertyCount = 0;
	}

	public boolean isEmpty() {
		return indexedObjectLookup.isEmpty();
	}

	@Override
	public IndexedClass visit(IndexedClass element) {
		return resolveCache(element);
	}

	@Override
	public IndexedObjectIntersectionOf visit(IndexedObjectIntersectionOf element) {
		return resolveCache(element);
	}

	@Override
	public IndexedObjectUnionOf visit(IndexedObjectUnionOf element) {
		return resolveCache(element);
	}

	@Override
	public IndexedObjectSomeValuesFrom visit(IndexedObjectSomeValuesFrom element) {
		return resolveCache(element);
	}

	@Override
	public IndexedObjectProperty visit(IndexedObjectProperty element) {
		return resolveCache(element);
	}

	@Override
	public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
		// caching not supported
		return axiom;
	}

	private <T extends IndexedObject> T resolveCache(T element) {
		@SuppressWarnings("unchecked")
		T cached = (T) indexedObjectLookup.get(element);
		return cached == null ? element : cached;
	}

	final IndexedObjectVisitor<Boolean> inserter = new IndexedObjectVisitor<Boolean>() {

		public Boolean commonVisit(IndexedObject element) {
			LOGGER_.trace("Adding {}", element);
			return indexedObjectLookup.add(element);
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
			if (indexedObjectLookup.add(element)) {
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

	};

	final IndexedObjectVisitor<Boolean> deletor = new IndexedObjectVisitor<Boolean>() {

		public Boolean commonVisit(IndexedObject element) {
			LOGGER_.trace("Removing {}", element);
			return (indexedObjectLookup.removeEntry(element) != null);
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
			if (indexedObjectLookup.add(element)) {
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

	};

	private class IndexedObjectViewFactory implements
			KeyEntryFactory<IndexedObject> {

		final IndexedEntryConverter<IndexedObject> converter = new IndexedEntryConverter<IndexedObject>();

		@Override
		public KeyEntry<IndexedObject, ? extends IndexedObject> createEntry(
				IndexedObject key) {
			return key.accept(converter);
		}

	}

}
