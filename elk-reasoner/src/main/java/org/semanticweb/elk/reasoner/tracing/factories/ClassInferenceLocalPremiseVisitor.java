/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing.factories;

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

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversed;
import org.semanticweb.elk.reasoner.saturation.inferences.BackwardLinkReversedExpanded;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyPropagated;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectHasSelf;
import org.semanticweb.elk.reasoner.saturation.inferences.ForwardLinkOfObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagationGenerated;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedEntity;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionDecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedDefinition;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionExpandedSubClassOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionObjectHasSelfPropertyRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionRange;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionTautology;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;

/**
 * A {@link ClassInference.Visitor} that visits only local premises of the
 * {@link ClassInference}s. A {@link ClassConclusion} premise of a
 * {@link ClassInference} is local if {@link ClassConclusion#getTraceRoot()} =
 * {@link ClassInference#getTraceRoot()}.
 * 
 * @author Yevgeny Kazakov
 *
 * @param <O>
 *            the type of the output of the visitor (all methods return
 *            {@code null})
 */
class ClassInferenceLocalPremiseVisitor<O>
		implements ClassInference.Visitor<O> {

	private final ClassConclusion.Factory conclusionFactory_;

	private final ClassConclusion.Visitor<?> conclusionVisitor_;

	ClassInferenceLocalPremiseVisitor(ClassConclusion.Factory conclusionFactory,
			ClassConclusion.Visitor<?> conclusionVisitor) {
		this.conclusionFactory_ = conclusionFactory;
		this.conclusionVisitor_ = conclusionVisitor;
	}

	@Override
	public O visit(BackwardLinkComposition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		if (inference.getOrigin() == inference.getConclusionSource()) {
			conclusionVisitor_
					.visit(inference.getThirdPremise(conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectHasSelf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(BackwardLinkOfObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;

	}

	@Override
	public O visit(BackwardLinkReversed inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(BackwardLinkReversedExpanded inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ContextInitializationNoPremises inference) {
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfDisjointSubsumers inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfObjectComplementOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyOfOwlNothing inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ClassInconsistencyPropagated inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		if (inference.getOrigin() == inference.getDestination()) {
			conclusionVisitor_
					.visit(inference.getSecondPremise(conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ForwardLinkComposition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		if (inference.getOrigin() == inference.getDestination()) {
			conclusionVisitor_
					.visit(inference.getThirdPremise(conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectHasSelf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(ForwardLinkOfObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(PropagationGenerated inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedDefinedClass inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedEntity inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		conclusionVisitor_
				.visit(inference.getSecondPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		if (inference.getOrigin() == inference.getDestination()) {
			conclusionVisitor_
					.visit(inference.getSecondPremise(conclusionFactory_));
		}
		return null;
	}

	@Override
	public O visit(SubClassInclusionComposedObjectUnionOf inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedFirstConjunct inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionDecomposedSecondConjunct inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedDefinition inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionExpandedSubClassOf inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionObjectHasSelfPropertyRange inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionOwlThing inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionRange inference) {
		conclusionVisitor_.visit(inference.getFirstPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubClassInclusionTautology inference) {
		conclusionVisitor_.visit(inference.getPremise(conclusionFactory_));
		return null;
	}

	@Override
	public O visit(SubContextInitializationNoPremises inference) {
		return null;
	}

}
