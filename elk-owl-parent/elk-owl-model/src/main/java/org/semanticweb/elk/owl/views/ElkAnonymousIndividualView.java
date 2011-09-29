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

import org.semanticweb.elk.owl.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.owl.visitors.ElkIndividualVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkAnonymousIndividual}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkAnonymousIndividualView<T extends ElkAnonymousIndividual>
		extends ElkObjectView<T> implements ElkAnonymousIndividual {

	/**
	 * Constructing {@link ElkAnonymousIndividualView} from
	 * {@link ElkAnonymousIndividual} using a sub-object viewer
	 * 
	 * @param refElkAnonymousIndividual
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkAnonymousIndividualView(T refElkAnonymousIndividual,
			ElkObjectViewer subObjectViewer) {
		super(refElkAnonymousIndividual, subObjectViewer);
	}

	public String getNodeId() {
		return this.elkObject.getNodeId();
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getNodeId());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkAnonymousIndividualView<?>) {
			ElkAnonymousIndividualView<?> otherView = (ElkAnonymousIndividualView<?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getNodeId().equals(otherView.getNodeId());
		}
		return false;
	}

	public <O> O accept(ElkIndividualVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}