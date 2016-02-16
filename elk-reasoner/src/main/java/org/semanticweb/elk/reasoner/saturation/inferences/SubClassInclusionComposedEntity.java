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

import org.semanticweb.elk.reasoner.indexing.model.IndexedClassEntity;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedEntity;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionComposed} from a
 * {@link SubClassInclusionDecomposed} with
 * {@link SubClassInclusionDecomposed#getSubsumer()} instance of
 * {@link IndexedEntity}:<br>
 * 
 * <pre>
 *  [C] ⊑ -A
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  [C] ⊑ +A
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * A = {@link #getSubsumer()}<br>
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class SubClassInclusionComposedEntity
		extends
			AbstractSubClassInclusionComposedInference<IndexedClassEntity> {

	public SubClassInclusionComposedEntity(IndexedContextRoot origin,
			IndexedClassEntity subsumer) {
		super(origin, subsumer);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubClassInclusionDecomposed getPremise() {
		return FACTORY.getSubClassInclusionDecomposed(getOrigin(),
				getSubsumer());
	}

	@Override
	public String toString() {
		return super.toString() + " (- => +)";
	}

	@Override
	public final <O> O accept(
			SubClassInclusionComposedInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionComposedEntity inference);

	}

}
