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

import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkClassAxiomVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkSubClassOfAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkSubClassOfAxiomView<T extends ElkSubClassOfAxiom> extends
		ElkBinaryObjectView<T, ElkClassExpression, ElkClassExpression>
		implements ElkSubClassOfAxiom {

	/**
	 * Constructing {@link ElkSubClassOfAxiomView} from
	 * {@link ElkSubClassOfAxiom} using a sub-object viewer
	 * 
	 * @param refElkSubClassOfAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkSubClassOfAxiomView(T refElkSubClassOfAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkSubClassOfAxiom, subObjectViewer);
	}

	public ElkClassExpression getSubClassExpression() {
		return getFirstElkSubObjectView();
	}

	public ElkClassExpression getSuperClassExpression() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkClassExpression getFirstElkSubObject() {
		return this.elkObject.getSubClassExpression();
	}

	@Override
	ElkClassExpression getSecondElkSubObject() {
		return this.elkObject.getSuperClassExpression();
	}

	public <O> O accept(ElkClassAxiomVisitor<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public <O> O accept(ElkAxiomVisitor<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		// TODO Auto-generated method stub
		return null;
	}
}