/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link Contradiction} obtained from a {@link SubClassInclusionComposed} premise.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractContradictionOfSubsumerInference<S extends IndexedClassExpression>
		extends AbstractContradictionInference {

	private final S premiseSubsumer_;

	AbstractContradictionOfSubsumerInference(IndexedContextRoot root,
			S premiseSubsumer) {
		super(root);
		this.premiseSubsumer_ = premiseSubsumer;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubClassInclusionComposed getPremise(SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				premiseSubsumer_);
	}

	protected S getPremiseSubsumer() {
		return this.premiseSubsumer_;
	}

}
