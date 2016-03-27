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

import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch1Watch;
import org.semanticweb.elk.matching.conclusions.PropertyRangeMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.matching.inferences.SubClassInclusionObjectHasSelfPropertyRangeMatch2;
import org.semanticweb.elk.matching.inferences.SubClassInclusionRangeMatch1;

class PropertyRangeMatch2InferenceVisitor
		extends
			AbstractConclusionMatchInferenceVisitor<PropertyRangeMatch2>
		implements
			PropertyRangeMatch1Watch.Visitor<Void> {

	PropertyRangeMatch2InferenceVisitor(InferenceMatch.Factory factory,
			PropertyRangeMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(
			SubClassInclusionObjectHasSelfPropertyRangeMatch2 inferenceMatch2) {
		factory.getSubClassInclusionObjectHasSelfPropertyRangeMatch3(
				inferenceMatch2, child);
		return null;
	}

	@Override
	public Void visit(SubClassInclusionRangeMatch1 inferenceMatch1) {
		factory.getSubClassInclusionRangeMatch2(inferenceMatch1, child);
		return null;
	}

}