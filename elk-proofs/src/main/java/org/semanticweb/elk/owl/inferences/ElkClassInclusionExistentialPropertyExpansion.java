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
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
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
public class ElkClassInclusionExistentialPropertyExpansion
		extends AbstractElkInference {
	
	private final static String NAME_SIMPLE_ = "Existential Property Expansion";
	
	private final static String NAME_COMPLEX_ = "Existential Composition";

	private final List<? extends ElkClassExpression> classExpressions_;

	private final List<? extends ElkObjectPropertyExpression> subChain_;

	private final ElkObjectPropertyExpression superProperty_;

	ElkClassInclusionExistentialPropertyExpansion(
			List<? extends ElkClassExpression> classExpressions,
			List<? extends ElkObjectPropertyExpression> subChain,
			ElkObjectPropertyExpression superProperty) {
		if (classExpressions.size() != subChain.size() + 1) {
			throw new IllegalArgumentException(
					classExpressions.toString() + ", " + subChain.toString());
		}
		this.classExpressions_ = classExpressions;
		this.subChain_ = subChain;
		this.superProperty_ = superProperty;
	}

	ElkClassInclusionExistentialPropertyExpansion(
			ElkClassExpression subExpression,
			ElkObjectPropertyExpression subProperty, ElkClassExpression filler,
			ElkObjectPropertyExpression superProperty) {
		List<ElkClassExpression> classExpressions = new ArrayList<ElkClassExpression>(
				2);
		classExpressions.add(subExpression);
		classExpressions.add(filler);
		this.classExpressions_ = classExpressions;
		List<ElkObjectPropertyExpression> subChain = new ArrayList<ElkObjectPropertyExpression>(
				1);
		subChain.add(subProperty);
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
		if (subChain_.size() == 1) {
			return NAME_SIMPLE_;
		}
		// else
		return NAME_COMPLEX_;
	}
	
	@Override
	public int getPremiseCount() {
		return classExpressions_.size();
	}

	@Override
	public ElkAxiom getPremise(int index, ElkObjectFactory factory) {
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
			ElkObjectFactory factory) {
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
			ElkObjectFactory factory) {
		return factory.getSubObjectPropertyOfAxiom(
				subChain_.size() == 1 ? subChain_.get(0)
						: factory.getObjectPropertyChain(subChain_),
				superProperty_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObjectFactory factory) {
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

		ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
				List<? extends ElkClassExpression> classExpressions,
				List<? extends ElkObjectPropertyExpression> subChain,
				ElkObjectPropertyExpression superProperty);

		ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyUnfolding(
				ElkClassExpression subExpression,
				ElkObjectPropertyExpression subProperty,
				ElkClassExpression filler,
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

		O visit(ElkClassInclusionExistentialPropertyExpansion inference);

	}

}
