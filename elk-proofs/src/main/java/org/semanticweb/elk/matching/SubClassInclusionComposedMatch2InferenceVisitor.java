package org.semanticweb.elk.matching;

/*-
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

import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1Watch;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyOfObjectComplementOfMatch2;
import org.semanticweb.elk.matching.inferences.DisjointSubsumerFromSubsumerMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch.Factory;
import org.semanticweb.elk.matching.inferences.PropagationGeneratedMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEmptyObjectIntersectionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEmptyObjectOneOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedEmptyObjectUnionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectHasValueMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectIntersectionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectIntersectionOfMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedObjectUnionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedSingletonObjectIntersectionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedSingletonObjectOneOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedSingletonObjectUnionOfMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedFirstEquivalentClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSecondEquivalentClassMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSubClassOfMatch2;

class SubClassInclusionComposedMatch2InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<SubClassInclusionComposedMatch2>
		implements SubClassInclusionComposedMatch1Watch.Visitor<Void> {

	SubClassInclusionComposedMatch2InferenceVisitor(Factory factory,
			SubClassInclusionComposedMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(
			ClassInconsistencyOfObjectComplementOfMatch2 inferenceMatch2) {
		factory.getClassInconsistencyOfObjectComplementOfMatch3(inferenceMatch2,
				child);
		return null;
	}

	@Override
	public Void visit(DisjointSubsumerFromSubsumerMatch2 inferenceMatch2) {
		factory.getDisjointSubsumerFromSubsumerMatch3(inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(PropagationGeneratedMatch1 inferenceMatch1) {
		factory.getPropagationGeneratedMatch2(inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedDefinedClassMatch2 inferenceMatch2) {
		factory.getSubClassInclusionComposedDefinedClassMatch3(inferenceMatch2,
				child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectIntersectionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedEmptyObjectIntersectionOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectOneOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedEmptyObjectOneOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedEmptyObjectUnionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedEmptyObjectUnionOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectHasValueMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedObjectHasValueMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedObjectIntersectionOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectIntersectionOfMatch2 inferenceMatch2) {
		factory.getSubClassInclusionComposedObjectIntersectionOfMatch3(
				inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedObjectUnionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedObjectUnionOfMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectIntersectionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedSingletonObjectIntersectionOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectOneOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedSingletonObjectOneOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionComposedSingletonObjectUnionOfMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedSingletonObjectUnionOfMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedFirstEquivalentClassMatch2 inferenceMatch2) {
		factory.getSubClassInclusionExpandedFirstEquivalentClassMatch3(
				inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSecondEquivalentClassMatch2 inferenceMatch2) {
		factory.getSubClassInclusionExpandedSecondEquivalentClassMatch3(
				inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSubClassOfMatch2 inferenceMatch2) {
		factory.getSubClassInclusionExpandedSubClassOfMatch3(inferenceMatch2,
				child);
		return null;
	}

}
