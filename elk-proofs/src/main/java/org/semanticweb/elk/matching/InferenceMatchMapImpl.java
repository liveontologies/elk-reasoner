package org.semanticweb.elk.matching;

import java.util.Collection;

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
import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDefinitionAxiomMatch1Watch;
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
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDeclarationAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDefinitionAxiomInference;
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
import org.semanticweb.elk.reasoner.tracing.TracingInferenceSet;
import org.semanticweb.elk.util.collections.HashListMultimap;
import org.semanticweb.elk.util.collections.Multimap;

// TODO: avoid casts
@SuppressWarnings("unchecked")
class InferenceMatchMapImpl
		implements InferenceMap, InferenceMatchMap, InferenceMatchMapWriter {

	private final TracingInferenceSet inferences_;

	private final Multimap<ConclusionMatch, InferenceMatch> watchedInferences_ = new HashListMultimap<ConclusionMatch, InferenceMatch>();

	InferenceMatchMapImpl(TracingInferenceSet inferences) {
		this.inferences_ = inferences;
	}

	private Collection<? extends InferenceMatch> getWatchInferences(
			ConclusionMatch conclusion) {
		// need this method since some java compilers have problems for casting
		// directly
		return watchedInferences_.get(conclusion);
	}

	@Override
	public void add(BackwardLinkMatch1 conclusion,
			BackwardLinkMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(ForwardLinkMatch1 conclusion,
			ForwardLinkMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedDefinitionAxiomMatch1 conclusion,
			IndexedDefinitionAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedDisjointClassesAxiomMatch1 conclusion,
			IndexedDisjointClassesAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedObjectPropertyRangeAxiomMatch1 conclusion,
			IndexedObjectPropertyRangeAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(IndexedSubClassOfAxiomMatch1 conclusion,
			IndexedSubClassOfAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);
	}

	@Override
	public void add(IndexedSubObjectPropertyOfAxiomMatch1 conclusion,
			IndexedSubObjectPropertyOfAxiomMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(PropagationMatch1 conclusion,
			PropagationMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(PropagationMatch2 conclusion,
			PropagationMatch2Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(PropertyRangeMatch1 conclusion,
			PropertyRangeMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(SubClassInclusionDecomposedMatch1 conclusion,
			SubClassInclusionDecomposedMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public void add(SubPropertyChainMatch1 conclusion,
			SubPropertyChainMatch1Watch inference) {
		watchedInferences_.add(conclusion, inference);

	}

	@Override
	public Iterable<? extends BackwardLinkInference> get(
			BackwardLink conclusion) {
		return (Iterable<? extends BackwardLinkInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends BackwardLinkMatch1Watch> get(
			BackwardLinkMatch1 conclusion) {
		return (Iterable<? extends BackwardLinkMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkInference> get(
			ForwardLink conclusion) {
		return (Iterable<? extends ForwardLinkInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends ForwardLinkMatch1Watch> get(
			ForwardLinkMatch1 conclusion) {
		return (Collection<? extends ForwardLinkMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedDeclarationAxiomInference> get(
			IndexedDeclarationAxiom conclusion) {
		return (Iterable<? extends IndexedDeclarationAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedDefinitionAxiomInference> get(
			IndexedDefinitionAxiom conclusion) {
		return (Iterable<? extends IndexedDefinitionAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedDefinitionAxiomMatch1Watch> get(
			IndexedDefinitionAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedDefinitionAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomInference> get(
			IndexedDisjointClassesAxiom conclusion) {
		return (Iterable<? extends IndexedDisjointClassesAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedDisjointClassesAxiomMatch1Watch> get(
			IndexedDisjointClassesAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedDisjointClassesAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedObjectPropertyRangeAxiomInference> get(
			IndexedObjectPropertyRangeAxiom conclusion) {
		return (Iterable<? extends IndexedObjectPropertyRangeAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedObjectPropertyRangeAxiomMatch1Watch> get(
			IndexedObjectPropertyRangeAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedObjectPropertyRangeAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubClassOfAxiomInference> get(
			IndexedSubClassOfAxiom conclusion) {
		return (Iterable<? extends IndexedSubClassOfAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubClassOfAxiomMatch1Watch> get(
			IndexedSubClassOfAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedSubClassOfAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubObjectPropertyOfAxiomInference> get(
			IndexedSubObjectPropertyOfAxiom conclusion) {
		return (Iterable<? extends IndexedSubObjectPropertyOfAxiomInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends IndexedSubObjectPropertyOfAxiomMatch1Watch> get(
			IndexedSubObjectPropertyOfAxiomMatch1 conclusion) {
		return (Collection<? extends IndexedSubObjectPropertyOfAxiomMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends PropagationInference> get(
			Propagation conclusion) {
		return (Iterable<? extends PropagationInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends PropagationMatch1Watch> get(
			PropagationMatch1 conclusion) {
		return (Collection<? extends PropagationMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends PropagationMatch2Watch> get(
			PropagationMatch2 conclusion) {
		return (Collection<? extends PropagationMatch2Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends PropertyRangeInference> get(
			PropertyRange conclusion) {
		return (Iterable<? extends PropertyRangeInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends PropertyRangeMatch1Watch> get(
			PropertyRangeMatch1 conclusion) {
		return (Collection<? extends PropertyRangeMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionComposedInference> get(
			SubClassInclusionComposed conclusion) {
		return (Iterable<? extends SubClassInclusionComposedInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedInference> get(
			SubClassInclusionDecomposed conclusion) {
		return (Iterable<? extends SubClassInclusionDecomposedInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubClassInclusionDecomposedMatch1Watch> get(
			SubClassInclusionDecomposedMatch1 conclusion) {
		return (Collection<? extends SubClassInclusionDecomposedMatch1Watch>) getWatchInferences(
				conclusion);
	}

	@Override
	public Iterable<? extends SubPropertyChainInference> get(
			SubPropertyChain conclusion) {
		return (Iterable<? extends SubPropertyChainInference>) inferences_
				.getInferences(conclusion);
	}

	@Override
	public Iterable<? extends SubPropertyChainMatch1Watch> get(
			SubPropertyChainMatch1 conclusion) {
		return (Collection<? extends SubPropertyChainMatch1Watch>) getWatchInferences(
				conclusion);
	}

}
