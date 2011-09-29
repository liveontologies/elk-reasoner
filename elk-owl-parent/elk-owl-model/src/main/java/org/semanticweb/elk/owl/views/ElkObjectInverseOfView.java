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

import org.semanticweb.elk.owl.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.visitors.ElkObjectPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Implements a view for instances of {@link ElkObjectInverseOf}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkObjectInverseOfView<T extends ElkObjectInverseOf> extends
		ElkUnaryObjectView<T, ElkObjectProperty> implements ElkObjectInverseOf {

	/**
	 * Constructing {@link ElkObjectInverseOfView} from
	 * {@link ElkObjectInverseOf} using a sub-object viewer
	 * 
	 * @param refElkObjectInverseOf
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkObjectInverseOfView(T refElkObjectInverseOf,
			ElkObjectViewer subObjectViewer) {
		super(refElkObjectInverseOf, subObjectViewer);
	}

	public ElkObjectProperty getObjectProperty() {
		return getElkSubObjectView();
	}

	@Override
	ElkObjectProperty getElkSubObject() {
		return this.elkObject.getObjectProperty();
	}

	public <O> O accept(ElkObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}