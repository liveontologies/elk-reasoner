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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *     R(a,b)
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  {a} ⊑ ∃R.{b}
 * </pre>
 * 
 * @author Yevgeny Kazakov
 *
 */
public class ElkClassInclusionOfObjectPropertyAssertion
		extends AbstractElkInference {

	private final static String NAME_ = "Property Assertion Transaltion";

	private final ElkIndividual subject_, object_;

	private final ElkObjectPropertyExpression property_;

	ElkClassInclusionOfObjectPropertyAssertion(ElkIndividual subject,
			ElkObjectPropertyExpression property, ElkIndividual object) {
		this.subject_ = subject;
		this.property_ = property;
		this.object_ = object;
	}

	public ElkIndividual getSubject() {
		return subject_;
	}

	public ElkObjectPropertyExpression getProperty() {
		return property_;
	}

	public ElkIndividual getObject() {
		return object_;
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

	public ElkObjectPropertyAssertionAxiom getPremise(
			ElkObject.Factory factory) {
		return factory.getObjectPropertyAssertionAxiom(property_, subject_,
				object_);
	}

	@Override
	public ElkSubClassOfAxiom getConclusion(ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(factory.getObjectOneOf(subject_),
				factory.getObjectSomeValuesFrom(property_,
						factory.getObjectOneOf(object_)));
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

		ElkClassInclusionOfObjectPropertyAssertion getElkClassInclusionOfObjectPropertyAssertion(
				ElkIndividual subject, ElkObjectPropertyExpression property,
				ElkIndividual object);
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

		O visit(ElkClassInclusionOfObjectPropertyAssertion inference);

	}

}
