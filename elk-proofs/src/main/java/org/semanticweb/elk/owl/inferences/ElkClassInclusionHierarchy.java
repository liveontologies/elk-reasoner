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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *    (1)           (n)
 *  C0 ⊑ C1 ... Cn-1 ⊑ Cn
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *        C0 ⊑ Cn
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionHierarchy extends AbstractElkInference {

	private final List<? extends ElkClassExpression> expressions_;

	ElkClassInclusionHierarchy(List<? extends ElkClassExpression> expressions) {
		this.expressions_ = expressions;
	}

	ElkClassInclusionHierarchy(ElkClassExpression first,
			ElkClassExpression second, ElkClassExpression third) {
		List<ElkClassExpression> expressions = new ArrayList<ElkClassExpression>(
				3);
		expressions.add(first);
		expressions.add(second);
		expressions.add(third);
		this.expressions_ = expressions;
	}

	public List<? extends ElkClassExpression> getExpressions() {
		return expressions_;
	}

	public int getPremiseCount() {
		return expressions_.size() - 1;
	}

	public ElkSubClassOfAxiom getPremise(int i, ElkObjectFactory factory) {
		if (i < 1 || i >= expressions_.size()) {
			throw new IllegalArgumentException("No such premise: " + i);
		}
		// else
		return factory.getSubClassOfAxiom(expressions_.get(i - 1),
				expressions_.get(i));
	}

	public ElkSubClassOfAxiom getConclusion(ElkObjectFactory factory) {
		return factory.getSubClassOfAxiom(expressions_.get(0),
				expressions_.get(expressions_.size() - 1));
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

		ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
				List<? extends ElkClassExpression> expressions);

		ElkClassInclusionHierarchy getElkClassInclusionHierarchy(
				ElkClassExpression first, ElkClassExpression second,
				ElkClassExpression third);

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

		O visit(ElkClassInclusionHierarchy inference);

	}

}
