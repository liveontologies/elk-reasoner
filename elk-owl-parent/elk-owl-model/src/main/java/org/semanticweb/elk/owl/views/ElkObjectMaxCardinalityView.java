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
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinalityQualified;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkObjectMaxCardinalityQualified}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkObjectMaxCardinalityView<T extends ElkObjectMaxCardinalityQualified>
		extends
		ElkCardinalityObjectView<T, ElkObjectPropertyExpression, ElkClassExpression>
		implements ElkObjectMaxCardinalityQualified {

	/**
	 * Constructing {@link ElkObjectMaxCardinalityView} from
	 * {@link ElkObjectMaxCardinalityQualified} using a sub-object viewer
	 * 
	 * @param refElkObjectMaxCardinality
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkObjectMaxCardinalityView(T refElkObjectMaxCardinality,
			ElkObjectViewer subObjectViewer) {
		super(refElkObjectMaxCardinality, subObjectViewer);
	}

	public ElkObjectPropertyExpression getProperty() {
		return getFirstElkSubObjectView();
	}

	public ElkClassExpression getFiller() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkObjectPropertyExpression getFirstElkSubObject() {
		return this.elkObject.getProperty();
	}

	@Override
	ElkClassExpression getSecondElkSubObject() {
		return this.elkObject.getFiller();
	}

	@Override
	public int getCardinality() {
		return this.elkObject.getCardinality();
	}

	public <O> O accept(ElkClassExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}
}