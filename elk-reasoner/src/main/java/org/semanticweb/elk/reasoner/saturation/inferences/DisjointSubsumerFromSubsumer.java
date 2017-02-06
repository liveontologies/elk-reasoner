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
package org.semanticweb.elk.reasoner.saturation.inferences;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpressionList;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedDisjointClassesAxiom;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.DisjointSubsumer;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.Conclusion.Factory;

/**
 * A {@link ClassInference} producing a {@link DisjointSubsumer} from a
 * {@link SubClassInclusionComposed} and {@link IndexedDisjointClassesAxiom}
 * such that {@link SubClassInclusionComposed#getSubsumer()} occurs in
 * {@link IndexedDisjointClassesAxiom#getMembers()}:<br>
 * 
 * <pre>
 *     (1)       (2)
 *  [C] ⊑ +D  [Disjoint(L)]  
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯ D ∈ L
 *        [C] ⊑ D:L
 * </pre>
 * 
 * The parameters can be obtained as follows:<br>
 * 
 * C = {@link #getDestination()}<br>
 * L = {@link #getDisjointExpressions()}<br>
 * D = {@link #getPosition()} gives an index of D in
 * {@link IndexedClassExpressionList#getElements()} <br>
 * 
 * @author Pavel Klinov
 *
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 */
public class DisjointSubsumerFromSubsumer
		extends AbstractDisjointSubsumerInference {

	private final ElkAxiom reason_;

	public DisjointSubsumerFromSubsumer(IndexedContextRoot inferenceRoot,
			IndexedClassExpressionList disjoint, int position,
			ElkAxiom reason) {
		super(inferenceRoot, disjoint, position);
		this.reason_ = reason;
	}

	@Override
	public IndexedContextRoot getOrigin() {
		return getDestination();
	}

	public ElkAxiom getReason() {
		return reason_;
	}

	public SubClassInclusionComposed getFirstPremise(
			SubClassInclusionComposed.Factory factory) {
		return factory.getSubClassInclusionComposed(getOrigin(),
				getDisjointExpressions().getElements().get(getPosition()));
	}

	public IndexedDisjointClassesAxiom getSecondPremise(
			IndexedDisjointClassesAxiom.Factory factory) {
		return factory.getIndexedDisjointClassesAxiom(getReason(),
				getDisjointExpressions());
	}

	@Override
	public int getPremiseCount() {
		return 2;
	}

	@Override
	public Conclusion getPremise(int index, Factory factory) {
		switch (index) {
		case 0:
			return getFirstPremise(factory);
		case 1:
			return getSecondPremise(factory);
		default:
			return failGetPremise(index);
		}
	}

	@Override
	public final <O> O accept(DisjointSubsumerInference.Visitor<O> visitor) {
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

		public O visit(DisjointSubsumerFromSubsumer inference);

	}

}
