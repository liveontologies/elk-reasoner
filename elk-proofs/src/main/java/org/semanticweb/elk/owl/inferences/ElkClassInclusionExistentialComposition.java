package org.semanticweb.elk.owl.inferences;

/*-
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
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *   (1)           (2)               (n)              (n+1)
 *  C0 ⊑ ∃R1.C1  C1 ⊑ ∃R2.C2 ... Cn-1 ⊑ ∃Rn.Cn  R1...Rn ⊑ S
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                         C0 ⊑ ∃S.Cn
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionExistentialComposition
		extends AbstractElkInference {

	private final static String NAME_ = "Existential Composition";

	private final List<? extends ElkClassExpression> classExpressions_;

	private final List<? extends ElkObjectPropertyExpression> subChain_;

	private final ElkObjectPropertyExpression superProperty_;

	ElkClassInclusionExistentialComposition(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		int chainLength = subChain.size();
		if (classExpressions.size() != chainLength + 1) {
			throw new IllegalArgumentException(classExpressions.toString()
					+ " is expected to contain one more element than "
					+ subChain.toString());
		}
		this.classExpressions_ = classExpressions;
		this.subChain_ = subChain;
		this.superProperty_ = superProperty;
	}

	public List<? extends ElkClassExpression> getClassExpressions() {
		return classExpressions_;
	}

	public List<? extends ElkObjectPropertyExpression> getSubChain() {
		return subChain_;
	}

	public ElkObjectPropertyExpression getSuperProperty() {
		return superProperty_;
	}

	@Override
	public String getName() {
		return NAME_;
	}

	@Override
	public int getPremiseCount() {
		return classExpressions_.size();
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObject.Factory factory) {
		checkPremiseIndex(index);
		if (index < getExistentialPremiseCount()) {
			return getExistentialPremise(index, factory);
		}
		// else
		return getLastPremise(factory);
	}

	public int getExistentialPremiseCount() {
		return classExpressions_.size() - 1;
	}

	public ElkSubClassOfAxiom getExistentialPremise(int index,
			ElkObject.Factory factory) {
		if (index < 0 || index >= getExistentialPremiseCount()) {
			throw new IndexOutOfBoundsException(
					"No existential premise with index: " + index);
		}
		// else
		return factory.getSubClassOfAxiom(classExpressions_.get(index),
				factory.getObjectSomeValuesFrom(subChain_.get(index),
						classExpressions_.get(index + 1)));
	}

	public ElkSubObjectPropertyOfAxiom getLastPremise(
			ElkObject.Factory factory) {
		return factory.getSubObjectPropertyOfAxiom(
				subChain_.size() == 1 ? subChain_.get(0)
						: factory.getObjectPropertyChain(subChain_),
				superProperty_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(classExpressions_.get(0),
				factory.getObjectSomeValuesFrom(superProperty_,
						classExpressions_.get(classExpressions_.size() - 1)));

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

		ElkClassInclusionExistentialComposition getElkClassInclusionExistentialComposition(
				List<? extends ElkClassExpression> classExpressions,
				List<? extends ElkObjectPropertyExpression> subChain,
				ElkObjectPropertyExpression superProperty);

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

		O visit(ElkClassInclusionExistentialComposition inference);

	}

}
