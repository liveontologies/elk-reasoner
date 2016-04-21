package org.semanticweb.elk.matching.conclusions;

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

public interface ConclusionMatchHierarchy {

	Iterable<? extends BackwardLinkMatch2> getChildren(
			BackwardLinkMatch1 parent);

	Iterable<? extends ForwardLinkMatch2> getChildren(ForwardLinkMatch1 parent);

	Iterable<? extends IndexedEquivalentClassesAxiomMatch2> getChildren(
			IndexedEquivalentClassesAxiomMatch1 parent);

	Iterable<? extends IndexedDisjointClassesAxiomMatch2> getChildren(
			IndexedDisjointClassesAxiomMatch1 parent);

	Iterable<? extends IndexedObjectPropertyRangeAxiomMatch2> getChildren(
			IndexedObjectPropertyRangeAxiomMatch1 parent);

	Iterable<? extends IndexedSubClassOfAxiomMatch2> getChildren(
			IndexedSubClassOfAxiomMatch1 parent);

	Iterable<? extends IndexedSubObjectPropertyOfAxiomMatch2> getChildren(
			IndexedSubObjectPropertyOfAxiomMatch1 parent);

	Iterable<? extends PropagationMatch2> getChildren(PropagationMatch1 parent);

	Iterable<? extends PropagationMatch3> getChildren(PropagationMatch2 parent);

	Iterable<? extends PropertyRangeMatch2> getChildren(
			PropertyRangeMatch1 parent);

	Iterable<? extends SubPropertyChainMatch2> getChildren(
			SubPropertyChainMatch1 parent);

	Iterable<? extends SubClassInclusionDecomposedMatch2> getChildren(
			SubClassInclusionDecomposedMatch1 parent);

}
