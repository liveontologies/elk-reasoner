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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectHasSelf;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.PropertyRange;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

/**
 * A {@link SubClassInclusionDecomposed} obtained from a
 * {@link SubClassInclusionDecomposed} with
 * {@link SubClassInclusionDecomposed#getSubsumer()} of the type
 * {@link IndexedObjectHasSelf} and a {@link PropertyRange}:<br>
 * 
 * <pre>
 *     (1)            (2)
 *  [C] ⊑ -∃R.Self  Range(R,D)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         [C] ⊑ -D
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * ∃R.Self = {@link #getPremiseSubsumer()} (from which R can be obtained)<br>
 * D = {@link #getConclusionSubsumer()}
 * 
 * @see IndexedObjectHasSelf#getProperty()
 * @see IndexedObjectProperty#getToldRanges()
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class SubClassInclusionObjectHasSelfPropertyRange
		extends AbstractSubClassInclusionDecomposedInference {

	private final IndexedObjectHasSelf premiseSubsumer_;

	public SubClassInclusionObjectHasSelfPropertyRange(
			IndexedContextRoot inferenceRoot,
			IndexedObjectHasSelf premiseSubsumer,
			IndexedClassExpression range) {
		super(inferenceRoot, range);
		this.premiseSubsumer_ = premiseSubsumer;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public IndexedObjectHasSelf getPremiseSubsumer() {
		return premiseSubsumer_;
	}

	public SubClassInclusionDecomposed getFirstPremise(
			SubClassInclusionDecomposed.Factory factory) {
		return factory.getSubClassInclusionDecomposed(getOrigin(),
				premiseSubsumer_);
	}

	public PropertyRange getSecondPremise(PropertyRange.Factory factory) {
		return factory.getPropertyRange(premiseSubsumer_.getProperty(),
				getSubsumer());
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
	 * @param <O>
	 *            the type of the output
	 */
	public static interface Visitor<O> {

		public O visit(SubClassInclusionObjectHasSelfPropertyRange inference);

	}

}
