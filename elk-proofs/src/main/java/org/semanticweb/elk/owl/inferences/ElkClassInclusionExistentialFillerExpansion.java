package org.semanticweb.elk.owl.inferences;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;

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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *    C ⊑ D  
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * ∃R.C ⊑ ∃R.D
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionExistentialFillerExpansion
		extends AbstractElkInference {

	private final static String NAME_ = "Existential Filler Expansion";

	private final ElkClassExpression subClass_, superClass_;

	private final ElkObjectPropertyExpression property_;

	ElkClassInclusionExistentialFillerExpansion(ElkClassExpression subClass,
			ElkClassExpression superClass,
			ElkObjectPropertyExpression property) {
		this.subClass_ = subClass;
		this.superClass_ = superClass;
		this.property_ = property;
	}

	public ElkClassExpression getSubClass() {
		return subClass_;
	}

	public ElkClassExpression getSuperClass() {
		return superClass_;
	}

	public ElkObjectPropertyExpression getProperty() {
		return property_;
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
		switch (index) {
		case 0:
			return getPremise(factory);
		default:
			return failGetPremise(index);
		}
	}

	public ElkSubClassOfAxiom getPremise(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(subClass_, superClass_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(
				factory.getObjectSomeValuesFrom(property_, subClass_),
				factory.getObjectSomeValuesFrom(property_, superClass_));

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

		ElkClassInclusionExistentialFillerExpansion getElkClassInclusionExistentialFillerExpansion(
				ElkClassExpression subClass, ElkClassExpression superClass,
				ElkObjectPropertyExpression property);

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

		O visit(ElkClassInclusionExistentialFillerExpansion inference);

	}

}
