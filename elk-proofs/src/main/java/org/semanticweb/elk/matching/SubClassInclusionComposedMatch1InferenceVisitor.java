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

import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedDefinedClass;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedOfDecomposed;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedInference;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionComposedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.inferences.SubClassInclusionOwlThing;

class SubClassInclusionComposedMatch1InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<SubClassInclusionComposedMatch1>
		implements SubClassInclusionComposedInference.Visitor<Void> {

	SubClassInclusionComposedMatch1InferenceVisitor(
			InferenceMatch.Factory factory,
			SubClassInclusionComposedMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(SubClassInclusionComposedDefinedClass inference) {
		factory.getSubClassInclusionComposedDefinedClassMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedOfDecomposed inference) {
		factory.getSubClassInclusionComposedOfDecomposedMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedObjectIntersectionOf inference) {
		factory.getSubClassInclusionComposedObjectIntersectionOfMatch1(
				inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedObjectSomeValuesFrom inference) {
		factory.getSubClassInclusionComposedObjectSomeValuesFromMatch1(
				inference, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionComposedObjectUnionOf inference) {
		factory.getSubClassInclusionComposedObjectUnionOfMatch1(inference,
				child);
		return null;
	}
	
	@Override
	public Void visit(SubClassInclusionOwlThing inference) {
		factory.getSubClassInclusionOwlThingMatch1(inference, child);
		return null;
	}


}
