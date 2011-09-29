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

import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkEquivalentObjectPropertiesAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkEquivalentObjectPropertiesAxiomView<T extends ElkEquivalentObjectPropertiesAxiom>
		extends ElkNaryObjectView<T, ElkObjectPropertyExpression> implements
		ElkEquivalentObjectPropertiesAxiom {

	/**
	 * Constructing {@link ElkEquivalentObjectPropertiesAxiomView} from
	 * {@link ElkEquivalentObjectPropertiesAxiom} using a viewer
	 * 
	 * @param refElkEquivalentObjectPropertiesAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer used to access the functions for sub objects
	 */
	public ElkEquivalentObjectPropertiesAxiomView(
			T refElkEquivalentObjectPropertiesAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkEquivalentObjectPropertiesAxiom, subObjectViewer);
	}

	public List<? extends ElkObjectPropertyExpression> getObjectPropertyExpressions() {
		return getElkSubObjectViews();
	}

	@Override
	Iterable<? extends ElkObjectPropertyExpression> getElkSubObjects() {
		return this.elkObject.getObjectPropertyExpressions();
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