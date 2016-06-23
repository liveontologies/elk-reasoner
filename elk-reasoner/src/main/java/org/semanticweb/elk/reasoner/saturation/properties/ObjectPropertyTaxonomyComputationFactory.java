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
package org.semanticweb.elk.reasoner.saturation.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.util.collections.ArrayHashMap;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;
import org.semanticweb.elk.util.concurrent.computation.SimpleInterrupter;

/**
 * Computes object property taxonomy.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyTaxonomyComputationFactory extends SimpleInterrupter
		implements
		InputProcessorFactory<IndexedObjectProperty, ObjectPropertyTaxonomyComputationFactory.Engine> {

	/**
	 * Visitor accepting results of the transitive reduction.
	 */
	private final TransitiveReductionOutputVisitor<ElkObjectProperty> outputProcessor_;

	private final OntologyIndex index_;

	private final ElkObjectProperty topProperty_;

	private final ElkObjectProperty bottomProperty_;

	private final IndexedObjectProperty indexedBottomProperty_;

	public ObjectPropertyTaxonomyComputationFactory(
			final TransitiveReductionOutputVisitor<ElkObjectProperty> outputProcessor,
			final OntologyIndex index,
			final PredefinedElkObjectPropertyFactory predefinedFactory) {
		this.outputProcessor_ = outputProcessor;
		this.index_ = index;
		this.topProperty_ = predefinedFactory.getOwlTopObjectProperty();
		this.bottomProperty_ = predefinedFactory.getOwlBottomObjectProperty();
		// TODO: Index should provide the bottom property instead of this.
		for (final IndexedObjectProperty prop : index.getObjectProperties()) {
			if (prop.getElkEntity().equals(bottomProperty_)) {
				indexedBottomProperty_ = prop;
				return;
			}
		}
		indexedBottomProperty_ = null;
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public void finish() {
		// Empty.
	}

	class Engine implements InputProcessor<IndexedObjectProperty> {

		@Override
		public void submit(final IndexedObjectProperty property) {
			instertIntoTaxonomy(property);
		}

		@Override
		public void process() throws InterruptedException {
			// Everything is done in submit().
		}

		@Override
		public void finish() {
			// Empty.
		}

	}

	/**
	 * Adds the specified object property into the taxonomy if it is not in it
	 * yet and sets its direct sub-properties if not set yet.
	 * 
	 * @param property
	 *            The property that should be inserted into taxonomy.
	 */
	private void instertIntoTaxonomy(final IndexedObjectProperty property) {

		/* 
		 * @formatter:off
		 * 
		 * Transitive reduction and taxonomy computation
		 * 	if sub-properties of a sub-property contain this property,
		 * 		they are equivalent
		 * 	if a property is a strict sub-property of another strict sub-property,
		 * 		it is not direct
		 * 
		 * @formatter:on
		 */

		final Map<IndexedObjectProperty, ElkObjectProperty> equivalent = collectEquivalent(
				property);
		if (equivalent == null) {
			// Equivalent to top.
			return;
		}
		final Map<IndexedObjectProperty, Collection<? extends ElkObjectProperty>> subEquivalent = new ArrayHashMap<IndexedObjectProperty, Collection<? extends ElkObjectProperty>>();
		final Set<IndexedObjectProperty> indirect = new ArrayHashSet<IndexedObjectProperty>();
		for (final IndexedObjectProperty subProperty : getSubProperties(
				property)) {

			if (equivalent.containsKey(subProperty)) {
				// subProperty is not strict
				continue;
			}
			// subProperty is strict

			final Map<IndexedObjectProperty, ElkObjectProperty> subEq = collectEquivalent(
					subProperty);
			// should not be null, because top cannot be a strict sub-property
			subEquivalent.put(subProperty, subEq.values());
			for (final IndexedObjectProperty subSubProperty : getSubProperties(
					subProperty)) {
				if (!subEq.containsKey(subSubProperty)) {
					// strict
					indirect.add(subSubProperty);
				}
			}

		}

		/*
		 * If property is not equivalent to bottom and there are no strict sub
		 * properties, add the bottom as a default sub property.
		 */
		if (subEquivalent.isEmpty() && (indexedBottomProperty_ == null
				|| !equivalent.containsKey(indexedBottomProperty_))) {
			outputProcessor_
					.visit(new TransitiveReductionOutputEquivalentDirectImpl<ElkObjectProperty>(
							equivalent.values(), Collections.singleton(
									Collections.singleton(bottomProperty_))));
			return;
		}
		// else

		final Collection<Collection<? extends ElkObjectProperty>> direct = Operations
				.map(subEquivalent.entrySet(),
						new Operations.Transformation<Map.Entry<IndexedObjectProperty, Collection<? extends ElkObjectProperty>>, Collection<? extends ElkObjectProperty>>() {

							@Override
							public Collection<? extends ElkObjectProperty> transform(
									final Entry<IndexedObjectProperty, Collection<? extends ElkObjectProperty>> element) {
								if (indirect.contains(element.getKey())) {
									return null;
								} else {
									return element.getValue();
								}
							}

						});

		outputProcessor_
				.visit(new TransitiveReductionOutputEquivalentDirectImpl<ElkObjectProperty>(
						equivalent.values(), direct));

	}

	/**
	 * Collects sub-properties of <code>property</code> that are equivalent to
	 * it. Returns <code>null</code> if <code>property</code> is equivalent to
	 * the top property.
	 * 
	 * @param property
	 * @return <code>null</code> if the specified property is equivalent to the
	 *         top property, otherwise mapping from equivalent indexed
	 *         properties to their elk entities.
	 */
	private Map<IndexedObjectProperty, ElkObjectProperty> collectEquivalent(
			final IndexedObjectProperty property) {

		final Set<IndexedObjectProperty> subProperties = getSubProperties(
				property);
		final Map<IndexedObjectProperty, ElkObjectProperty> equivalent = new ArrayHashMap<IndexedObjectProperty, ElkObjectProperty>();
		for (final IndexedObjectProperty subProperty : subProperties) {
			if (subProperty.getElkEntity().equals(topProperty_)) {
				outputProcessor_
						.visit(new TransitiveReductionOutputExtremeImpl<ElkObjectProperty>(
								property.getElkEntity()));
				return null;
			}
			if (getSubProperties(subProperty).contains(property)) {
				equivalent.put(subProperty, subProperty.getElkEntity());
			}
		}

		return equivalent;
	}

	/**
	 * Returns derived sub-properties of <code>property</code> including the
	 * bottom property (if it is indexed). If <code>property</code> is the top
	 * property, returns all properties.
	 * 
	 * @param property
	 * @return derived sub-properties of <code>property</code> including the
	 *         bottom property (if it is indexed). If <code>property</code> is
	 *         the top property, returns all properties.
	 */
	private Set<IndexedObjectProperty> getSubProperties(
			final IndexedObjectProperty property) {
		// TODO: This should be done during property saturation!
		final Set<IndexedObjectProperty> result = new HashSet<IndexedObjectProperty>(
				topProperty_.equals(property.getElkEntity())
						? index_.getObjectProperties()
						: property.getSaturated().getSubProperties());
		if (indexedBottomProperty_ != null) {
			result.add(indexedBottomProperty_);
		}
		return result;
	}

	private static class TransitiveReductionOutputEquivalentDirectImpl<E extends ElkEntity>
			implements TransitiveReductionOutputEquivalentDirect<E> {

		final Collection<? extends E> equivalent_;
		final Collection<? extends Collection<? extends E>> direct_;

		public TransitiveReductionOutputEquivalentDirectImpl(
				final Collection<? extends E> equivalent,
				final Collection<? extends Collection<? extends E>> direct) {
			this.equivalent_ = equivalent;
			this.direct_ = direct;
		}

		@Override
		public Collection<? extends E> getEquivalent() {
			return equivalent_;
		}

		@Override
		public Iterable<? extends Collection<? extends E>> getDirectlyRelated() {
			return Collections.unmodifiableCollection(direct_);
		}

		@Override
		public void accept(final TransitiveReductionOutputVisitor<E> visitor) {
			visitor.visit(this);
		}

	}

	private static class TransitiveReductionOutputExtremeImpl<E extends ElkEntity>
			implements TransitiveReductionOutputExtreme<E> {

		final E extremeMember_;

		public TransitiveReductionOutputExtremeImpl(final E extremeMember) {
			this.extremeMember_ = extremeMember;
		}

		@Override
		public E getExtremeMember() {
			return extremeMember_;
		}

		@Override
		public void accept(final TransitiveReductionOutputVisitor<E> visitor) {
			visitor.visit(this);
		}

	}

}
