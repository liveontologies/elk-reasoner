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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectUnionOf;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.Conclusion.Factory;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getSubsumer()} instance of
 * {@link IndexedObjectUnionOf} from a {@link SubClassInclusionComposed}:<br>
 * 
 * <pre>
 *      [C] ⊑ +D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ 
 *  [C] ⊑ +(..⊔ D ⊔..)
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()}<br>
 * (..⊔ D ⊔..) = {@link #getConclusionSubsumer()}<br>
 * D = {@link #getPosition()} gives the position of D in
 * {@link IndexedObjectUnionOf#getDisjuncts()} for E<br>
 * 
 * @see IndexedObjectUnionOf#getDisjuncts()
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class SubClassInclusionComposedObjectUnionOf extends
		AbstractSubClassInclusionComposedInference<IndexedObjectUnionOf> {

	/**
	 * The position of the disjunct of {@link IndexedObjectUnionOf} from which
	 * this {@link IndexedObjectUnionOf} is produced
	 */
	private final int position_;

	public SubClassInclusionComposedObjectUnionOf(
			IndexedContextRoot inferenceRoot, IndexedObjectUnionOf disjunction,
			int position) {
		super(inferenceRoot, disjunction);
		this.position_ = position;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	/**
	 * @return the position of the produced super-class
	 *         {@link IndexedObjectUnionOf} corresponding to the super-class of
	 *         the premise
	 */
	public int getPosition() {
		return position_;
	}

	public SubClassInclusionComposed getPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getSubsumer().getDisjuncts().get(position_));
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public Conclusion getPremise(int index, Factory factory) {
		switch (index) {
		case 0:
			return getPremise(factory);
		default:
			return failGetPremise(index);
		}
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

		public O visit(SubClassInclusionComposedObjectUnionOf inference);

	}

}
