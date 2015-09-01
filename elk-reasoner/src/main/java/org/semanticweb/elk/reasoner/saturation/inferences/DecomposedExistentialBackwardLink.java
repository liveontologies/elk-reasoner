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
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.DecomposedSubsumerImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.DecomposedSubsumer;
import org.semanticweb.elk.reasoner.saturation.inferences.visitors.BackwardLinkInferenceVisitor;

/**
 * Represents a decomposition of {@link IndexedObjectSomeValuesFrom} which
 * creates a {@link BackwardLink}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class DecomposedExistentialBackwardLink extends
		AbstractBackwardLinkInference {

	private final IndexedContextRoot inferenceRoot_;

	private final IndexedObjectSomeValuesFrom existential_;

	public DecomposedExistentialBackwardLink(IndexedContextRoot inferenceRoot,
			IndexedObjectSomeValuesFrom subsumer) {
		super(IndexedObjectSomeValuesFrom.Helper.getTarget(subsumer), subsumer
				.getProperty(), inferenceRoot);
		existential_ = subsumer;
		inferenceRoot_ = inferenceRoot;
	}

	public IndexedObjectSomeValuesFrom getDecomposedExistential() {
		return this.existential_;
	}

	public DecomposedSubsumer getPremise() {
		return new DecomposedSubsumerImpl(getInferenceRoot(), existential_);
	}

	@Override
	public IndexedContextRoot getInferenceRoot() {
		return inferenceRoot_;
	}

	@Override
	public String toString() {
		return super.toString() + " (decomposition)";
	}

	@Override
	public <I, O> O accept(BackwardLinkInferenceVisitor<I, O> visitor, I input) {
		return visitor.visit(this, input);
	}

}
