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
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.concurrent.computation.DelegateInterruptMonitor;

/**
 * Computes object property taxonomy.
 * 
 * @author Peter Skocovsky
 */
public class ObjectPropertyTaxonomyComputationFactory
		extends DelegateInterruptMonitor implements
		InputProcessorFactory<IndexedObjectProperty, ObjectPropertyTaxonomyComputationFactory.Engine> {

	/**
	 * Visitor accepting results of the transitive reduction.
	 */
	private final TransitiveReductionOutputVisitor<ElkObjectProperty> outputProcessor_;

	private final IndexedObjectProperty indexedTopProperty_;

	private final IndexedObjectProperty indexedBottomProperty_;

	private final Collection<? extends Collection<? extends ElkObjectProperty>> defaultDirectSubproperties_;

	public ObjectPropertyTaxonomyComputationFactory(
			final InterruptMonitor interrupter,
			final TransitiveReductionOutputVisitor<ElkObjectProperty> outputProcessor,
			final OntologyIndex index,
			final PredefinedElkObjectPropertyFactory predefinedFactory) {
		super(interrupter);
		this.outputProcessor_ = outputProcessor;
		this.indexedTopProperty_ = index.getOwlTopObjectProperty();
		this.indexedBottomProperty_ = index.getOwlBottomObjectProperty();
		this.defaultDirectSubproperties_ = Collections.singleton(Collections
				.singleton(predefinedFactory.getOwlBottomObjectProperty()));
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
		for (final IndexedObjectProperty subProperty : property.getSaturated()
				.getSubProperties()) {

			if (equivalent.containsKey(subProperty)) {
				// subProperty is not strict
				continue;
			}
			// subProperty is strict

			final Map<IndexedObjectProperty, ElkObjectProperty> subEq = collectEquivalent(
					subProperty);
			// should not be null, because top cannot be a strict sub-property
			subEquivalent.put(subProperty, subEq.values());
			for (final IndexedObjectProperty subSubProperty : subProperty
					.getSaturated().getSubProperties()) {
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
							equivalent.values(), defaultDirectSubproperties_));
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

		final Set<IndexedObjectProperty> subProperties = property.getSaturated()
				.getSubProperties();
		final Map<IndexedObjectProperty, ElkObjectProperty> equivalent = new ArrayHashMap<IndexedObjectProperty, ElkObjectProperty>();
		for (final IndexedObjectProperty subProperty : subProperties) {
			if (subProperty.equals(indexedTopProperty_)) {
				outputProcessor_
						.visit(new TransitiveReductionOutputExtremeImpl<ElkObjectProperty>(
								property.getElkEntity()));
				return null;
			}
			if (subProperty.getSaturated().getSubProperties().contains(property)
					|| property.equals(indexedBottomProperty_)) {
				equivalent.put(subProperty, subProperty.getElkEntity());
			}
		}
		if (indexedBottomProperty_.getSaturated().getSubProperties()
				.contains(property)) {
			equivalent.put(indexedBottomProperty_,
					indexedBottomProperty_.getElkEntity());
		}

		return equivalent;
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
