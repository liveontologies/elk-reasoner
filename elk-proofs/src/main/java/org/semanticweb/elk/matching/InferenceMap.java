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

import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainInference;

public interface InferenceMap {

	Iterable<? extends BackwardLinkInference> get(BackwardLink conclusion);

	Iterable<? extends ForwardLinkInference> get(ForwardLink conclusion);

	Iterable<? extends IndexedDeclarationAxiomInference> get(
			IndexedDeclarationAxiom conclusion);

	Iterable<? extends IndexedEquivalentClassesAxiomInference> get(
			IndexedEquivalentClassesAxiom conclusion);

	Iterable<? extends IndexedDisjointClassesAxiomInference> get(
			IndexedDisjointClassesAxiom conclusion);

	Iterable<? extends IndexedObjectPropertyRangeAxiomInference> get(
			IndexedObjectPropertyRangeAxiom conclusion);

	Iterable<? extends IndexedSubClassOfAxiomInference> get(
			IndexedSubClassOfAxiom conclusion);

	Iterable<? extends IndexedSubObjectPropertyOfAxiomInference> get(
			IndexedSubObjectPropertyOfAxiom conclusion);

	Iterable<? extends PropagationInference> get(Propagation conclusion);

	Iterable<? extends PropertyRangeInference> get(PropertyRange conclusion);

	Iterable<? extends SubClassInclusionComposedInference> get(
			SubClassInclusionComposed conclusion);

	Iterable<? extends SubClassInclusionDecomposedInference> get(
			SubClassInclusionDecomposed conclusion);

	Iterable<? extends SubPropertyChainInference> get(
			SubPropertyChain conclusion);

}
