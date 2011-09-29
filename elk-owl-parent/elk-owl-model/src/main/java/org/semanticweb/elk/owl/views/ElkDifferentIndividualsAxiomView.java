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

import org.semanticweb.elk.owl.interfaces.ElkDifferentIndividualsAxiom;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.visitors.ElkAssertionAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkDifferentIndividualsAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the typed of the wrapped elk object
 */
public class ElkDifferentIndividualsAxiomView<T extends ElkDifferentIndividualsAxiom>
		extends ElkNaryObjectView<T, ElkIndividual> implements
		ElkDifferentIndividualsAxiom {

	/**
	 * Constructing {@link ElkDifferentIndividualsAxiomView} from
	 * {@link ElkDifferentIndividualsAxiom} using a viewer
	 * 
	 * @param refElkDifferentIndividualsAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer used to access the functions for sub objects
	 */
	public ElkDifferentIndividualsAxiomView(T refElkDifferentIndividualsAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkDifferentIndividualsAxiom, subObjectViewer);
	}

	public List<? extends ElkIndividual> getIndividuals() {
		return getElkSubObjectViews();
	}

	@Override
	Iterable<? extends ElkIndividual> getElkSubObjects() {
		return this.elkObject.getIndividuals();
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