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
import org.semanticweb.elk.owl.interfaces.ElkObjectMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkObjectMaxCardinality}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkObjectMaxCardinalityView<T extends ElkObjectMaxCardinality>
		extends
		ElkCardinalityObjectView<T, ElkObjectPropertyExpression, ElkClassExpression>
		implements ElkObjectMaxCardinality {

	/**
	 * Constructing {@link ElkObjectMaxCardinalityView} from
	 * {@link ElkObjectMaxCardinality} using a sub-object viewer
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

	public ElkObjectPropertyExpression getObjectPropertyExpression() {
		return getFirstElkSubObjectView();
	}

	public ElkClassExpression getClassExpression() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkObjectPropertyExpression getFirstElkSubObject() {
		return this.elkObject.getObjectPropertyExpression();
	}

	@Override
	ElkClassExpression getSecondElkSubObject() {
		return this.elkObject.getClassExpression();
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