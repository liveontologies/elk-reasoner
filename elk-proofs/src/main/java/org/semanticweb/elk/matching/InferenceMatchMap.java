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
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;

public interface InferenceMatchMap extends InferenceMap {

	Iterable<? extends BackwardLinkMatch1Watch> get(
			BackwardLinkMatch1 conclusion);

	Iterable<? extends DisjointSubsumerMatch1Watch> get(
			DisjointSubsumerMatch1 conclusion);

	Iterable<? extends ForwardLinkMatch1Watch> get(
			ForwardLinkMatch1 conclusion);
	
	Iterable<? extends ForwardLinkMatch2Watch> get(
			ForwardLinkMatch2 conclusion);

	Iterable<? extends IndexedDisjointClassesAxiomMatch1Watch> get(
			IndexedDisjointClassesAxiomMatch1 conclusion);

	Iterable<? extends IndexedEquivalentClassesAxiomMatch1Watch> get(
			IndexedEquivalentClassesAxiomMatch1 conclusion);

	Iterable<? extends IndexedObjectPropertyRangeAxiomMatch1Watch> get(
			IndexedObjectPropertyRangeAxiomMatch1 conclusion);

	Iterable<? extends IndexedSubClassOfAxiomMatch1Watch> get(
			IndexedSubClassOfAxiomMatch1 conclusion);

	Iterable<? extends IndexedSubObjectPropertyOfAxiomMatch1Watch> get(
			IndexedSubObjectPropertyOfAxiomMatch1 conclusion);

	Iterable<? extends PropertyRangeMatch1Watch> get(
			PropertyRangeMatch1 conclusion);

	Iterable<? extends SubClassInclusionDecomposedMatch1Watch> get(
			SubClassInclusionDecomposedMatch1 conclusion);

	Iterable<? extends SubPropertyChainMatch1Watch> get(
			SubPropertyChainMatch1 conclusion);
	
}
