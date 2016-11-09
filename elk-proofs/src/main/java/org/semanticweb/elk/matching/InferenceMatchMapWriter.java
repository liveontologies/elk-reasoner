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
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.BackwardLinkMatch3Watch;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1Watch;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch3Watch;
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
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;

public interface InferenceMatchMapWriter {

	void add(BackwardLinkMatch1 conclusion, BackwardLinkMatch1Watch inference);

	void add(BackwardLinkMatch2 conclusion, BackwardLinkMatch2Watch inference);

	void add(BackwardLinkMatch3 conclusion, BackwardLinkMatch3Watch inference);

	void add(ClassInconsistencyMatch1 conclusion,
			ClassInconsistencyMatch1Watch inference);

	void add(DisjointSubsumerMatch1 conclusion,
			DisjointSubsumerMatch1Watch inference);

	void add(ForwardLinkMatch1 conclusion, ForwardLinkMatch1Watch inference);

	void add(ForwardLinkMatch2 conclusion, ForwardLinkMatch2Watch inference);

	void add(ForwardLinkMatch3 conclusion, ForwardLinkMatch3Watch inference);

	void add(IndexedDisjointClassesAxiomMatch1 conclusion,
			IndexedDisjointClassesAxiomMatch1Watch inference);

	void add(IndexedEquivalentClassesAxiomMatch1 conclusion,
			IndexedEquivalentClassesAxiomMatch1Watch inference);

	void add(IndexedObjectPropertyRangeAxiomMatch1 conclusion,
			IndexedObjectPropertyRangeAxiomMatch1Watch inference);

	void add(IndexedSubClassOfAxiomMatch1 conclusion,
			IndexedSubClassOfAxiomMatch1Watch inference);

	void add(IndexedSubObjectPropertyOfAxiomMatch1 conclusion,
			IndexedSubObjectPropertyOfAxiomMatch1Watch inference);

	void add(PropagationMatch1 conclusion, PropagationMatch1Watch inference);

	void add(PropertyRangeMatch1 conclusion,
			PropertyRangeMatch1Watch inference);

	void add(SubClassInclusionComposedMatch1 conclusion,
			SubClassInclusionComposedMatch1Watch inference);

	void add(SubClassInclusionDecomposedMatch1 conclusion,
			SubClassInclusionDecomposedMatch1Watch inference);

	void add(SubPropertyChainMatch1 conclusion,
			SubPropertyChainMatch1Watch inference);

}
