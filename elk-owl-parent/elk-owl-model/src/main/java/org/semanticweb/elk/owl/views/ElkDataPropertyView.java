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

import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.visitors.ElkDataPropertyExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * Implements a view for instances of {@link ElkDataProperty}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkDataPropertyView<T extends ElkDataProperty> extends
		ElkEntityView<T> implements ElkDataProperty {

	/**
	 * Constructing {@link ElkDataPropertyView} from {@link ElkDataProperty}
	 * using a sub-object viewer
	 * 
	 * @param refElkDataProperty
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkDataPropertyView(T refElkDataProperty,
			ElkObjectViewer subObjectViewer) {
		super(refElkDataProperty, subObjectViewer);
	}

	public <O> O accept(ElkDataPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkEntityVisitor<O> visitor) {
		return visitor.visit(this);
	}

}