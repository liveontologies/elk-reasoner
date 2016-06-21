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

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.OntologyIndex;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
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
	 * Property taxonomy that is populated during processing.
	 */
	private final UpdateableTaxonomy<ElkObjectProperty> taxonomy_;

	private final OntologyIndex index_;

	private final ElkObjectProperty topProperty_;

	private final ElkObjectProperty bottomProperty_;

	private final IndexedObjectProperty indexedBottomProperty_;

	public ObjectPropertyTaxonomyComputationFactory(
			final UpdateableTaxonomy<ElkObjectProperty> taxonomy,
			final OntologyIndex index,
			final PredefinedElkObjectPropertyFactory predefinedFactory) {
		this.taxonomy_ = taxonomy;
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

		// Add top node if not present.

		NonBottomTaxonomyNode<ElkObjectProperty> topNode = taxonomy_
				.getNonBottomNode(topProperty_);
		if (topNode == null) {
			topNode = taxonomy_
					.getCreateNode(Collections.singleton(topProperty_));
		}

		// Collect all nodes with no super-node.
		final Collection<Set<ElkObjectProperty>> subMemberSets = Operations.map(
				taxonomy_.getNodes(),
				new Operations.Transformation<TaxonomyNode<ElkObjectProperty>, Set<ElkObjectProperty>>() {

					@Override
					public Set<ElkObjectProperty> transform(
							final TaxonomyNode<ElkObjectProperty> node) {
						if (!node.getDirectSuperNodes().isEmpty()
								|| node.equals(taxonomy_.getTopNode())) {
							return null;
						}
						// else
						return new AbstractSet<ElkObjectProperty>() {

							@Override
							public Iterator<ElkObjectProperty> iterator() {
								return node.iterator();
							}

							@Override
							public int size() {
								return node.size();
							}

							@Override
							public boolean contains(final Object o) {
								if (o instanceof ElkObjectProperty) {
									return node.contains((ElkObjectProperty) o);
								}
								return false;
							}

						};
					}

				});

		taxonomy_.setCreateDirectSupernodes(topNode, subMemberSets);
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

		final Set<IndexedObjectProperty> subProperties = getSubProperties(
				property);
		final NonBottomTaxonomyNode<ElkObjectProperty> createdNode;
		final TaxonomyNode<ElkObjectProperty> node = taxonomy_
				.getNode(property.getElkEntity());
		if (node != null) {

			// If property is in the bottom node, it is already finished.
			if (node.equals(taxonomy_.getBottomNode())) {
				return;
			}

			createdNode = taxonomy_.getNonBottomNode(property.getElkEntity());

			/*
			 * If property has some non-bottom sub-nodes, it is already
			 * finished.
			 */
			if (!createdNode.getDirectNonBottomSubNodes().isEmpty()) {
				return;
			}

		} else {

			// There is no node, so we need to create it.
			createdNode = createNodeFromEquivalent(property);
			if (createdNode == null) {
				// There should be no sub-nodes.
				return;
			}

		}

		/*
		 * If this is a top node, do not assign sub-nodes, it will be done in
		 * finish.
		 */
		if (createdNode.equals(taxonomy_.getTopNode())) {
			return;
		}

		/*
		 * Create nodes for strict sub-properties and collect their strict
		 * sub-properties.
		 */
		final Set<TaxonomyNode<ElkObjectProperty>> subNodes = new ArrayHashSet<TaxonomyNode<ElkObjectProperty>>();
		final Set<ElkObjectProperty> indirect = new ArrayHashSet<ElkObjectProperty>();
		for (final IndexedObjectProperty subProperty : subProperties) {

			if (createdNode.contains(subProperty.getElkEntity())) {
				// not strict
				continue;
			}

			final Set<IndexedObjectProperty> subSubProperties = getSubProperties(
					subProperty);
			final NonBottomTaxonomyNode<ElkObjectProperty> createdSubNode;
			final TaxonomyNode<ElkObjectProperty> subNode = taxonomy_
					.getNode(subProperty.getElkEntity());
			if (subNode != null) {
				// if subNode is bottom, this will be null
				createdSubNode = taxonomy_
						.getNonBottomNode(subProperty.getElkEntity());
			} else {
				// There is no subNode, so we need to create it.
				createdSubNode = createNodeFromEquivalent(subProperty);
				// if subProperty is in bottom, this will be null
			}

			if (createdSubNode != null) {
				/*
				 * subProperty is not in bottom, so it may have strict
				 * sub-properties and its node may be added as a sub-node.
				 */
				subNodes.add(createdSubNode);
				for (final IndexedObjectProperty subSubProperty : subSubProperties) {
					if (!createdSubNode
							.contains(subSubProperty.getElkEntity())) {
						// strict
						indirect.add(subSubProperty.getElkEntity());
					}
				}
			}

		}

		final Collection<Set<ElkObjectProperty>> subMemberSets = Operations.map(
				subNodes,
				new Operations.Transformation<TaxonomyNode<ElkObjectProperty>, Set<ElkObjectProperty>>() {

					@Override
					public Set<ElkObjectProperty> transform(
							final TaxonomyNode<ElkObjectProperty> node) {
						if (indirect.contains(node.getCanonicalMember())) {
							return null;
						}
						// else
						return new AbstractSet<ElkObjectProperty>() {

							@Override
							public Iterator<ElkObjectProperty> iterator() {
								return node.iterator();
							}

							@Override
							public int size() {
								return node.size();
							}

							@Override
							public boolean contains(final Object o) {
								if (o instanceof ElkObjectProperty) {
									return node.contains((ElkObjectProperty) o);
								}
								return false;
							}

						};
					}

				});

		taxonomy_.setCreateDirectSupernodes(createdNode, subMemberSets);

	}

	/**
	 * Collects properties that are equivalent to <code>property</code> and adds
	 * creates a node in the taxonomy that contains them, except if
	 * <code>property</code> is equivalent to bottom, in which case it is added
	 * to the bottom node.
	 * 
	 * @param property
	 * @return The created node if <code>property</code> is <strong>not</strong>
	 *         equivalent to bottom, <code>null</code> otherwise.
	 */
	private NonBottomTaxonomyNode<ElkObjectProperty> createNodeFromEquivalent(
			final IndexedObjectProperty property) {

		final Set<IndexedObjectProperty> subProperties = getSubProperties(
				property);
		final Collection<ElkObjectProperty> equivalent = new ArrayList<ElkObjectProperty>(
				subProperties.size());
		boolean isInBottom = false;
		for (final IndexedObjectProperty subProperty : subProperties) {

			if (subProperty.getElkEntity().equals(topProperty_)) {
				equivalent.add(subProperty.getElkEntity());
			} else if (getSubProperties(subProperty).contains(property)) {
				isInBottom = isInBottom
						|| subProperty.getElkEntity().equals(bottomProperty_);
				equivalent.add(subProperty.getElkEntity());
			}

		}
		if (isInBottom) {
			for (final ElkObjectProperty eqProp : equivalent) {
				taxonomy_.addToBottomNode(eqProp);
			}
			return null;
		} else {
			return taxonomy_.getCreateNode(equivalent);
		}
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

}
