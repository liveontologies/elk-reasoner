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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *   (1)        (n)
 *  C ⊑ C1 ... C ⊑ Cn
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *   C ⊑ C1 ⊓...⊓ Cn
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionObjectIntersectionOfComposition
		extends AbstractElkInference {

	private final ElkClassExpression subExpression_;

	private final List<? extends ElkClassExpression> conjuncts_;

	ElkClassInclusionObjectIntersectionOfComposition(
			ElkClassExpression subExpression,
			List<? extends ElkClassExpression> conjuncts) {
		this.subExpression_ = subExpression;
		this.conjuncts_ = conjuncts;
	}

	public ElkClassExpression getSubExpression() {
		return subExpression_;
	}

	public List<? extends ElkClassExpression> getConjuncts() {
		return conjuncts_;
	}

	public int getPremiseCount() {
		return conjuncts_.size();
	}

	public ElkSubClassOfAxiom getPremise(int i, ElkObjectFactory factory) {
		if (i < 1 || i > conjuncts_.size()) {
			throw new IllegalArgumentException("No such premise: " + i);
		}
		// else
		return factory.getSubClassOfAxiom(subExpression_,
				conjuncts_.get(i - 1));
	}

	public ElkSubClassOfAxiom getConclusion(ElkObjectFactory factory) {
		return factory.getSubClassOfAxiom(subExpression_,
				factory.getObjectIntersectionOf(conjuncts_));
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

		ElkClassInclusionObjectIntersectionOfComposition getElkClassInclusionObjectIntersectionOfComposition(
				ElkClassExpression subExpression,
				List<? extends ElkClassExpression> conjuncts);

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

		O visit(ElkClassInclusionObjectIntersectionOfComposition inference);

	}

}
