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
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *    R ⊑ S  
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 * ∃R.C ⊑ ∃S.C
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionExistentialPropertyExpansion
		extends AbstractElkInference {

	private final static String NAME_ = "Existential Property Expansion";

	private final ElkObjectPropertyExpression subProperty_, superProperty_;

	private final ElkClassExpression filler_;

	ElkClassInclusionExistentialPropertyExpansion(
			ElkObjectPropertyExpression subProperty,
			ElkObjectPropertyExpression superProperty,
			ElkClassExpression filler) {
		this.subProperty_ = subProperty;
		this.superProperty_ = superProperty;
		this.filler_ = filler;
	}

	public ElkObjectPropertyExpression getSubProperty() {
		return subProperty_;
	}

	public ElkObjectPropertyExpression getSuperProperty() {
		return superProperty_;
	}

	public ElkClassExpression getFiller() {
		return filler_;
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

	public ElkSubObjectPropertyOfAxiom getPremise(ElkObject.Factory factory) {
		return factory.getSubObjectPropertyOfAxiom(subProperty_,
				superProperty_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(
				factory.getObjectSomeValuesFrom(subProperty_, filler_),
				factory.getObjectSomeValuesFrom(superProperty_, filler_));

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

		ElkClassInclusionExistentialPropertyExpansion getElkClassInclusionExistentialPropertyExpansion(
				ElkObjectPropertyExpression subProperty,
				ElkObjectPropertyExpression superProperty,
				ElkClassExpression filler);

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
