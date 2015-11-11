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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ComposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Subsumer;

/**
 * The base implementation of a {@link Contradiction} inference produced when
 * processing a {@link Subsumer}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
abstract class AbstractContradictionFromSubsumerInference<S extends IndexedClassExpression>
		extends AbstractContradictionInference {

	private final S premiseSubsumer_;

	AbstractContradictionFromSubsumerInference(IndexedContextRoot root,
			S premiseSubsumer) {
		super(root);
		this.premiseSubsumer_ = premiseSubsumer;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public ComposedSubsumer getPremise(ComposedSubsumer.Factory factory) {
		return factory.getComposedSubsumer(getInferenceRoot(),
				premiseSubsumer_);
	}

	protected S getPremiseSubsumer() {
		return this.premiseSubsumer_;
	}

}
