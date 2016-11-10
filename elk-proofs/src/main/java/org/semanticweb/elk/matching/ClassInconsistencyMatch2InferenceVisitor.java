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

import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch1Watch;
import org.semanticweb.elk.matching.conclusions.ClassInconsistencyMatch2;
import org.semanticweb.elk.matching.inferences.ClassInconsistencyPropagatedMatch2;
import org.semanticweb.elk.matching.inferences.InferenceMatch.Factory;

class ClassInconsistencyMatch2InferenceVisitor extends
		AbstractConclusionMatchInferenceVisitor<ClassInconsistencyMatch2>
		implements ClassInconsistencyMatch1Watch.Visitor<Void> {

	ClassInconsistencyMatch2InferenceVisitor(Factory factory,
			ClassInconsistencyMatch2 child) {
		super(factory, child);
	}

	@Override
	public Void visit(ClassInconsistencyPropagatedMatch2 inferenceMatch2) {
		factory.getClassInconsistencyPropagatedMatch3(inferenceMatch2, child);
		return null;
	}

}
