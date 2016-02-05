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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.Contradiction;

/**
 * A {@link Contradiction} obtained by propagation of a {@link Contradiction}
 * over a {@link BackwardLink}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class ContradictionPropagated extends AbstractContradictionInference {

	private final IndexedObjectProperty premiseRelation_;

	private final IndexedContextRoot inferenceRoot_;

	public ContradictionPropagated(IndexedContextRoot inferenceRoot,
			IndexedObjectProperty relation, IndexedContextRoot conclusionRoot) {
		super(conclusionRoot);
		premiseRelation_ = relation;
		inferenceRoot_ = inferenceRoot;
	}

	public ContradictionPropagated(BackwardLink premise) {
		this(premise.getDestination(), premise.getRelation(),
				premise.getTraceRoot());
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return inferenceRoot_;
	}

	public BackwardLink getFirstPremise(BackwardLink.Factory factory) {
		return factory.getBackwardLink(getOrigin(), premiseRelation_,
				getDestination());
	}

	public Contradiction getSecondPremise(Contradiction.Factory factory) {
		return factory.getContradiction(getOrigin());
	}

	@Override
	public String toString() {
		return "Propagated contradiction" + premiseRelation_ + "<-"
				+ inferenceRoot_;
	}

	@Override
	public final <O> O accept(ContradictionInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * Visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public static interface Visitor<O> {
		
		public O visit(ContradictionPropagated inference);
		
	}

}
