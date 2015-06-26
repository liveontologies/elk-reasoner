/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.inferences;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.BackwardLinkImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.ContradictionImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.BackwardLink;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Contradiction;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.ClassInferenceVisitor;

/**
 * Represents a {@link Contradiction} produced via a propagation over a
 * {@link BackwardLink}.
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 */
public class PropagatedContradiction extends ContradictionImpl implements
		Contradiction, ClassInference {

	private final IndexedObjectProperty premiseRelation_;

	private final IndexedContextRoot inconsistentRoot_;

	public PropagatedContradiction(IndexedContextRoot conclusionRoot,
			IndexedObjectProperty relation, IndexedContextRoot inconsistentRoot) {
		super(conclusionRoot);
		premiseRelation_ = relation;
		inconsistentRoot_ = inconsistentRoot;
	}

	public PropagatedContradiction(BackwardLink premise) {
		this(premise.getSource(), premise.getRelation(), premise.getRoot());
	}

	@Override
	public IndexedContextRoot getInferenceContextRoot() {
		return inconsistentRoot_;
	}

	public BackwardLink getLinkPremise() {
		return new BackwardLinkImpl(getInferenceContextRoot(), getRoot(),
				premiseRelation_);
	}

	public Contradiction getContradictionPremise() {
		return new ContradictionImpl(getInferenceContextRoot());
	}

	@Override
	public String toString() {
		return "Propagated contradiction" + premiseRelation_ + "<-"
				+ inconsistentRoot_;
	}

	@Override
	public <I, O> O acceptTraced(ClassInferenceVisitor<I, O> visitor,
			I parameter) {
		return visitor.visit(this, parameter);
	}

}
