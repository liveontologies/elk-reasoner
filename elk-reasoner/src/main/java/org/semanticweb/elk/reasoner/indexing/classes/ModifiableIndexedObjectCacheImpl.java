package org.semanticweb.elk.reasoner.indexing.classes;

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

import org.semanticweb.elk.owl.predefined.PredefinedElkClassFactory;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObject;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlNothing;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlThing;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.collections.entryset.EntryCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link ModifiableIndexedObjectCache}
 * 
 * @author "Yevgeny Kazakov"
 */
class ModifiableIndexedObjectCacheImpl implements ModifiableIndexedObjectCache {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ModifiableIndexedObjectCacheImpl.class);

	private final EntryCollection<CachedIndexedComplexClassExpression<?>> cachedComplexClassExpressions_;

	private final EntryCollection<CachedIndexedComplexPropertyChain> cachedBinaryPropertyChains_;

	private final EntryCollection<CachedIndexedClassExpressionList> cachedClassExpressionLists_;

	private final EntryCollection<CachedIndexedClass> cachedClasses_;

	private final EntryCollection<CachedIndexedObjectProperty> cachedObjectProperties_;

	private final EntryCollection<CachedIndexedIndividual> cachedIndividuals_;

	private final CachedIndexedObject.Filter resolver_, inserter_, deleter_;

	private final CachedIndexedOwlThing owlThing_;

	private final CachedIndexedOwlNothing owlNothing_;

	public ModifiableIndexedObjectCacheImpl(
			PredefinedElkClassFactory elkFactory, int initialSize) {
		this.cachedComplexClassExpressions_ = new EntryCollection<CachedIndexedComplexClassExpression<?>>(
				initialSize);
		this.cachedBinaryPropertyChains_ = new EntryCollection<CachedIndexedComplexPropertyChain>(
				initialSize);
		this.cachedClassExpressionLists_ = new EntryCollection<CachedIndexedClassExpressionList>(
				initialSize);
		this.cachedClasses_ = new EntryCollection<CachedIndexedClass>(
				initialSize);
		this.cachedObjectProperties_ = new EntryCollection<CachedIndexedObjectProperty>(
				initialSize);
		this.cachedIndividuals_ = new EntryCollection<CachedIndexedIndividual>(
				initialSize);
		this.resolver_ = new Resolver_();
		this.inserter_ = new Inserter_();
		this.deleter_ = new Deleter_();
		// owl:Thing and owl:Nothing always occur in the cache
		this.owlThing_ = new CachedIndexedOwlThingImpl(
				elkFactory.getOwlThing());
		this.owlNothing_ = new CachedIndexedOwlNothingImpl(
				elkFactory.getOwlNothing());
		add(owlThing_);
		add(owlNothing_);
	}

	public ModifiableIndexedObjectCacheImpl(
			PredefinedElkClassFactory elkFactory) {
		this(elkFactory, 1024);
	}

	@Override
	public Collection<? extends IndexedClass> getClasses() {
		return cachedClasses_;
	}

	@Override
	public Collection<? extends IndexedIndividual> getIndividuals() {
		return cachedIndividuals_;
	}

	@Override
	public Collection<? extends IndexedObjectProperty> getObjectProperties() {
		return cachedObjectProperties_;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends IndexedClassExpression> getClassExpressions() {
		return Operations.getCollection(
				Operations.concat(cachedClasses_, cachedIndividuals_,
						cachedComplexClassExpressions_),
				cachedClasses_.size() + cachedIndividuals_.size()
						+ cachedComplexClassExpressions_.size());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<? extends IndexedPropertyChain> getPropertyChains() {
		return Operations.getCollection(
				Operations.concat(cachedObjectProperties_,
						cachedBinaryPropertyChains_),
				cachedObjectProperties_.size()
						+ cachedBinaryPropertyChains_.size());
	}

	@Override
	public CachedIndexedOwlThing getOwlThing() {
		return owlThing_;
	}

	@Override
	public CachedIndexedOwlNothing getOwlNothing() {
		return owlNothing_;
	}

	@Override
	public <T extends CachedIndexedObject<T>> T resolve(
			CachedIndexedObject<T> input) {
		return input.accept(resolver_);
	}

	@Override
	public void add(CachedIndexedObject<?> input) {
		LOGGER_.trace("{}: adding to cache", input);
		input.accept(inserter_);
	}

	@Override
	public void remove(CachedIndexedObject<?> input) {
		LOGGER_.trace("{}: removing from cache", input);
		input.accept(deleter_);
	}

	private class Resolver_ implements CachedIndexedObject.Filter {

		@Override
		public CachedIndexedClass filter(CachedIndexedClass element) {
			return cachedClasses_.findStructural(element);
		}

		@Override
		public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
			return cachedIndividuals_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectComplementOf filter(
				CachedIndexedObjectComplementOf element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectIntersectionOf filter(
				CachedIndexedObjectIntersectionOf element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectHasSelf filter(
				CachedIndexedObjectHasSelf element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectUnionOf filter(
				CachedIndexedObjectUnionOf element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedDataHasValue filter(
				CachedIndexedDataHasValue element) {
			return cachedComplexClassExpressions_.findStructural(element);
		}

		@Override
		public CachedIndexedObjectProperty filter(
				CachedIndexedObjectProperty element) {
			return cachedObjectProperties_.findStructural(element);
		}

		@Override
		public CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element) {
			return cachedBinaryPropertyChains_.findStructural(element);
		}

		@Override
		public CachedIndexedClassExpressionList filter(
				CachedIndexedClassExpressionList element) {
			return cachedClassExpressionLists_.findStructural(element);
		}

	}

	private class Inserter_ implements CachedIndexedObject.Filter {

		@Override
		public CachedIndexedClass filter(CachedIndexedClass element) {
			cachedClasses_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
			cachedIndividuals_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectComplementOf filter(
				CachedIndexedObjectComplementOf element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectIntersectionOf filter(
				CachedIndexedObjectIntersectionOf element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectHasSelf filter(
				CachedIndexedObjectHasSelf element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectUnionOf filter(
				CachedIndexedObjectUnionOf element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedDataHasValue filter(
				CachedIndexedDataHasValue element) {
			cachedComplexClassExpressions_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedObjectProperty filter(
				CachedIndexedObjectProperty element) {
			cachedObjectProperties_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element) {
			cachedBinaryPropertyChains_.addStructural(element);
			return null;
		}

		@Override
		public CachedIndexedClassExpressionList filter(
				CachedIndexedClassExpressionList element) {
			cachedClassExpressionLists_.addStructural(element);
			return null;
		}

	}

	private class Deleter_ implements CachedIndexedObject.Filter {

		@Override
		public CachedIndexedClass filter(CachedIndexedClass element) {
			return cachedClasses_.removeStructural(element);
		}

		@Override
		public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
			return cachedIndividuals_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectComplementOf filter(
				CachedIndexedObjectComplementOf element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectIntersectionOf filter(
				CachedIndexedObjectIntersectionOf element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectHasSelf filter(
				CachedIndexedObjectHasSelf element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectUnionOf filter(
				CachedIndexedObjectUnionOf element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedDataHasValue filter(
				CachedIndexedDataHasValue element) {
			return cachedComplexClassExpressions_.removeStructural(element);
		}

		@Override
		public CachedIndexedObjectProperty filter(
				CachedIndexedObjectProperty element) {
			return cachedObjectProperties_.removeStructural(element);
		}

		@Override
		public CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element) {
			return cachedBinaryPropertyChains_.removeStructural(element);
		}

		@Override
		public CachedIndexedClassExpressionList filter(
				CachedIndexedClassExpressionList element) {
			return cachedClassExpressionLists_.removeStructural(element);
		}

	}

}
