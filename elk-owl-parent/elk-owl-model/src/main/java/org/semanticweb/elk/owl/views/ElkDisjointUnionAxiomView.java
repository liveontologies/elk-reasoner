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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkObjectIntersectionOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */
public class ElkDisjointUnionAxiomView<T extends ElkDisjointUnionAxiom> extends
		ElkUnaryNaryObjectView<T, ElkClass, ElkClassExpression> implements
		ElkDisjointUnionAxiom {

	/**
	 * Constructing {@link ElkDisjointUnionAxiomView} from
	 * {@link ElkDisjointUnionAxiom} using a viewer
	 * 
	 * @param refElkDisjointUnionAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer used to access the functions for sub objects
	 */
	public ElkDisjointUnionAxiomView(T refElkDisjointUnionAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkDisjointUnionAxiom, subObjectViewer);
	}

	public ElkClass getDefinedClass() {
		return getUnarySubObjectView();
	}

	public List<? extends ElkClassExpression> getClassExpressions() {
		return getNarySubObjectViews();
	}

	@Override
	ElkClass getUnarySubObject() {
		return this.elkObject.getDefinedClass();
	}

	@Override
	Iterable<? extends ElkClassExpression> getNarySubObjects() {
		return this.elkObject.getClassExpressions();
	}

	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}