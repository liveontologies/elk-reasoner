package org.semanticweb.elk.matching.inferences;

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

import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.SubClassInclusionComposedMatch1;
import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

public abstract class AbstractSubClassInclusionComposedCanonizerMatch1
		extends AbstractInferenceMatch<SubClassInclusionComposed> {

	private final IndexedContextRootMatch destinationMatch_;

	AbstractSubClassInclusionComposedCanonizerMatch1(
			SubClassInclusionComposed parent,
			IndexedContextRootMatch destinationMatch) {
		super(parent);
		this.destinationMatch_ = destinationMatch;
	}

	IndexedContextRootMatch getDestinationMatch() {
		return destinationMatch_;
	}

	public abstract SubClassInclusionComposedMatch1 getPremiseMatch(
			ConclusionMatchExpressionFactory factory);

	abstract SubClassInclusionComposedMatch1 getConclusionMatch(
			ConclusionMatchExpressionFactory factory);

}
