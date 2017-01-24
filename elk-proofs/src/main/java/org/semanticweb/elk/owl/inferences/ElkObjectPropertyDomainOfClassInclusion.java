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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyDomainAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;

/**
 * Represents the inference:
 * 
 * <pre>
 *         ∃R.⊤ ⊑ C
 * ⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯
 *  ObjectPropertyDomain(R C)
 * </pre>
 * 
 * @author Peter Skocovsky
 */
public class ElkObjectPropertyDomainOfClassInclusion
		extends AbstractElkInference {

	private final static String NAME = "Property Domain Introduction";

	private final ElkObjectPropertyExpression property_;

	private final ElkClassExpression domain_;

	ElkObjectPropertyDomainOfClassInclusion(
			final ElkObjectPropertyExpression property,
			final ElkClassExpression domain) {
		this.property_ = property;
		this.domain_ = domain;
	}

	public ElkObjectPropertyExpression getProperty() {
		return property_;
	}

	public ElkClassExpression getDomain() {
		return domain_;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public int getPremiseCount() {
		return 1;
	}

	@Override
	public ElkAxiom getPremise(final int index,
			final ElkObject.Factory factory) {
		if (index == 0) {
			return getPremise(factory);
		}
		// else
		return failGetPremise(index);
	}

	public ElkSubClassOfAxiom getPremise(final ElkObject.Factory factory) {
		return factory.getSubClassOfAxiom(factory.getObjectSomeValuesFrom(
				property_, factory.getOwlThing()), domain_);
	}

	@Override
	public ElkObjectPropertyDomainAxiom getConclusion(
			final ElkObject.Factory factory) {
		return factory.getObjectPropertyDomainAxiom(property_, domain_);
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

		ElkObjectPropertyDomainOfClassInclusion getElkObjectPropertyDomainOfClassInclusion(
				ElkObjectPropertyExpression property,
				ElkClassExpression domain);
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

		O visit(ElkObjectPropertyDomainOfClassInclusion inference);

	}

}
