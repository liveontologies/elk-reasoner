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

import org.semanticweb.elk.matching.conclusions.DisjointSubsumerMatch1;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerFromSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.DisjointSubsumerInference;

class DisjointSubsumerMatch1InferenceVisitor
		extends AbstractConclusionMatchInferenceVisitor<DisjointSubsumerMatch1>
		implements DisjointSubsumerInference.Visitor<Void> {

	DisjointSubsumerMatch1InferenceVisitor(InferenceMatch.Factory factory,
			DisjointSubsumerMatch1 child) {
		super(factory, child);
	}

	@Override
	public Void visit(DisjointSubsumerFromSubsumer inference) {
		factory.getDisjointSubsumerFromSubsumerMatch1(inference, child);
		return null;
	}

}
