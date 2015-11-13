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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.ComposedSubClassInclusionInferenceVisitor;

/**
 * A {@link SubClassInclusionComposed} with {@link IndexedClassEntity} super-class
 * obtained from the corresponding {@link SubClassInclusionDecomposed}.
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class SubClassInclusionComposedEntity extends
		AbstractSubClassInclusionComposedInference<IndexedClassEntity> {

	/**
	 */
	public SubClassInclusionComposedEntity(IndexedContextRoot inferenceRoot,
			IndexedClassEntity subsumer) {
		super(inferenceRoot, subsumer);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return getConclusionRoot();
	}

	public SubClassInclusionDecomposed getPremise(SubClassInclusionDecomposed.Factory factory) {
		return factory.getDecomposedSubClassInclusion(getInferenceRoot(), getSuperExpression());
	}

	@Override
	public String toString() {
		return super.toString() + " (- => +)";
	}

	@Override
	public <I, O> O accept(ComposedSubClassInclusionInferenceVisitor<I, O> visitor,
			I input) {
		return visitor.visit(this, input);
	}

}
