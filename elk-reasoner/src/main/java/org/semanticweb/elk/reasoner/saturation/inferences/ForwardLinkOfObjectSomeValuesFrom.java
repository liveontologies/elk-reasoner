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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ForwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionDecomposed;

// TODO: split on two inferences
/**
 * A {@link ClassInference} producing a {@link ForwardLink} from a
 * {@link SubClassInclusionDecomposed} with
 * {@link SubClassInclusionDecomposed#getSubsumer()} of the type
 * {@link IndexedObjectSomeValuesFrom}:<br>
 * 
 * <pre>
 *    [C] ⊑ -∃R.D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  [C] ⊑ <∃R>.(D ⊓ ∃R-)
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getOrigin()} = {@link #getDestination()} <br>
 * ∃R.D = {@link #getDecomposedExistential()} (from which R and D can be
 * obtained)<br>
 * 
 * @author "Yevgeny Kazakov"
 */
public class ForwardLinkOfObjectSomeValuesFrom extends AbstractClassInference
		implements ForwardLinkInference {

	private final IndexedObjectSomeValuesFrom existential_;

	public ForwardLinkOfObjectSomeValuesFrom(IndexedContextRoot inferenceRoot,
			IndexedObjectSomeValuesFrom subsumer) {
		super(inferenceRoot);
		existential_ = subsumer;
	}

	public IndexedObjectSomeValuesFrom getDecomposedExistential() {
		return this.existential_;
	}

	public SubClassInclusionDecomposed getPremise(
			SubClassInclusionDecomposed.Factory factory) {
		return factory.getSubClassInclusionDecomposed(getOrigin(),
				existential_);
	}

	/**
	 * @param factory
	 *            the factory for creating conclusions
	 * 
	 * @return the conclusion produced by this inference
	 */
	public ForwardLink getConclusion(ForwardLink.Factory factory) {
		return factory.getForwardLink(getDestination(),
				existential_.getProperty(),
				IndexedObjectSomeValuesFrom.Helper.getTarget(existential_));
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	@Override
	public final <O> O accept(ClassInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public final <O> O accept(ForwardLinkInference.Visitor<O> visitor) {
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

		public O visit(ForwardLinkOfObjectSomeValuesFrom inference);

	}

}
