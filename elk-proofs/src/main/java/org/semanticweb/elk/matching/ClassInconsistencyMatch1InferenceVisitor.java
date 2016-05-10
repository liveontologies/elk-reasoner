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

import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfDisjointSubsumers;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfObjectComplementOf;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyOfOwlNothing;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInconsistencyPropagated;

class ClassInconsistencyMatch1InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<ClassInconsistencyMatch1>
		implements ClassInconsistencyInference.Visitor<Void> {

	ClassInconsistencyMatch1InferenceVisitor(InferenceMatch.Factory factory,
			ClassInconsistencyMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(ClassInconsistencyOfOwlNothing inference) {
		factory.getClassInconsistencyOfOwlNothingMatch1(inference, child);
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyOfDisjointSubsumers inference) {
		factory.getClassInconsistencyOfDisjointSubsumersMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyOfObjectComplementOf inference) {
		factory.getClassInconsistencyOfObjectComplementOfMatch1(inference,
				child);
		return null;
	}

	@Override
	public Void visit(ClassInconsistencyPropagated inference) {
		factory.getClassInconsistencyPropagatedMatch1(inference, child);
		return null;
	}

}
