/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.predefined.PredefinedElkObjectPropertyFactory;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractDistinctBottomTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.impl.AbstractUpdateableGenericTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.impl.BottomGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.impl.ConcurrentNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.impl.NonBottomGenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.NonBottomTaxonomyNode;
import org.semanticweb.elk.reasoner.taxonomy.model.TaxonomyNodeFactory;

/**
 * Object property taxonomy suitable for concurrent construction or concurrent
 * cleaning.
 * <p>
 * <strong>CAUTION!</strong> This taxonomy is reverse, i.e., top node contains
 * bottom property, sub-nodes contain super-properties, and so on.
 * 
 * @author Peter Skocovsky
 * 
 * @see #setCreateDirectSupernodes(NonBottomTaxonomyNode, Iterable)
 * @see #removeDirectSupernodes(NonBottomTaxonomyNode)
 */
// @formatter:off
public class ReverseObjectPropertyTaxonomy
		extends AbstractUpdateableGenericTaxonomy<
				ElkObjectProperty,
				GenericTaxonomyNode.Projection<ElkObjectProperty>,
				NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
		> {

	private final GenericTaxonomyNode.Projection<ElkObjectProperty> bottomNode_;

	public ReverseObjectPropertyTaxonomy(
			final PredefinedElkObjectPropertyFactory elkFactory,
			final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		super(
				new ConcurrentNodeStore<
						ElkObjectProperty,
						NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
				>(classKeyProvider),
				new TaxonomyNodeFactory<
						ElkObjectProperty,
						NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>,
						AbstractDistinctBottomTaxonomy<
								ElkObjectProperty,
								GenericTaxonomyNode.Projection<ElkObjectProperty>,
								NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
						>
				>() {
					@Override
					public NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty> createNode(
							final Iterable<? extends ElkObjectProperty> members,
							final int size,
							final AbstractDistinctBottomTaxonomy<
									ElkObjectProperty,
									GenericTaxonomyNode.Projection<ElkObjectProperty>,
									NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>
							> taxonomy) {
						return new NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>(
								taxonomy, members, size);
					}
				},
				elkFactory.getOwlBottomObjectProperty());
		this.bottomNode_ = new BottomGenericTaxonomyNode.Projection<ElkObjectProperty>(
				this, elkFactory.getOwlTopObjectProperty());
	}
// @formatter:on

	@Override
	public GenericTaxonomyNode.Projection<ElkObjectProperty> getBottomNode() {
		return bottomNode_;
	}

	@Override
	protected Set<? extends GenericTaxonomyNode.Projection<ElkObjectProperty>> toTaxonomyNodes(
			final Set<? extends NonBottomGenericTaxonomyNode.Projection<ElkObjectProperty>> nodes) {
		return nodes;
	}

}
