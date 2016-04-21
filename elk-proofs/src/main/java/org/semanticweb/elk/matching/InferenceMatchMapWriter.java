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
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;

public interface InferenceMatchMapWriter {

	void add(BackwardLinkMatch1 conclusion, BackwardLinkMatch1Watch inference);

	void add(ForwardLinkMatch1 conclusion, ForwardLinkMatch1Watch inference);

	void add(IndexedEquivalentClassesAxiomMatch1 conclusion,
			IndexedEquivalentClassesAxiomMatch1Watch inference);

	void add(IndexedDisjointClassesAxiomMatch1 conclusion,
			IndexedDisjointClassesAxiomMatch1Watch inference);

	void add(IndexedObjectPropertyRangeAxiomMatch1 conclusion,
			IndexedObjectPropertyRangeAxiomMatch1Watch inference);

	void add(IndexedSubObjectPropertyOfAxiomMatch1 conclusion,
			IndexedSubObjectPropertyOfAxiomMatch1Watch inference);

	void add(IndexedSubClassOfAxiomMatch1 conclusion,
			IndexedSubClassOfAxiomMatch1Watch inference);

	void add(PropagationMatch1 conclusion, PropagationMatch1Watch inference);

	void add(PropagationMatch2 conclusion, PropagationMatch2Watch inference);

	void add(PropertyRangeMatch1 conclusion,
			PropertyRangeMatch1Watch inference);

	void add(SubClassInclusionDecomposedMatch1 conclusion,
			SubClassInclusionDecomposedMatch1Watch inference);

	void add(SubPropertyChainMatch1 conclusion,
			SubPropertyChainMatch1Watch inference);

}
