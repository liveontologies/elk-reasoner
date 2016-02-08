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
 * A {@link SubClassInclusionComposed} with {@link IndexedObjectSomeValuesFrom}
 * super-class obtained by from a {@link Propagation} and a matching
 * {@link BackwardLink}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author "Yevgeny Kazakov"
 */
public class SubClassInclusionComposedObjectSomeValuesFrom
		extends
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

	public BackwardLink getFirstPremise() {
		return FACTORY.getBackwardLink(getOrigin(), propagationRelation_,
				getDestination());
	}

	public Propagation getSecondPremise() {
		return FACTORY.getPropagation(getOrigin(), propagationRelation_,
				getSubsumer());
	}

	@Override
	public String toString() {
		return super.toString() + " (propagation)";
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
	 */
	public static interface Visitor<O> {
		
		public O visit(SubClassInclusionComposedObjectSomeValuesFrom inference);
		
	}

}
