/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

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
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Subsumer;

/**
 * The base implementation of a {@link Contradiction} inference produced when
 * processing a {@link Subsumer}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
abstract class ContradictionFromSubsumer<S extends IndexedClassExpression>
		extends ContradictionImpl implements ClassInference {

	private final S premiseSubsumer_;

	ContradictionFromSubsumer(IndexedContextRoot inferenceRoot,
			S premiseSubsumer) {
		super(inferenceRoot);
		this.premiseSubsumer_ = premiseSubsumer;
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public Subsumer<S> getPremise() {
		return new DecomposedSubsumerImpl<S>(getInferenceRoot(),
				premiseSubsumer_);
	}

	protected S getPremiseSubsumer() {
		return this.premiseSubsumer_;
	}

}
