package org.semanticweb.elk.reasoner.taxonomy;

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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.BottomTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableGenericNodeStore;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableTaxonomyTypeNode;

public class IndividualTaxonomy
		extends AbstractUpdateableGenericInstanceTaxonomy<
				ElkClass,
				ElkNamedIndividual,
				UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>,
				UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>,
				BottomTypeNode<ElkClass, ElkNamedIndividual>
		> {

	private final BottomTypeNode<ElkClass, ElkNamedIndividual> bottomNode_;
	
	public IndividualTaxonomy(
			final ComparatorKeyProvider<ElkEntity> classKeyProvider,
			final ComparatorKeyProvider<ElkNamedIndividual> instanceKeyProvider,
			UpdateableGenericNodeStore<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>> typeNodeStore,
			InternalNodeFactoryFactory<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>, BottomTypeNode<ElkClass, ElkNamedIndividual>> typeNodeFactoryFactory,
			UpdateableGenericNodeStore<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>> instanceNodeStore,
			InternalNodeFactoryFactory<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>> instanceNodeFactoryFactory,
			ElkClass topMember) {
		super(
				new ConcurrentNodeStore<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>>(classKeyProvider),
				new InternalNodeFactoryFactory<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>, BottomTypeNode<ElkClass, ElkNamedIndividual>>() {
					@Override
					public InternalNodeFactory<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>, BottomTypeNode<ElkClass, ElkNamedIndividual>> createInternalNodeFactory(
							final BottomTypeNode<ElkClass, ElkNamedIndividual> taxonomy) {
						return new InternalNodeFactory<ElkClass, UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual>, BottomTypeNode<ElkClass, ElkNamedIndividual>>(taxonomy) {
							@Override
							public UpdateableTaxonomyTypeNode<ElkClass, ElkNamedIndividual> createNode(
									final Iterable<? extends ElkClass> members, final int size,
									final ComparatorKeyProvider<? super ElkClass> keyProvider) {
								return new NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>(taxonomy_, members, size);
							}
						};
					}
				},
				new ConcurrentNodeStore<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>>(instanceKeyProvider),
				new InternalNodeFactoryFactory<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>>() {
					@Override
					public InternalNodeFactory<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>> createInternalNodeFactory(
							final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy) {
						return new InternalNodeFactory<ElkNamedIndividual, UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>>(taxonomy) {
							@Override
							public UpdateableTaxonomyInstanceNode<ElkClass, ElkNamedIndividual> createNode(
									final Iterable<? extends ElkNamedIndividual> members, final int size,
									final ComparatorKeyProvider<? super ElkNamedIndividual> keyProvider) {
								return new IndividualNode.Projection2<ElkClass, ElkNamedIndividual>(taxonomy_, members, size);
							}
						};
					}
				},
				PredefinedElkClass.OWL_THING);
		this.bottomNode_ = new BottomGenericTypeNode<ElkClass, ElkNamedIndividual, UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual>>(this, PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public BottomTypeNode<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottomNode_;
	}

}
