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
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDataSomeValuesFrom;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkDataSomeValuesFrom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkDataSomeValuesFromView<T extends ElkDataSomeValuesFrom> extends
		ElkBinaryObjectView<T, ElkDataPropertyExpression, ElkDataRange>
		implements ElkDataSomeValuesFrom {

	/**
	 * Constructing {@link ElkDataSomeValuesFromView} from
	 * {@link ElkDataSomeValuesFrom} using a sub-object viewer
	 * 
	 * @param refElkDataSomeValuesFrom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkDataSomeValuesFromView(T refElkDataSomeValuesFrom,
			ElkObjectViewer subObjectViewer) {
		super(refElkDataSomeValuesFrom, subObjectViewer);
	}

	public ElkDataPropertyExpression getProperty() {
		return getFirstElkSubObjectView();
	}

	public ElkDataRange getFiller() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkDataPropertyExpression getFirstElkSubObject() {
		return this.elkObject.getProperty();
	}

	@Override
	ElkDataRange getSecondElkSubObject() {
		return this.elkObject.getFiller();
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}