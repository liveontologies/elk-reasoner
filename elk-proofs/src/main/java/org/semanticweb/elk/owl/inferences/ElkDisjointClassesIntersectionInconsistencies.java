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
package org.semanticweb.elk.owl.inferences;

import java.util.Arrays;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * Represents the inference:
 * 
 * <pre>
 *      (1)          (2)           ((n*(n+1)/2)-1)  (n*(n+1)/2) 
 *  C0 ⊓ C1 ⊑ ⊥  C0 ⊓ C2 ⊑ ⊥  ...  Cn-2 ⊓ Cn ⊑ ⊥  Cn-1 ⊓ Cn ⊑ ⊥
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  DisjointClasses(C0 C1 ... Cn)
 * </pre>
 * 
 * @author Peter Skocovsky
 */
public class ElkDisjointClassesIntersectionInconsistencies
		extends AbstractElkInference {

	public final static String NAME = "Disjoint Classes Introduction";

	private final List<? extends ElkClassExpression> expressions_;

	ElkDisjointClassesIntersectionInconsistencies(
			final List<? extends ElkClassExpression> expressions) {
		this.expressions_ = expressions;
	}

	ElkDisjointClassesIntersectionInconsistencies(
			final ElkClassExpression... expressions) {
		this(Arrays.asList(expressions));
	}

	public List<? extends ElkClassExpression> getExpressions() {
		return expressions_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPremiseCount() {
		final int size = expressions_.size();
		return ((size - 1) * size) / 2;
	}

	@Override
	public ElkAxiom getPremise(final int index,
			final ElkObject.Factory factory) {
		checkPremiseIndex(index);

		final int size = expressions_.size();

		int sum = size - 1;
		int i = sum - 1;
		while (index >= sum) {
			sum += i;
			i--;
		}
		final int firstIndex = size - i - 2;
		final int secondIndex = size - (sum - index);

		return factory.getSubClassOfAxiom(
				factory.getObjectIntersectionOf(expressions_.get(firstIndex),
						expressions_.get(secondIndex)),
				factory.getOwlNothing());
	}

	@Override
	public ElkDisjointClassesAxiom getConclusion(
			final ElkObject.Factory factory) {
		return factory.getDisjointClassesAxiom(expressions_);
	}

	@Override
	public <O> O accept(final ElkInference.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Peter Skocovsky
	 */
	public interface Factory {

		ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
				List<? extends ElkClassExpression> expressions);

		ElkDisjointClassesIntersectionInconsistencies getElkDisjointClassesIntersectionInconsistencies(
				ElkClassExpression... expressions);

	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Peter Skocovsky
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(ElkDisjointClassesIntersectionInconsistencies inference);

	}

}
