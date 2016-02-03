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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * {@link SubClassInclusionDecomposed} representing a tautology
 * {@code C ⊑ owl:Thing} obtained from no premises.
 * 
 * @author Yevgeny Kazakov
 */
public class SubClassInclusionOwlThing
		extends
			AbstractSubClassInclusionDecomposedInference {

	public SubClassInclusionOwlThing(IndexedContextRoot inferenceRoot,
			IndexedClass owlThingSubsumer) {
		super(inferenceRoot, owlThingSubsumer);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}
	
	public ContextInitialization getPremise(
			ContextInitialization.Factory factory) {
		return factory.getContextInitialization(getOrigin());
	}

	@Override
	public String toString() {
		return super.toString() + " (owl:Thing)";
	}

	@Override
	public final <O> O accept(
			SubClassInclusionDecomposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionOwlThing inference);

	}

}
