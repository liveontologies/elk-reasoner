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
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedComplexPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.model.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectComplementOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObject;
import org.semanticweb.elk.reasoner.indexing.model.ModifiableIndexedObjectCache;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedClassExpressionListEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedComplexClassExpressionEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedComplexPropertyChainEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedIndividualEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedObjectPropertyEntry;
import org.semanticweb.elk.reasoner.indexing.model.StructuralIndexedSubObject;
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

	private final EntryCollection<StructuralIndexedComplexClassExpressionEntry<?>> cachedComplexClassExpressions_;

	private final EntryCollection<StructuralIndexedComplexPropertyChainEntry<?>> cachedComplexPropertyChains_;

	private final EntryCollection<StructuralIndexedClassExpressionListEntry<?>> cachedClassExpressionLists_;

	private final EntryCollection<StructuralIndexedClassEntry<?>> cachedClasses_;

	private final EntryCollection<StructuralIndexedObjectPropertyEntry<?>> cachedObjectProperties_;

	private final EntryCollection<StructuralIndexedIndividualEntry<?>> cachedIndividuals_;

	private final StructuralIndexedClassEntry<?> owlThing_, owlNothing_;

	private final StructuralIndexedObjectPropertyEntry<?> owlTopObjectProperty_, owlBottomObjectProperty_;

	private final List<IndexedObjectCache.ChangeListener> listeners_;

	public ModifiableIndexedObjectCacheImpl(
			final PredefinedElkEntityFactory elkFactory, int initialSize) {
		this.cachedComplexClassExpressions_ = new EntryCollection<StructuralIndexedComplexClassExpressionEntry<?>>(
				initialSize);
		this.cachedComplexPropertyChains_ = new EntryCollection<StructuralIndexedComplexPropertyChainEntry<?>>(
				initialSize);
		this.cachedClassExpressionLists_ = new EntryCollection<StructuralIndexedClassExpressionListEntry<?>>(
				initialSize);
		this.cachedClasses_ = new EntryCollection<StructuralIndexedClassEntry<?>>(
				initialSize);
		this.cachedObjectProperties_ = new EntryCollection<StructuralIndexedObjectPropertyEntry<?>>(
				initialSize);
		this.cachedIndividuals_ = new EntryCollection<StructuralIndexedIndividualEntry<?>>(
				initialSize);
		// predefined entities always occur in the cache
		this.owlThing_ = new OwlThingImpl(
				elkFactory.getOwlThing());
		this.owlNothing_ = new OwlNothingImpl(
				elkFactory.getOwlNothing());
		this.owlTopObjectProperty_ = new OwlTopObjectPropertyImpl(
				elkFactory.getOwlTopObjectProperty());
		this.owlBottomObjectProperty_ = new OwlBottomObjectPropertyImpl(
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
						cachedComplexPropertyChains_),
				cachedObjectProperties_.size()
						+ cachedComplexPropertyChains_.size());
	}

	@Override
	public final StructuralIndexedClassEntry<?> getOwlThing() {
		return owlThing_;
	}

	@Override
	public final StructuralIndexedClassEntry<?> getOwlNothing() {
		return owlNothing_;
	}

	@Override
	public StructuralIndexedObjectPropertyEntry<?> getOwlTopObjectProperty() {
		return owlTopObjectProperty_;
	}

	@Override
	public StructuralIndexedObjectPropertyEntry<?> getOwlBottomObjectProperty() {
		return owlBottomObjectProperty_;
	}

	@Override
	public void add(StructuralIndexedSubObject<?> input) {
		LOGGER_.trace("{}: adding to cache", input);
		input.accept(new StructuralIndexedSubObject.Visitor<Void>() {

			@Override
			public <T extends StructuralIndexedClassEntry<T>> Void visit(T element) {
				cachedClasses_.addStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_
							.get(i);
					listener.classAddition(element);
					listener.classExpressionAddition(element);
				}
				return null;
			}
			
			@Override
			public <T extends StructuralIndexedComplexClassExpressionEntry<T>> Void visit(T element) {			
				cachedComplexClassExpressions_.addStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).classExpressionAddition(element);
				}
				return null;
			}


			@Override
			public <T extends StructuralIndexedIndividualEntry<T>> Void visit(T element) {
				cachedIndividuals_.addStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_
							.get(i);
					listener.individualAddition(element);
					listener.classExpressionAddition(element);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedClassExpressionListEntry<T>> Void visit(T element) {
				cachedClassExpressionLists_.addStructural(element);
				return null;
			}

			@Override
			public <T extends StructuralIndexedComplexPropertyChainEntry<T>> Void visit(T element) {
				cachedComplexPropertyChains_.addStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).propertyChainAddition(element);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedObjectPropertyEntry<T>> Void visit(T element) {
				cachedObjectProperties_.addStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_
							.get(i);
					listener.objectPropertyAddition(element);
					listener.propertyChainAddition(element);
				}
				return null;
			}

		});
	}

	private EntryCollection<?> getResolver(IndexedSubObject input) {
		return input.accept(new IndexedSubObject.Visitor<EntryCollection<?>>() {

			@Override
			public EntryCollection<?> visit(IndexedClass element) {
				return cachedClasses_;
			}

			@Override
			public EntryCollection<?> visit(IndexedIndividual element) {
				return cachedIndividuals_;
			}

			@Override
			public EntryCollection<?> visit(IndexedDataHasValue element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(IndexedObjectComplementOf element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(IndexedObjectHasSelf element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(
					IndexedObjectIntersectionOf element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(
					IndexedObjectSomeValuesFrom element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(IndexedObjectUnionOf element) {
				return cachedComplexClassExpressions_;
			}

			@Override
			public EntryCollection<?> visit(IndexedObjectProperty element) {
				return cachedObjectProperties_;
			}

			@Override
			public EntryCollection<?> visit(
					IndexedComplexPropertyChain element) {
				return cachedComplexPropertyChains_;
			}

			@Override
			public EntryCollection<?> visit(
					IndexedClassExpressionList element) {
				return cachedClassExpressionLists_;
			}

		});

	}

	@Override
	public <T extends StructuralIndexedSubObject<T>> T resolve(T input) {
		return getResolver(input).findStructural(input);
	}

	@Override
	public void remove(StructuralIndexedSubObject<?> input) {
		input.accept(new StructuralIndexedSubObject.Visitor<Void>() {

			@Override
			public <T extends StructuralIndexedClassEntry<T>> Void visit(T element) {
				T removed = cachedClasses_.removeStructural(element);
				if (removed == null) {
					return null;
				}
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_.get(i);
					listener.classRemoval(removed);
					listener.classExpressionRemoval(removed);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedComplexClassExpressionEntry<T>> Void visit(T element) {
				T removed = cachedComplexClassExpressions_.removeStructural(element);
				if (removed == null) {
					return null;
				}
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).classExpressionRemoval(removed);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedIndividualEntry<T>> Void visit(T element) {
				T removed = cachedIndividuals_.removeStructural(element);
				if (removed == null) {
					return null;
				}
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_
							.get(i);
					listener.individualRemoval(element);
					listener.classExpressionRemoval(element);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedClassExpressionListEntry<T>> Void visit(T element) {
				cachedClassExpressionLists_.removeStructural(element);
				return null;
			}

			@Override
			public <T extends StructuralIndexedComplexPropertyChainEntry<T>> Void visit(T element) {
				cachedComplexPropertyChains_.removeStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					listeners_.get(i).propertyChainRemoval(element);
				}
				return null;
			}

			@Override
			public <T extends StructuralIndexedObjectPropertyEntry<T>> Void visit(T element) {
				cachedObjectProperties_.removeStructural(element);
				for (int i = 0; i < listeners_.size(); i++) {
					IndexedObjectCache.ChangeListener listener = listeners_
							.get(i);
					listener.objectPropertyRemoval(element);
					listener.propertyChainRemoval(element);
				}
				return null;
			}

		});	
	}

	@Override
	public final boolean addListener(
			IndexedObjectCache.ChangeListener listener) {
		return listeners_.add(listener);
	}

	@Override
	public final boolean removeListener(
			IndexedObjectCache.ChangeListener listener) {
		return listeners_.remove(listener);
	}

}
