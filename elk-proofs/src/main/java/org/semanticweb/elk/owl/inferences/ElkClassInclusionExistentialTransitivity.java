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
import org.semanticweb.elk.owl.interfaces.ElkTransitiveObjectPropertyAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *   (1)           (2)               (n)              (n+1)
 *  C0 ⊑ ∃T.C1  C1 ⊑ ∃T.C2 ... Cn-1 ⊑ ∃T.Cn  TransitiveObjectProperty(T)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *                         C0 ⊑ ∃T.Cn
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionExistentialTransitivity
		extends AbstractElkInference {

	private final static String NAME_ = "Existential Transitivity Composition";

	private final List<? extends ElkClassExpression> classExpressions_;

	private final ElkObjectPropertyExpression transitiveProperty_;

	ElkClassInclusionExistentialTransitivity(
			List<? extends ElkClassExpression> classExpressions,
			ElkObjectPropertyExpression transitiveProperty) {
		if (classExpressions.size() < 3) {
			throw new IllegalArgumentException(classExpressions.toString());
		}
		this.classExpressions_ = classExpressions;
		this.transitiveProperty_ = transitiveProperty;
	}

	public List<? extends ElkClassExpression> getClassExpressions() {
		return classExpressions_;
	}

	public ElkObjectPropertyExpression getTransitiveProperty() {
		return transitiveProperty_;
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
				factory.getObjectSomeValuesFrom(transitiveProperty_,
						classExpressions_.get(index + 1)));
	}

	public ElkTransitiveObjectPropertyAxiom getLastPremise(
			ElkObject.Factory factory) {
		return factory.getTransitiveObjectPropertyAxiom(transitiveProperty_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(classExpressions_.get(0),
				factory.getObjectSomeValuesFrom(transitiveProperty_,
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

		ElkClassInclusionExistentialTransitivity getElkClassInclusionExistentialTransitivity(
				List<? extends ElkClassExpression> classExpressions,
				ElkObjectPropertyExpression transitiveProperty);

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

		O visit(ElkClassInclusionExistentialTransitivity inference);

	}

}
