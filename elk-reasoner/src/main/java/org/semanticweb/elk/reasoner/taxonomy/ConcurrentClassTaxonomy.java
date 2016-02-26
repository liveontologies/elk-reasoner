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
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTaxonomyNode;
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
				BottomTaxonomyNode<ElkClass>
		> {
	
	private final BottomTaxonomyNode<ElkClass> bottomNode_;
	
	public ConcurrentClassTaxonomy(
			final ComparatorKeyProvider<ElkEntity> classKeyProvider) {
		super(
				new ConcurrentNodeStore<ElkClass, UpdateableTaxonomyNode<ElkClass>>(classKeyProvider),
				new InternalNodeFactoryFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomTaxonomyNode<ElkClass>>() {
					@Override
					public InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomTaxonomyNode<ElkClass>> createInternalNodeFactory(
							final BottomTaxonomyNode<ElkClass> taxonomy) {
						return new InternalNodeFactory<ElkClass, UpdateableTaxonomyNode<ElkClass>, BottomTaxonomyNode<ElkClass>>(taxonomy) {
							@Override
							public UpdateableTaxonomyNode<ElkClass> createNode(
									final Iterable<? extends ElkClass> members, final int size,
									final ComparatorKeyProvider<? super ElkClass> keyProvider) {
								return new NonBottomGenericTaxonomyNode.Projection<ElkClass>(taxonomy_, members, size);
							}
						};
					}
				},
				PredefinedElkClass.OWL_THING);
		this.bottomNode_ = new BottomGenericTaxonomyNode<ElkClass, UpdateableTaxonomy<ElkClass>>(this, PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public BottomTaxonomyNode<ElkClass> getBottomNode() {
		return bottomNode_;
	}
	
}
