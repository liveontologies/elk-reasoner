/*
 * #%L
 * ELK OWL Object Interfaces
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.views;

import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.ElkNegativeDataPropertyAssertionAxiom;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of
 * {@link ElkNegativeDataPropertyAssertionAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkNegativeDataPropertyAssertionAxiomView<T extends ElkNegativeDataPropertyAssertionAxiom>
		extends
		ElkPropertyAssertionAxiomView<T, ElkDataPropertyExpression, ElkIndividual, ElkLiteral>
		implements ElkNegativeDataPropertyAssertionAxiom {

	/**
	 * Constructing {@link ElkNegativeDataPropertyAssertionAxiomView} from
	 * {@link ElkNegativeDataPropertyAssertionAxiom} using a sub-object viewer
	 * 
	 * @param refElkNegativeDataPropertyAssertionAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkNegativeDataPropertyAssertionAxiomView(
			T refElkNegativeDataPropertyAssertionAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkNegativeDataPropertyAssertionAxiom, subObjectViewer);
	}

	public ElkDataPropertyExpression getDataPropertyExpression() {
		return getPropertyView();
	}

	public ElkIndividual getIndividual() {
		return getFirstInstanceView();
	}

	public ElkLiteral getLiteral() {
		return getSecondInstanceView();
	}

	@Override
	ElkDataPropertyExpression getProperty() {
		return this.elkObject.getDataPropertyExpression();
	}

	@Override
	ElkIndividual getFirstInstance() {
		return this.elkObject.getIndividual();
	}

	@Override
	ElkLiteral getSecondInstance() {
		return this.elkObject.getLiteral();
	}

	public <O> O accept(ElkAssertionAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}