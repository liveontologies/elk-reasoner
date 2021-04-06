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
package org.semanticweb.elk.reasoner.indexing.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.owl.predefined.PredefinedElkEntityFactory;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlBottomObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedOwlTopObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.CachedIndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectCache;
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

	private final CachedIndexedSubObject.Filter resolver_, inserter_, deleter_;

	private final CachedIndexedClass owlThing_;

	private final CachedIndexedClass owlNothing_;

	private final CachedIndexedOwlTopObjectProperty owlTopObjectProperty_;

	private final CachedIndexedOwlBottomObjectProperty owlBottomObjectProperty_;

	private final List<IndexedObjectCache.ChangeListener> listeners_;

	public ModifiableIndexedObjectCacheImpl(
			final PredefinedElkEntityFactory elkFactory, int initialSize) {
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
		this.owlTopObjectProperty_ = new CachedIndexedOwlTopObjectPropertyImpl(
				elkFactory.getOwlTopObjectProperty());
		this.owlBottomObjectProperty_ = new CachedIndexedOwlBottomObjectPropertyImpl(
				elkFactory.getOwlBottomObjectProperty());
		this.listeners_ = new ArrayList<IndexedObjectCache.ChangeListener>();
		add(owlThing_);
		add(owlNothing_);
		add(owlTopObjectProperty_);
		add(owlBottomObjectProperty_);
	}

	public ModifiableIndexedObjectCacheImpl(
			final PredefinedElkEntityFactory elkFactory) {
		this(elkFactory, 1024);
	}

	@Override
	public final Collection<? extends IndexedClass> getClasses() {
		return cachedClasses_;
	}

	@Override
	public final Collection<? extends IndexedIndividual> getIndividuals() {
		return cachedIndividuals_;
	}

	@Override
	public final Collection<? extends IndexedObjectProperty> getObjectProperties() {
		return cachedObjectProperties_;
	}

	@Override
	public final Collection<? extends IndexedClassExpression> getClassExpressions() {
		return Operations.getCollection(
				Operations.concat(cachedClasses_, cachedIndividuals_,
						cachedComplexClassExpressions_),
				cachedClasses_.size() + cachedIndividuals_.size()
						+ cachedComplexClassExpressions_.size());
	}

	@Override
	public final Collection<? extends IndexedPropertyChain> getPropertyChains() {
		return Operations.getCollection(
				Operations.concat(cachedObjectProperties_,
						cachedBinaryPropertyChains_),
				cachedObjectProperties_.size()
						+ cachedBinaryPropertyChains_.size());
	}

	@Override
	public final CachedIndexedClass getOwlThing() {
		return owlThing_;
	}

	@Override
	public final CachedIndexedClass getOwlNothing() {
		return owlNothing_;
	}

	@Override
	public CachedIndexedOwlTopObjectProperty getOwlTopObjectProperty() {
		return owlTopObjectProperty_;
	}

	@Override
	public CachedIndexedOwlBottomObjectProperty getOwlBottomObjectProperty() {
		return owlBottomObjectProperty_;
	}

	@Override
	public CachedIndexedSubObject.Filter getResolver() {	
		return this.resolver_;
	}

	@Override
	public void add(CachedIndexedSubObject input) {
		LOGGER_.trace("{}: adding to cache", input);
		input.accept(inserter_);
	}

	@Override
	public void remove(CachedIndexedSubObject input) {
		LOGGER_.trace("{}: removing from cache", input);
		input.accept(deleter_);
	}

	@Override
	public final boolean addListener(IndexedObjectCache.ChangeListener listener) {
		return listeners_.add(listener);
	}

	@Override
	public final boolean removeListener(IndexedObjectCache.ChangeListener listener) {
		return listeners_.remove(listener);
	}

	private class Resolver_ implements CachedIndexedSubObject.Filter {

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

	private class Inserter_ implements CachedIndexedSubObject.Filter {

		@Override
		public CachedIndexedClass filter(CachedIndexedClass element) {
			cachedClasses_.addStructural(element);
			for (int i = 0; i < listeners_.size(); i++) {
				IndexedObjectCache.ChangeListener listener = listeners_.get(i);
				listener.classAddition(element);
				listener.classExpressionAddition(element);
			}
			return null;
		}

		@Override
		public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
			cachedIndividuals_.addStructural(element);
			for (int i = 0; i < listeners_.size(); i++) {
				IndexedObjectCache.ChangeListener listener = listeners_.get(i);
				listener.individualAddition(element);
				listener.classExpressionAddition(element);
			}
			return null;
		}

		private <T extends CachedIndexedComplexClassExpression<T>> T defaultFilter(
				T element) {
			cachedComplexClassExpressions_.addStructural(element);
			for (int i = 0; i < listeners_.size(); i++) {
				listeners_.get(i).classExpressionAddition(element);
			}
			return null;
		}

		@Override
		public CachedIndexedObjectComplementOf filter(
				CachedIndexedObjectComplementOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectIntersectionOf filter(
				CachedIndexedObjectIntersectionOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectHasSelf filter(
				CachedIndexedObjectHasSelf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectUnionOf filter(
				CachedIndexedObjectUnionOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedDataHasValue filter(
				CachedIndexedDataHasValue element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectProperty filter(
				CachedIndexedObjectProperty element) {
			cachedObjectProperties_.addStructural(element);
			for (int i = 0; i < listeners_.size(); i++) {
				IndexedObjectCache.ChangeListener listener = listeners_.get(i);
				listener.objectPropertyAddition(element);
				listener.propertyChainAddition(element);
			}
			return null;
		}

		@Override
		public CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element) {
			cachedBinaryPropertyChains_.addStructural(element);
			for (int i = 0; i < listeners_.size(); i++) {
				listeners_.get(i).propertyChainAddition(element);
			}
			return null;
		}

		@Override
		public CachedIndexedClassExpressionList filter(
				CachedIndexedClassExpressionList element) {
			cachedClassExpressionLists_.addStructural(element);
			return null;
		}

	}

	private class Deleter_ implements CachedIndexedSubObject.Filter {

		@Override
		public CachedIndexedClass filter(CachedIndexedClass element) {
			CachedIndexedClass result = cachedClasses_
					.removeStructural(element);
			if (result != null) {
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_.get(i);
					listener.classRemoval(result);
					listener.classExpressionRemoval(result);
				}
			}
			return result;
		}

		@Override
		public CachedIndexedIndividual filter(CachedIndexedIndividual element) {
			CachedIndexedIndividual result = cachedIndividuals_
					.removeStructural(element);
			if (result != null) {
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_.get(i);
					listener.individualRemoval(result);
					listener.classExpressionRemoval(result);
				}
			}
			return result;
		}

		private <T extends CachedIndexedComplexClassExpression<T>> T defaultFilter(
				T element) {
			T result = cachedComplexClassExpressions_.removeStructural(element);
			if (result != null) {
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).classExpressionRemoval(element);
				}
			}
			return result;
		}

		@Override
		public CachedIndexedObjectComplementOf filter(
				CachedIndexedObjectComplementOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectIntersectionOf filter(
				CachedIndexedObjectIntersectionOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectSomeValuesFrom filter(
				CachedIndexedObjectSomeValuesFrom element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectHasSelf filter(
				CachedIndexedObjectHasSelf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectUnionOf filter(
				CachedIndexedObjectUnionOf element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedDataHasValue filter(
				CachedIndexedDataHasValue element) {
			return defaultFilter(element);
		}

		@Override
		public CachedIndexedObjectProperty filter(
				CachedIndexedObjectProperty element) {
			CachedIndexedObjectProperty result = cachedObjectProperties_
					.removeStructural(element);
			if (result != null) {
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_.get(i);
					listener.objectPropertyRemoval(result);
					listener.propertyChainRemoval(result);
				}
			}
			return result;
		}

		@Override
		public CachedIndexedComplexPropertyChain filter(
				CachedIndexedComplexPropertyChain element) {
			CachedIndexedComplexPropertyChain result = cachedBinaryPropertyChains_
					.removeStructural(element);
			if (result != null) {
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).propertyChainRemoval(element);
				}
			}
			return result;
		}

		@Override
		public CachedIndexedClassExpressionList filter(
				CachedIndexedClassExpressionList element) {
			return cachedClassExpressionLists_.removeStructural(element);
		}

	}

}
