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
import org.semanticweb.elk.owl.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *  DisjointClasses(C0 C1 ... Cn)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *         Ci ⊓ Cj ⊑ ⊥
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionOfDisjointClasses extends AbstractElkInference {

	private final static String NAME_ = "Disjoint Classes Translation";

	private final List<? extends ElkClassExpression> expressions_;

	/**
	 * (different) positions for two disjoint classes
	 */
	private final int firstPos_, secondPos_;

	ElkClassInclusionOfDisjointClasses(
			List<? extends ElkClassExpression> expressions, int firstPos,
			int secondPos) {
		if (firstPos == secondPos) {
			throw new IllegalArgumentException(
					"Different positions expected but both = " + firstPos);
		}
		this.expressions_ = expressions;
		this.firstPos_ = firstPos;
		this.secondPos_ = secondPos;
	}

	public List<? extends ElkClassExpression> getExpressions() {
		return expressions_;
	}

	public int getFirstPos() {
		return firstPos_;
	}

	public int getSecondPos() {
		return secondPos_;
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

	public ElkDisjointClassesAxiom getPremise(ElkObject.Factory factory) {
		return factory.getDisjointClassesAxiom(expressions_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(
				factory.getObjectIntersectionOf(expressions_.get(firstPos_),
						expressions_.get(secondPos_)),
				factory.getOwlNothing());
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

		ElkClassInclusionOfDisjointClasses getElkClassInclusionOfDisjointClasses(
				List<? extends ElkClassExpression> expressions, int firstPos,
				int secondPos);

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

		O visit(ElkClassInclusionOfDisjointClasses inference);

	}

}
