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

import org.semanticweb.elk.owl.interfaces.ElkClassAssertionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkClassAssertionAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkClassAssertionAxiomView<T extends ElkClassAssertionAxiom>
		extends ElkBinaryObjectView<T, ElkClassExpression, ElkIndividual>
		implements ElkClassAssertionAxiom {

	/**
	 * Constructing {@link ElkClassAssertionAxiomView} from
	 * {@link ElkClassAssertionAxiom} using a sub-object viewer
	 * 
	 * @param refElkClassAssertionAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkClassAssertionAxiomView(T refElkClassAssertionAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkClassAssertionAxiom, subObjectViewer);
	}

	public ElkClassExpression getClassExpression() {
		return getFirstElkSubObjectView();
	}

	public ElkIndividual getIndividual() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkClassExpression getFirstElkSubObject() {
		return this.elkObject.getClassExpression();
	}

	@Override
	ElkIndividual getSecondElkSubObject() {
		return this.elkObject.getIndividual();
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