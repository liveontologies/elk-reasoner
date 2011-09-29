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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.owl.visitors.ElkSubObjectPropertyExpressionVisitor;

/**
 * Implements a view for instances of {@link ElkObjectPropertyChain}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkObjectPropertyChainView<T extends ElkObjectPropertyChain>
		extends ElkObjectView<T> implements ElkObjectPropertyChain {

	/**
	 * Constructing {@link ElkObjectPropertyChainView} from
	 * {@link ElkObjectPropertyChain} using a sub-object viewer
	 * 
	 * @param refElkObjectPropertyChain
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkObjectPropertyChainView(T refElkObjectPropertyChain,
			ElkObjectViewer subObjectViewer) {
		super(refElkObjectPropertyChain, subObjectViewer);
	}

	public List<? extends ElkObjectPropertyExpression> getObjectPropertyExpressions() {
		List<ElkObjectPropertyExpression> result = new ArrayList<ElkObjectPropertyExpression>();
		for (ElkObjectPropertyExpression obj : this.elkObject
				.getObjectPropertyExpressions())
			result.add(this.subObjectViewer.getView(obj));
		return result;
	}

	@Override
	public int generateHashCode() {
		return combinedHashCode(getClass(),
				combinedHashCode(getObjectPropertyExpressions()));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkObjectPropertyChainView<?>) {
			ElkObjectPropertyChainView<?> otherView = (ElkObjectPropertyChainView<?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& this.getObjectPropertyExpressions().equals(
							otherView.getObjectPropertyExpressions());
		}
		return false;
	}

	public <O> O accept(ElkSubObjectPropertyExpressionVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}