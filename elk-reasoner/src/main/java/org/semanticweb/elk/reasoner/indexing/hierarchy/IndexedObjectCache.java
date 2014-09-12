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
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;
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

	protected final KeyEntryHashSet<IndexedClassExpression> indexedClassExpressionLookup;
	protected final KeyEntryHashSet<IndexedPropertyChain> indexedPropertyChainLookup;
	protected final KeyEntryHashSet<IndexedAxiom> indexedAxiomLookup;
	protected int indexedClassCount = 0;
	protected int indexedIndividualCount = 0;
	protected int indexedObjectPropertyCount = 0;
	private final IndexedObjectFactory objectFactory_;

	public IndexedObjectCache(IndexedObjectFactory factory) {
		LOGGER_.trace("Creating new cache");

		indexedClassExpressionLookup = new KeyEntryHashSet<IndexedClassExpression>(
				new IndexedClassExpressionViewFactory(), 1024);
		indexedPropertyChainLookup = new KeyEntryHashSet<IndexedPropertyChain>(
				new IndexedPropertyChainViewFactory(), 128);
		indexedAxiomLookup = new KeyEntryHashSet<IndexedAxiom>(
				new IndexedAxiomViewFactory(), 1024);
		objectFactory_ = factory;
	}

	public IndexObjectConverter getIndexObjectConverter() {
		return new IndexObjectConverter(this, this, objectFactory_);
	}

	public void clear() {
		LOGGER_.trace("Clear cache");

		indexedClassExpressionLookup.clear();
		indexedPropertyChainLookup.clear();
		indexedAxiomLookup.clear();
		indexedClassCount = 0;
		indexedIndividualCount = 0;
		indexedObjectPropertyCount = 0;
	}

	public long size() {
		return indexedClassCount + indexedIndividualCount
				+ indexedObjectPropertyCount;
	}

	public boolean isEmpty() {
		return indexedClassExpressionLookup.isEmpty()
				&& indexedPropertyChainLookup.isEmpty()
				&& indexedAxiomLookup.isEmpty();
	}

	/**
	 * Remove all object of the given {@link IndexedObjectCache} from this
	 * {@link IndexedObjectCache}
	 * 
	 * @param other
	 *            the {@link IndexedObjectCache} whose stored object should be
	 *            removed from this {@link IndexedObjectCache}
	 */
	public void subtract(IndexedObjectCache other) {
		for (IndexedClassExpression ice : other.indexedClassExpressionLookup) {
			if (ice.getCompositionRuleHead() != null)
				throw new ElkUnexpectedIndexingException(
						"Deleting object with registered rules: " + ice);
			if (!ice.accept(deletor))
				throw new ElkUnexpectedIndexingException(
						"Cannot remove indexed object from the cache " + ice);
		}
		for (IndexedPropertyChain ipc : other.indexedPropertyChainLookup) {
			if (!ipc.accept(deletor))
				throw new ElkUnexpectedIndexingException(
						"Cannot remove indexed object from the cache " + ipc);
		}
		for (IndexedAxiom ax : other.indexedAxiomLookup)
			if (!ax.accept(deletor))
				throw new ElkUnexpectedIndexingException(
						"Cannot remove indexed object from the cache " + ax);
		// the counters should be subtracted during deletion
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
	public IndexedObjectComplementOf visit(IndexedObjectComplementOf element) {
		return resolveCache(
				(IndexedObjectComplementOf) indexedClassExpressionLookup
						.get(element),
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
	public IndexedObjectUnionOf visit(IndexedObjectUnionOf element) {
		return resolveCache(
				(IndexedObjectUnionOf) indexedClassExpressionLookup
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
	public IndexedSubObjectPropertyOfAxiom<?> visit(
			IndexedSubObjectPropertyOfAxiom<?> axiom) {
		// caching not supported
		return axiom;
	}
	
	@Override
	public IndexedReflexiveObjectPropertyAxiom<?> visit(
			IndexedReflexiveObjectPropertyAxiom<?> axiom) {
		// caching not supported
		return axiom;
	}

	@Override
	public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
		return resolveCache(
				(IndexedDisjointnessAxiom) indexedAxiomLookup.get(axiom), axiom);
	}

	private static <T> T resolveCache(T cached, T element) {
		return cached == null ? element : cached;
	}

	final IndexedObjectVisitor<Boolean> inserter = new IndexedObjectVisitor<Boolean>() {
		@Override
		public Boolean visit(IndexedClass element) {
			LOGGER_.trace("Adding {}", element);
			if (indexedClassExpressionLookup.add(element)) {
				indexedClassCount++;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			LOGGER_.trace("Adding {}", element);
			if (indexedClassExpressionLookup.add(element)) {
				indexedIndividualCount++;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedObjectComplementOf element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectUnionOf element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			LOGGER_.trace("Adding {}", element);
			return indexedClassExpressionLookup.add(element);
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			LOGGER_.trace("Adding {}", element);
			if (indexedPropertyChainLookup.add(element)) {
				indexedObjectPropertyCount++;
				SaturatedPropertyChain.getCreate(element);
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedBinaryPropertyChain element) {
			LOGGER_.trace("Adding {}", element);

			if (indexedPropertyChainLookup.add(element)) {
				SaturatedPropertyChain.getCreate(element);
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
			LOGGER_.trace("Adding {}", axiom);
			return indexedAxiomLookup.add(axiom);
		}

		@Override
		public Boolean visit(IndexedSubObjectPropertyOfAxiom<?> axiom) {
			// caching not supported
			return true;
		}
		
		@Override
		public Boolean visit(IndexedReflexiveObjectPropertyAxiom<?> axiom) {
			// caching not supported
			return true;
		}

	};

	final IndexedObjectVisitor<Boolean> deletor = new IndexedObjectVisitor<Boolean>() {

		@Override
		public Boolean visit(IndexedClass element) {
			LOGGER_.trace("Removing {}", element);

			if (indexedClassExpressionLookup.removeEntry(element) != null) {
				indexedClassCount--;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedIndividual element) {
			LOGGER_.trace("Removing {}", element);

			if (indexedClassExpressionLookup.removeEntry(element) != null) {
				indexedIndividualCount--;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedObjectComplementOf element) {
			LOGGER_.trace("Removing {}", element);
			return indexedClassExpressionLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedObjectIntersectionOf element) {
			LOGGER_.trace("Removing {}", element);
			return indexedClassExpressionLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedObjectSomeValuesFrom element) {
			LOGGER_.trace("Removing {}", element);
			return indexedClassExpressionLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedObjectUnionOf element) {
			LOGGER_.trace("Removing {}", element);
			return indexedClassExpressionLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedDataHasValue element) {
			LOGGER_.trace("Removing {}", element);
			return indexedClassExpressionLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedObjectProperty element) {
			LOGGER_.trace("Removing {}", element);

			if (indexedPropertyChainLookup.removeEntry(element) != null) {
				indexedObjectPropertyCount--;
				return true;
			}
			return false;
		}

		@Override
		public Boolean visit(IndexedBinaryPropertyChain element) {
			LOGGER_.trace("Removing {}", element);

			return indexedPropertyChainLookup.removeEntry(element) != null;
		}

		@Override
		public Boolean visit(IndexedSubClassOfAxiom axiom) {
			// caching not supported
			return true;
		}

		@Override
		public Boolean visit(IndexedDisjointnessAxiom axiom) {
			LOGGER_.trace("Removing {}", axiom);

			return indexedAxiomLookup.removeEntry(axiom) != null;
		}

		@Override
		public Boolean visit(IndexedSubObjectPropertyOfAxiom<?> axiom) {
			// caching not supported
			return true;
		}
		
		@Override
		public Boolean visit(IndexedReflexiveObjectPropertyAxiom<?> axiom) {
			// caching not supported
			return true;
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
