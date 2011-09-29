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

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSymmetricObjectPropertyAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkSymmetricObjectPropertyAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkSymmetricObjectPropertyAxiomView<T extends ElkSymmetricObjectPropertyAxiom>
		extends ElkUnaryObjectView<T, ElkObjectPropertyExpression> implements
		ElkSymmetricObjectPropertyAxiom {

	/**
	 * Constructing {@link ElkSymmetricObjectPropertyAxiomView} from
	 * {@link ElkSymmetricObjectPropertyAxiom} using a sub-object viewer
	 * 
	 * @param refElkSymmetricObjectPropertyAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkSymmetricObjectPropertyAxiomView(
			T refElkSymmetricObjectPropertyAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkSymmetricObjectPropertyAxiom, subObjectViewer);
	}

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return getElkSubObjectView();
	}

	@Override
	ElkObjectPropertyExpression getElkSubObject() {
		return this.elkObject.getObjectPropertyExpression();
	}

	public <O> O accept(ElkObjectPropertyAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}