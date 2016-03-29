package org.semanticweb.elk.owl.inferences;

/*
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *      C ⊑ D
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *   C ⊑ (..⊔ D ⊔..)
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionObjectUnionOfComposition
		extends AbstractElkInference {

	private final ElkClassExpression subExpression_;

	private final List<? extends ElkClassExpression> disjuncts_;

	private final int disjunctPos_;

	ElkClassInclusionObjectUnionOfComposition(ElkClassExpression subExpression,
			List<? extends ElkClassExpression> disjuncts, int disjunctPos) {
		this.subExpression_ = subExpression;
		this.disjuncts_ = disjuncts;
		this.disjunctPos_ = disjunctPos;
	}

	public ElkClassExpression getSubExpression() {
		return subExpression_;
	}

	public List<? extends ElkClassExpression> getDisjuncts() {
		return disjuncts_;
	}

	public int getDisjunctPos() {
		return disjunctPos_;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObjectFactory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkSubClassOfAxiom getPremise(ElkObjectFactory factory) {
		return factory.getSubClassOfAxiom(subExpression_,
				disjuncts_.get(disjunctPos_));
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObjectFactory factory) {
		return factory.getSubClassOfAxiom(subExpression_,
				factory.getObjectUnionOf(disjuncts_));
	}

	@Override
	public <O> O accept(ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		ElkClassInclusionObjectUnionOfComposition getElkClassInclusionObjectUnionOfComposition(
				ElkClassExpression subExpression,
				List<? extends ElkClassExpression> disjuncts, int disjunctPos);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkClassInclusionObjectUnionOfComposition inference);

	}

}
