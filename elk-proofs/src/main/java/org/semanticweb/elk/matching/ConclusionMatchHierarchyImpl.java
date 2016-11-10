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
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch4;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch2;
import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchHierarchy;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch4;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.util.collections.HashListMultimap;

public class ConclusionMatchHierarchyImpl
		implements ConclusionMatchHierarchy, ConclusionMatch.Visitor<Boolean> {

	private final class ChildMap<T extends AbstractChild<?>>
			extends HashListMultimap<Object, T> {

		boolean add(T child) {
			return add(child.getParent(), child);
		}

	}

	private final ChildMap<BackwardLinkMatch2> backwardLinkMatch2Map_ = new ChildMap<BackwardLinkMatch2>();

	private final ChildMap<BackwardLinkMatch3> backwardLinkMatch3Map_ = new ChildMap<BackwardLinkMatch3>();

	private final ChildMap<BackwardLinkMatch4> backwardLinkMatch4Map_ = new ChildMap<BackwardLinkMatch4>();

	private final ChildMap<ClassInconsistencyMatch2> classInconsistencyMatch2Map_ = new ChildMap<ClassInconsistencyMatch2>();

	private final ChildMap<DisjointSubsumerMatch2> disjointSubsumerMatch2Map_ = new ChildMap<DisjointSubsumerMatch2>();

	private final ChildMap<ForwardLinkMatch2> forwardLinkMatch2Map_ = new ChildMap<ForwardLinkMatch2>();

	private final ChildMap<ForwardLinkMatch3> forwardLinkMatch3Map_ = new ChildMap<ForwardLinkMatch3>();

	private final ChildMap<ForwardLinkMatch4> forwardLinkMatch4Map_ = new ChildMap<ForwardLinkMatch4>();

	private final ChildMap<IndexedEquivalentClassesAxiomMatch2> indexedDefinitionAxiomMatch2Map_ = new ChildMap<IndexedEquivalentClassesAxiomMatch2>();

	private final ChildMap<IndexedDisjointClassesAxiomMatch2> indexedDisjointClassesAxiomMatch2Map_ = new ChildMap<IndexedDisjointClassesAxiomMatch2>();

	private final ChildMap<IndexedObjectPropertyRangeAxiomMatch2> indexedObjectPropertyRangeAxiomMatch2Map_ = new ChildMap<IndexedObjectPropertyRangeAxiomMatch2>();

	private final ChildMap<IndexedSubClassOfAxiomMatch2> indexedSubClassOfAxiomMatch2Map_ = new ChildMap<IndexedSubClassOfAxiomMatch2>();

	private final ChildMap<IndexedSubObjectPropertyOfAxiomMatch2> indexedSubObjectPropertyOfAxiomMatch2Map_ = new ChildMap<IndexedSubObjectPropertyOfAxiomMatch2>();

	private final ChildMap<PropagationMatch2> propagationMatch2Map_ = new ChildMap<PropagationMatch2>();

	private final ChildMap<PropertyRangeMatch2> propertyRangeMatch2Map_ = new ChildMap<PropertyRangeMatch2>();

	private final ChildMap<SubClassInclusionComposedMatch2> subClassInclusionComposedMatch2Map_ = new ChildMap<SubClassInclusionComposedMatch2>();

	private final ChildMap<SubClassInclusionDecomposedMatch2> subClassInclusionDecomposedMatch2Map_ = new ChildMap<SubClassInclusionDecomposedMatch2>();

	private final ChildMap<SubPropertyChainMatch2> subPropertyChainMatch2Map_ = new ChildMap<SubPropertyChainMatch2>();

	@Override
	public Iterable<? extends BackwardLinkMatch2> getChildren(
			BackwardLinkMatch1 parent) {
		return backwardLinkMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch3> getChildren(
			BackwardLinkMatch2 parent) {
		return backwardLinkMatch3Map_.get(parent);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch4> getChildren(
			BackwardLinkMatch3 parent) {
		return backwardLinkMatch4Map_.get(parent);
	}

	@Override
	public Iterable<? extends ClassInconsistencyMatch2> getChildren(
			ClassInconsistencyMatch1 parent) {
		return classInconsistencyMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends DisjointSubsumerMatch2> getChildren(
			DisjointSubsumerMatch1 parent) {
		return disjointSubsumerMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch2> getChildren(
			ForwardLinkMatch1 parent) {
		return forwardLinkMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch3> getChildren(
			ForwardLinkMatch2 parent) {
		return forwardLinkMatch3Map_.get(parent);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch4> getChildren(
			ForwardLinkMatch3 parent) {
		return forwardLinkMatch4Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomMatch2> getChildren(
			IndexedDisjointClassesAxiomMatch1 parent) {
		return indexedDisjointClassesAxiomMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends IndexedEquivalentClassesAxiomMatch2> getChildren(
			IndexedEquivalentClassesAxiomMatch1 parent) {
		return indexedDefinitionAxiomMatch2Map_.get(parent);
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
	public Iterable<? extends PropertyRangeMatch2> getChildren(
			PropertyRangeMatch1 parent) {
		return propertyRangeMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends SubClassInclusionComposedMatch2> getChildren(
			SubClassInclusionComposedMatch1 parent) {
		return subClassInclusionComposedMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedMatch2> getChildren(
			SubClassInclusionDecomposedMatch1 parent) {
		return subClassInclusionDecomposedMatch2Map_.get(parent);
	}

	@Override
	public Iterable<? extends SubPropertyChainMatch2> getChildren(
			SubPropertyChainMatch1 parent) {
		return subPropertyChainMatch2Map_.get(parent);
	}

	@Override
	public Boolean visit(BackwardLinkMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(BackwardLinkMatch2 conclusionMatch) {
		return backwardLinkMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(BackwardLinkMatch3 conclusionMatch) {
		return backwardLinkMatch3Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(BackwardLinkMatch4 conclusionMatch) {
		return backwardLinkMatch4Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(ClassInconsistencyMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(ClassInconsistencyMatch2 conclusionMatch) {
		return classInconsistencyMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(DisjointSubsumerMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(DisjointSubsumerMatch2 conclusionMatch) {
		return disjointSubsumerMatch2Map_.add(conclusionMatch);
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
	public Boolean visit(ForwardLinkMatch3 conclusionMatch) {
		return forwardLinkMatch3Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(ForwardLinkMatch4 conclusionMatch) {
		return forwardLinkMatch4Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		return indexedDisjointClassesAxiomMatch2Map_.add(conclusionMatch);
	}

	@Override
	public Boolean visit(IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedEquivalentClassesAxiomMatch2 conclusionMatch) {
		return indexedDefinitionAxiomMatch2Map_.add(conclusionMatch);
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
	public Boolean visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		return indexedSubClassOfAxiomMatch2Map_.add(conclusionMatch);
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
	public Boolean visit(PropagationMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(PropagationMatch2 conclusionMatch) {
		return propagationMatch2Map_.add(conclusionMatch);
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
	public Boolean visit(SubClassInclusionComposedMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(SubClassInclusionComposedMatch2 conclusionMatch) {
		return subClassInclusionComposedMatch2Map_.add(conclusionMatch);
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
	public Boolean visit(SubPropertyChainMatch1 conclusionMatch) {
		return false;
	}

	@Override
	public Boolean visit(SubPropertyChainMatch2 conclusionMatch) {
		return subPropertyChainMatch2Map_.add(conclusionMatch);
	};

}
