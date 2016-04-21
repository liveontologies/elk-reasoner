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

import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch1Watch;
import org.semanticweb.elk.matching.conclusions.IndexedEquivalentClassesAxiomMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionComposedDefinedClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedDefinitionMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedFirstEquivalentClassMatch1;
import org.semanticweb.elk.matching.inferences.SubClassInclusionExpandedSecondEquivalentClassMatch1;

class IndexedEquivalentClassesAxiomMatch2InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<IndexedEquivalentClassesAxiomMatch2>
		implements IndexedEquivalentClassesAxiomMatch1Watch.Visitor<Void> {

	IndexedEquivalentClassesAxiomMatch2InferenceVisitor(
			InferenceMatch.Factory factory,
			IndexedEquivalentClassesAxiomMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(
			SubClassInclusionComposedDefinedClassMatch1 inferenceMatch1) {
		factory.getSubClassInclusionComposedDefinedClassMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedDefinitionMatch1 inferenceMatch1) {
		factory.getSubClassInclusionExpandedDefinitionMatch2(inferenceMatch1,
				child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedFirstEquivalentClassMatch1 inferenceMatch1) {
		factory.getSubClassInclusionExpandedFirstEquivalentClassMatch2(
				inferenceMatch1, child);
		return null;
	}

	@Override
	public Void visit(
			SubClassInclusionExpandedSecondEquivalentClassMatch1 inferenceMatch1) {
		factory.getSubClassInclusionExpandedSecondEquivalentClassMatch2(
				inferenceMatch1, child);
		return null;
	}

}