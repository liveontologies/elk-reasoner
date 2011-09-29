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

import org.semanticweb.elk.owl.interfaces.ElkDataMaxCardinality;
import org.semanticweb.elk.owl.interfaces.ElkDataPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.visitors.ElkClassExpressionVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkDataMaxCardinality}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkDataMaxCardinalityView<T extends ElkDataMaxCardinality> extends
		ElkCardinalityObjectView<T, ElkDataPropertyExpression, ElkDataRange>
		implements ElkDataMaxCardinality {

	/**
	 * Constructing {@link ElkDataMaxCardinalityView} from
	 * {@link ElkDataMaxCardinality} using a sub-object viewer
	 * 
	 * @param refElkDataMaxCardinality
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkDataMaxCardinalityView(T refElkDataMaxCardinality,
			ElkObjectViewer subObjectViewer) {
		super(refElkDataMaxCardinality, subObjectViewer);
	}

	public ElkDataPropertyExpression getDataPropertyExpression() {
		return getFirstElkSubObjectView();
	}

	public ElkDataRange getDataRange() {
		return getSecondElkSubObjectView();
	}

	@Override
	ElkDataPropertyExpression getFirstElkSubObject() {
		return this.elkObject.getDataPropertyExpression();
	}

	@Override
	ElkDataRange getSecondElkSubObject() {
		return this.elkObject.getDataRange();
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