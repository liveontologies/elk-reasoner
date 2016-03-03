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
package org.semanticweb.elk.reasoner.taxonomy;

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.predefined.PredefinedElkClass;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericInstanceNode;
import org.semanticweb.elk.reasoner.taxonomy.model.GenericTypeNode;
import org.semanticweb.elk.reasoner.taxonomy.model.InstanceTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.model.UpdateableInstanceTaxonomy;

public class IndividualTaxonomy
		extends AbstractUpdateableGenericInstanceTaxonomy<
				ElkClass,
				ElkNamedIndividual,
				GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>,
				GenericInstanceNode.Projection<ElkClass, ElkNamedIndividual>,
				NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>,
				IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>
		> {

	private final GenericTypeNode.Projection<ElkClass, ElkNamedIndividual> bottomNode_;
	
	public IndividualTaxonomy(
			final ComparatorKeyProvider<ElkEntity> classKeyProvider,
			final ComparatorKeyProvider<ElkNamedIndividual> instanceKeyProvider) {
		super(
				new ConcurrentNodeStore<ElkClass, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>>(classKeyProvider),
				new InternalNodeFactoryFactory<ElkClass, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, AbstractDistinctBottomTaxonomy<ElkClass, GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>>>() {
					@Override
					public InternalNodeFactory<ElkClass, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, AbstractDistinctBottomTaxonomy<ElkClass, GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>>> createInternalNodeFactory(
							final AbstractDistinctBottomTaxonomy<ElkClass, GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>> taxonomy) {
						return new InternalNodeFactory<ElkClass, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, AbstractDistinctBottomTaxonomy<ElkClass, GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>, NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>>>(taxonomy) {
							@Override
							public NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual> createNode(
									final Iterable<? extends ElkClass> members, final int size,
									final ComparatorKeyProvider<? super ElkClass> keyProvider) {
								return new NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>(taxonomy_, members, size);
							}
						};
					}
				},
				new ConcurrentNodeStore<ElkNamedIndividual, IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>>(instanceKeyProvider),
				new InternalNodeFactoryFactory<ElkNamedIndividual, IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>>() {
					@Override
					public InternalNodeFactory<ElkNamedIndividual, IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>> createInternalNodeFactory(
							final InstanceTaxonomy<ElkClass, ElkNamedIndividual> taxonomy) {
						return new InternalNodeFactory<ElkNamedIndividual, IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>, InstanceTaxonomy<ElkClass, ElkNamedIndividual>>(taxonomy) {
							@Override
							public IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual> createNode(
									final Iterable<? extends ElkNamedIndividual> members, final int size,
									final ComparatorKeyProvider<? super ElkNamedIndividual> keyProvider) {
								return new IndividualTaxonomyNode.Projection<ElkClass, ElkNamedIndividual>(taxonomy_, members, size);
							}
						};
					}
				},
				PredefinedElkClass.OWL_THING);
		this.bottomNode_ = new BottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual, UpdateableInstanceTaxonomy<ElkClass, ElkNamedIndividual>>(this, PredefinedElkClass.OWL_NOTHING);
	}

	@Override
	public GenericTypeNode.Projection<ElkClass, ElkNamedIndividual> getBottomNode() {
		return bottomNode_;
	}

	@Override
	Set<? extends GenericTypeNode.Projection<ElkClass, ElkNamedIndividual>> toTaxonomyNodes(
			final Set<? extends NonBottomGenericTypeNode.Projection<ElkClass, ElkNamedIndividual>> nodes) {
		return nodes;
	}

}
