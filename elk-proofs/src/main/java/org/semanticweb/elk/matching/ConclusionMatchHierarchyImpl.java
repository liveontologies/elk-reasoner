package org.semanticweb.elk.matching;

/*
 * #%L
 * ELK Proofs Package
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

import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchHierarchy;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch3;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.util.collections.HashListMultimap;

public class ConclusionMatchHierarchyImpl
		implements ConclusionMatchHierarchy, ConclusionMatch.Visitor<Boolean> {

	private final ChildMap<BackwardLinkMatch2> backwardLinkMatch2Map_ = new ChildMap<BackwardLinkMatch2>();

	private final ChildMap<ForwardLinkMatch2> forwardLinkMatch2Map_ = new ChildMap<ForwardLinkMatch2>();

	private final ChildMap<IndexedDefinitionAxiomMatch2> indexedDefinitionAxiomMatch2Map_ = new ChildMap<IndexedDefinitionAxiomMatch2>();

	private final ChildMap<IndexedDisjointClassesAxiomMatch2> indexedDisjointClassesAxiomMatch2Map_ = new ChildMap<IndexedDisjointClassesAxiomMatch2>();

	private final ChildMap<IndexedObjectPropertyRangeAxiomMatch2> indexedObjectPropertyRangeAxiomMatch2Map_ = new ChildMap<IndexedObjectPropertyRangeAxiomMatch2>();

	private final ChildMap<IndexedSubClassOfAxiomMatch2> indexedSubClassOfAxiomMatch2Map_ = new ChildMap<IndexedSubClassOfAxiomMatch2>();

	private final ChildMap<IndexedSubObjectPropertyOfAxiomMatch2> indexedSubObjectPropertyOfAxiomMatch2Map_ = new ChildMap<IndexedSubObjectPropertyOfAxiomMatch2>();

	private final ChildMap<PropagationMatch2> propagationMatch2Map_ = new ChildMap<PropagationMatch2>();

	private final ChildMap<PropagationMatch3> propagationMatch3Map_ = new ChildMap<PropagationMatch3>();

	private final ChildMap<PropertyRangeMatch2> propertyRangeMatch2Map_ = new ChildMap<PropertyRangeMatch2>();

	private final ChildMap<SubPropertyChainMatch2> subPropertyChainMatch2Map_ = new ChildMap<SubPropertyChainMatch2>();

	private final ChildMap<SubClassInclusionDecomposedMatch2> subClassInclusionDecomposedMatch2Map_ = new ChildMap<SubClassInclusionDecomposedMatch2>();

	@Override
	public Boolean visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		return indexedDisjointClassesAxiomMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		return indexedSubClassOfAxiomMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(IndexedDefinitionAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedDefinitionAxiomMatch2 conclusionMatch) {
		indexedDefinitionAxiomMatch2Map_.add(conclusionMatch);
		return null;
	}

	@Override
	public Boolean visit(
			IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(
			IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		return indexedSubObjectPropertyOfAxiomMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(
			IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(
			IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		return indexedObjectPropertyRangeAxiomMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(BackwardLinkMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(BackwardLinkMatch2 conclusionMatch) {
		backwardLinkMatch2Map_.add(conclusionMatch);
		return null;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		return subClassInclusionDecomposedMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(ForwardLinkMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(ForwardLinkMatch2 conclusionMatch) {
		return forwardLinkMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(PropagationMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(PropagationMatch2 conclusionMatch) {
		return propagationMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(PropagationMatch3 conclusionMatch) {
		return propagationMatch3Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(PropertyRangeMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(PropertyRangeMatch2 conclusionMatch) {
		return propertyRangeMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(SubPropertyChainMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(SubPropertyChainMatch2 conclusionMatch) {
		return subPropertyChainMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch2> getChildren(
			BackwardLinkMatch1 parent) {
		return backwardLinkMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch2> getChildren(
			ForwardLinkMatch1 parent) {
		return forwardLinkMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedDefinitionAxiomMatch2> getChildren(
			IndexedDefinitionAxiomMatch1 parent) {
		return indexedDefinitionAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomMatch2> getChildren(
			IndexedDisjointClassesAxiomMatch1 parent) {
		return indexedDisjointClassesAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedObjectPropertyRangeAxiomMatch2> getChildren(
			IndexedObjectPropertyRangeAxiomMatch1 parent) {
		return indexedObjectPropertyRangeAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedSubClassOfAxiomMatch2> getChildren(
			IndexedSubClassOfAxiomMatch1 parent) {
		return indexedSubClassOfAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedSubObjectPropertyOfAxiomMatch2> getChildren(
			IndexedSubObjectPropertyOfAxiomMatch1 parent) {
		return indexedSubObjectPropertyOfAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends PropagationMatch2> getChildren(
			PropagationMatch1 parent) {
		return propagationMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends PropagationMatch3> getChildren(
			PropagationMatch2 parent) {
		return propagationMatch3Map_.get(parent);
	}

	@Override
	public Iterable<? extends PropertyRangeMatch2> getChildren(
			PropertyRangeMatch1 parent) {
		return propertyRangeMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends SubPropertyChainMatch2> getChildren(
			SubPropertyChainMatch1 parent) {
		return subPropertyChainMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedMatch2> getChildren(
			SubClassInclusionDecomposedMatch1 parent) {
		return subClassInclusionDecomposedMatch2Map_.get(parent);
	}

	private final class ChildMap<T extends AbstractChild<?>>
			extends HashListMultimap<Object, T> {

		boolean add(T child) {
			return add(child.getParent(), child);
		}

	};

}
