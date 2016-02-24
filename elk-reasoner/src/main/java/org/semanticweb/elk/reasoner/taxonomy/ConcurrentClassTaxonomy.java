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
/**
 * @author Yevgeny Kazakov, May 15, 2011
 */
package org.semanticweb.elk.reasoner.taxonomy;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyNode;

/**
 * Class taxonomy that is suitable for concurrent processing. Taxonomy objects
 * are only constructed for consistent ontologies, and some consequences of this
 * are hardcoded here.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * @author Peter Skocovsky
 */
public class ConcurrentClassTaxonomy
		extends AbstractUpdateableGenericTaxonomy<
				ElkClass,
				UpdateableTaxonomyNode<ElkClass>,
				NonBottomGenericTaxonomyNode.Projection<ElkClass>,
				BottomGenericTaxonomyNode.Projection<ElkClass>
		> implements UpdateableTaxonomy<ElkClass> {
	
	public ConcurrentClassTaxonomy(
			final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		super(
				new ConcurrentNodeStore<ElkClass, NonBottomGenericTaxonomyNode.Projection<ElkClass>>(classKeyProvider),
				new InternalNodeFactoryFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, NonBottomGenericTaxonomyNode.Projection<ElkClass>>() {
					@Override
					public InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, NonBottomGenericTaxonomyNode.Projection<ElkClass>> createInternalNodeFactory(
							AbstractDistinctBottomTaxonomy<ElkClass, UpdateableTaxonomyNode<ElkClass>> taxonomy) {
						return new InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, NonBottomGenericTaxonomyNode.Projection<ElkClass>>(taxonomy) {
							@Override
							public NonBottomGenericTaxonomyNode.Projection<ElkClass> createNode(
									Iterable<? extends ElkClass> members,
									int size,
									ComparatorKeyProvider<? super ElkClass> keyProvider) {
								return new NonBottomGenericTaxonomyNode.Projection<ElkClass>(taxonomy_, members, size);
							}
						};
					}
				},
				new InternalNodeFactoryFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomGenericTaxonomyNode.Projection<ElkClass>>() {
					@Override
					public InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomGenericTaxonomyNode.Projection<ElkClass>> createInternalNodeFactory(
							AbstractDistinctBottomTaxonomy<ElkClass, UpdateableTaxonomyNode<ElkClass>> taxonomy) {
						return new InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomGenericTaxonomyNode.Projection<ElkClass>>(taxonomy) {
							@Override
							public BottomGenericTaxonomyNode.Projection<ElkClass> createNode(Iterable<? extends ElkClass> members,
									int size, ComparatorKeyProvider<? super ElkClass> keyProvider) {
								return new BottomGenericTaxonomyNode.Projection<ElkClass>(taxonomy_);
							}
						};
					}
				},
				PredefinedElkClass.OWL_THING,
				PredefinedElkClass.OWL_NOTHING
			);
	}

}
