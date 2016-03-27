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
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Propagation;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;

/**
 * A {@link ClassInference} producing a {@link SubClassInclusionComposed} with
 * {@link SubClassInclusionComposed#getSubsumer()} instance of
 * {@link IndexedObjectSomeValuesFrom} from a {@link BackwardLink} and a
 * {@link Propagation}:<br>
 * 
 * <pre>
 *   (1)                (2)
 *  C ⊑ <∃R>.[D]  ∃[R].[D] ⊑ ∃S.E
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *          [C] ⊑ +∃S.E
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * R = {@link #getPropagationRelation()}<br>
 * D = {@link #getOrigin()}<br>
 * ∃S.E = {@link #getConclusionSubsumer()} (from which S and E can be obtained)
 * <br>
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class SubClassInclusionComposedObjectSomeValuesFrom extends
		AbstractSubClassInclusionComposedInference<IndexedObjectSomeValuesFrom> {

	private final IndexedObjectProperty propagationRelation_;

	private final IndexedContextRoot inferenceRoot_;

	public SubClassInclusionComposedObjectSomeValuesFrom(Propagation premise,
			IndexedContextRoot conclusionRoot) {
		super(conclusionRoot, premise.getCarry());
		inferenceRoot_ = premise.getDestination();
		propagationRelation_ = premise.getSubDestination();
	}

	public SubClassInclusionComposedObjectSomeValuesFrom(BackwardLink premise,
			IndexedObjectSomeValuesFrom carry) {
		super(premise.getTraceRoot(), carry);
		inferenceRoot_ = premise.getDestination();
		propagationRelation_ = premise.getRelation();
	}

	public IndexedObjectProperty getPropagationRelation() {
		return propagationRelation_;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return inferenceRoot_;
	}

	public BackwardLink getFirstPremise(BackwardLink.Factory factory) {
		return factory.getBackwardLink(getOrigin(), propagationRelation_,
				getDestination());
	}

	public Propagation getSecondPremise(Propagation.Factory factory) {
		return factory.getPropagation(getOrigin(), propagationRelation_,
				getSubsumer());
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

		public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference);

	}

}
