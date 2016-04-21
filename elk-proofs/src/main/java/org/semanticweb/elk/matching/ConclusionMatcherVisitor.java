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
import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ForwardLinkMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedDisjointClassesAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedObjectPropertyRangeAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubClassOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedSubObjectPropertyOfAxiomMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1;
import org.semanticweb.elk.matching.conclusions.PropagationMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2;
import org.semanticweb.elk.matching.conclusions.PropagationMatch2Watch;
import org.semanticweb.elk.matching.conclusions.PropagationMatch3;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionDecomposedMatch2;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubPropertyChainMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEquivalentClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectPropertyRangeAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubClassOfAxiomInference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedSubObjectPropertyOfAxiomInference;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkInference;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.PropertyRangeInference;
import org.semanticweb.elk.reasoner.saturation.properties.inferences.SubPropertyChainInference;

public class ConclusionMatcherVisitor implements ConclusionMatch.Visitor<Void> {

	private final InferenceMatch.Factory inferenceFactory_;

	private final InferenceMatchMap inferences_;

	ConclusionMatcherVisitor(InferenceMatch.Factory inferenceFactory,
			InferenceMatchMap inferences) {
		this.inferenceFactory_ = inferenceFactory;
		this.inferences_ = inferences;
	}

	@Override
	public Void visit(final BackwardLinkMatch1 conclusionMatch) {
		for (BackwardLinkInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new BackwardLinkMatch1InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(BackwardLinkMatch2 conclusionMatch) {
		for (BackwardLinkMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new BackwardLinkMatch2InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkMatch1 conclusionMatch) {
		for (ForwardLinkInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new ForwardLinkMatch1InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(ForwardLinkMatch2 conclusionMatch) {
		for (ForwardLinkMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new ForwardLinkMatch2InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedEquivalentClassesAxiomMatch1 conclusionMatch) {
		for (IndexedEquivalentClassesAxiomInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new IndexedEquivalentClassesAxiomMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedEquivalentClassesAxiomMatch2 conclusionMatch) {
		for (IndexedEquivalentClassesAxiomMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedDisjointClassesAxiomMatch1 conclusionMatch) {
		for (IndexedDisjointClassesAxiomInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new IndexedDisjointClassesAxiomMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedDisjointClassesAxiomMatch2 conclusionMatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(IndexedObjectPropertyRangeAxiomMatch1 conclusionMatch) {
		for (IndexedObjectPropertyRangeAxiomInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(
					new IndexedObjectPropertyRangeAxiomMatch1InferenceVisitor(
							inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedObjectPropertyRangeAxiomMatch2 conclusionMatch) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(IndexedSubClassOfAxiomMatch1 conclusionMatch) {
		for (IndexedSubClassOfAxiomInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new IndexedSubClassOfAxiomMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedSubClassOfAxiomMatch2 conclusionMatch) {
		for (IndexedSubClassOfAxiomMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new IndexedSubClassOfAxiomMatch2InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedSubObjectPropertyOfAxiomMatch1 conclusionMatch) {
		for (IndexedSubObjectPropertyOfAxiomInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(
					new IndexedSubObjectPropertyOfAxiomMatch1InferenceVisitor(
							inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(IndexedSubObjectPropertyOfAxiomMatch2 conclusionMatch) {
		for (IndexedSubObjectPropertyOfAxiomMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(
					new IndexedSubObjectPropertyOfAxiomMatch2InferenceVisitor(
							inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(PropagationMatch1 conclusionMatch) {
		for (PropagationInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new PropagationMatch1InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(PropagationMatch2 conclusionMatch) {
		for (PropagationMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new PropagationMatch2InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(PropagationMatch3 conclusionMatch) {
		for (PropagationMatch2Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new PropagationMatch3InferenceVisitor(inferenceFactory_,
					conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(PropertyRangeMatch1 conclusionMatch) {
		for (PropertyRangeInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new PropertyRangeMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(PropertyRangeMatch2 conclusionMatch) {
		for (PropertyRangeMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new PropertyRangeMatch2InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(final SubClassInclusionComposedMatch1 conclusionMatch) {
		for (SubClassInclusionComposedInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new SubClassInclusionComposedMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(final SubClassInclusionDecomposedMatch1 conclusionMatch) {
		for (SubClassInclusionDecomposedInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new SubClassInclusionDecomposedMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(SubClassInclusionDecomposedMatch2 conclusionMatch) {
		for (SubClassInclusionDecomposedMatch1Watch inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new SubClassInclusionDecomposedMatch2InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(final SubPropertyChainMatch1 conclusionMatch) {
		for (SubPropertyChainInference inf : inferences_
				.get(conclusionMatch.getParent())) {
			inf.accept(new SubPropertyChainMatch1InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

	@Override
	public Void visit(SubPropertyChainMatch2 conclusionMatch) {
		for (SubPropertyChainMatch1Watch inferenceMatch : inferences_
				.get(conclusionMatch.getParent())) {
			inferenceMatch.accept(new SubPropertyChainMatch2InferenceVisitor(
					inferenceFactory_, conclusionMatch));
		}
		return null;
	}

}
