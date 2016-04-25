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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *  EquivalentObjectProperties(R0 R1 ... Rn)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                  Ri ⊑ Rj
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkPropertyInclusionOfEquivalence extends AbstractElkInference {

	private final static String NAME_ = "Equivalent Properties Decomposition";

	private final List<? extends ElkObjectPropertyExpression> expressions_;

	/**
	 * positions for sub-property and super-property within property equivalence
	 */
	private final int subPos_, superPos_;

	ElkPropertyInclusionOfEquivalence(
			List<? extends ElkObjectPropertyExpression> expressions, int subPos,
			int superPos) {
		this.expressions_ = expressions;
		this.subPos_ = subPos;
		this.superPos_ = superPos;
	}

	ElkPropertyInclusionOfEquivalence(ElkObjectPropertyExpression first,
			ElkObjectPropertyExpression second, boolean sameOrder) {
		List<ElkObjectPropertyExpression> expressions = new ArrayList<ElkObjectPropertyExpression>(
				2);
		expressions.add(first);
		expressions.add(second);
		this.expressions_ = expressions;
		if (sameOrder) {
			subPos_ = 0;
			superPos_ = 1;
		} else {
			subPos_ = 1;
			superPos_ = 0;
		}
	}

	public List<? extends ElkObjectPropertyExpression> getExpressions() {
		return expressions_;
	}

	public int getSubPos() {
		return subPos_;
	}

	public int getSuperPos() {
		return superPos_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObject.Factory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkEquivalentObjectPropertiesAxiom getPremise(
			ElkObject.Factory factory) {
		return factory.getEquivalentObjectPropertiesAxiom(expressions_);
	}

	@Override
	public ElkSubObjectPropertyOfAxiom getConclusion(
			ElkObject.Factory factory) {
		return factory.getSubObjectPropertyOfAxiom(expressions_.get(subPos_),
				expressions_.get(superPos_));
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

		ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
				List<? extends ElkObjectPropertyExpression> expressions,
				int subPos, int superPos);

		ElkPropertyInclusionOfEquivalence getElkPropertyInclusionOfEquivalence(
				ElkObjectPropertyExpression first,
				ElkObjectPropertyExpression second, boolean sameOrder);

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

		O visit(ElkPropertyInclusionOfEquivalence inference);

	}

}
