/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences.visitors;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedConjunction;
import org.semanticweb.elk.reasoner.saturation.inferences.ComposedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromInconsistentDisjointnessAxiom;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromNegation;
import org.semanticweb.elk.reasoner.saturation.inferences.ContradictionFromOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedExistentialForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedFirstConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveBackwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedReflexiveForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.DecomposedSecondConjunct;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjunctionComposition;
import org.semanticweb.elk.reasoner.saturation.inferences.GeneratedPropagation;
import org.semanticweb.elk.reasoner.saturation.inferences.InitializationSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ObjectHasSelfPropertyRangeSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedContradiction;
import org.semanticweb.elk.reasoner.saturation.inferences.PropagatedSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ReflexiveSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.ReversedForwardLink;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassOfSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.SuperReversedForwardLink;

/**
 * A skeleton implementation of {@link ClassInferenceVisitor}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class AbstractClassInferenceVisitor<I, O> implements
		ClassInferenceVisitor<I, O> {

	protected abstract O defaultTracedVisit(ClassInference conclusion, I input);

	@Override
	public O visit(ComposedBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedConjunction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ComposedForwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionFromDisjointSubsumers conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionFromInconsistentDisjointnessAxiom conclusion,
			I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionFromNegation conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ContradictionFromOwlNothing conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedExistentialBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedExistentialForwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedFirstConjunct conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedReflexiveBackwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedReflexiveForwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DecomposedSecondConjunct conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DisjointSubsumerFromSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(DisjunctionComposition conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(GeneratedPropagation conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(InitializationSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ObjectHasSelfPropertyRangeSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(PropagatedContradiction conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(PropagatedSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReflexiveSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(ReversedForwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SubClassOfSubsumer conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

	@Override
	public O visit(SuperReversedForwardLink conclusion, I input) {
		return defaultTracedVisit(conclusion, input);
	}

}
