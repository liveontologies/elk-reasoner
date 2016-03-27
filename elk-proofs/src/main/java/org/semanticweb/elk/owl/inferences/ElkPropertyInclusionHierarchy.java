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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *   (1)           (n)
 *  P ⊑ R1 ... Rn-1 ⊑ Rn
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *        P ⊑ Rn
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkPropertyInclusionHierarchy extends AbstractElkInference {

	private final ElkSubObjectPropertyExpression subExpression_;

	private final List<? extends ElkObjectPropertyExpression> expressions_;

	ElkPropertyInclusionHierarchy(ElkSubObjectPropertyExpression subExpression,
			List<? extends ElkObjectPropertyExpression> expressions) {
		this.subExpression_ = subExpression;
		this.expressions_ = expressions;
	}

	ElkPropertyInclusionHierarchy(ElkSubObjectPropertyExpression first,
			ElkObjectPropertyExpression second,
			ElkObjectPropertyExpression third) {
		subExpression_ = first;
		List<ElkObjectPropertyExpression> expressions = new ArrayList<ElkObjectPropertyExpression>(
				2);
		expressions.add(second);
		expressions.add(third);
		this.expressions_ = expressions;
	}

	public ElkSubObjectPropertyExpression getSubExpression() {
		return subExpression_;
	}

	public List<? extends ElkObjectPropertyExpression> getExpressions() {
		return expressions_;
	}

	public int getPremiseCount() {
		return expressions_.size();
	}

	public ElkSubObjectPropertyOfAxiom getPremise(int i,
			ElkObjectFactory factory) {
		if (i < 1 || i > expressions_.size()) {
			throw new IllegalArgumentException("No such premise: " + i);
		}
		// else
		if (i == 1) {
			return factory.getSubObjectPropertyOfAxiom(subExpression_,
					expressions_.get(0));
		}
		// else i > 1
		return factory.getSubObjectPropertyOfAxiom(expressions_.get(i - 2),
				expressions_.get(i - 1));

	}

	public ElkSubObjectPropertyOfAxiom getConclusion(ElkObjectFactory factory) {
		return factory.getSubObjectPropertyOfAxiom(subExpression_,
				expressions_.get(expressions_.size() - 1));
	}

	public <O> O accept(Visitor<O> visitor) {
		return visitor.visit(this);
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

		ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
				ElkSubObjectPropertyExpression subExpression,
				List<? extends ElkObjectPropertyExpression> expressions);

		ElkPropertyInclusionHierarchy getElkPropertyInclusionHierarchy(
				ElkSubObjectPropertyExpression first,
				ElkObjectPropertyExpression second,
				ElkObjectPropertyExpression third);

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

		O visit(ElkPropertyInclusionHierarchy inference);

	}

}
