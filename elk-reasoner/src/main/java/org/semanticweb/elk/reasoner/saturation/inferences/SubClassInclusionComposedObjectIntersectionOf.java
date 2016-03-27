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

import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectIntersectionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getSubsumer()} instance of
 * {@link IndexedObjectIntersectionOf} from two
 * {@link SubClassInclusionComposed} premises:<br>
 * 
 * <pre>
 *     (1)        (2)
 *  [C] ⊑ +D1  [C] ⊑ +D2
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *    [C] ⊑ +(D1 ⊓ D2)
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * D1 ⊓ D2 = {@link #getConclusionSubsumer()} (from which D1 and D2 can be
 * obtained)<br>
 * 
 * @see IndexedObjectIntersectionOf#getFirstConjunct()
 * @see IndexedObjectIntersectionOf#getSecondConjunct()
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 * 
 */
public class SubClassInclusionComposedObjectIntersectionOf extends
		AbstractSubClassInclusionComposedInference<IndexedObjectIntersectionOf> {

	public SubClassInclusionComposedObjectIntersectionOf(
			IndexedContextRoot inferenceRoot,
			IndexedObjectIntersectionOf conjunction) {
		super(inferenceRoot, conjunction);
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public SubClassInclusionComposed getFirstPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getSubsumer().getFirstConjunct());
	}

	public SubClassInclusionComposed getSecondPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getSubsumer().getSecondConjunct());
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

		public O visit(SubClassInclusionComposedObjectIntersectionOf inference);

	}

}
