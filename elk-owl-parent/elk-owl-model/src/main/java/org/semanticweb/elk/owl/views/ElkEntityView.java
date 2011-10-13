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

import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * An abstract view class for instances {@link ElkEntity}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the sub-object
 */
public abstract class ElkEntityView<T extends ElkEntity> extends
		ElkObjectView<T> {

	/**
	 * Constructing {@link ElkEntityView} from {@link ElkEntity}
	 * 
	 * @param refElkEntity
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-object
	 */
	public ElkEntityView(T refElkEntity, ElkObjectViewer subObjectViewer) {
		super(refElkEntity, subObjectViewer);
	}

	public ElkIri getIri() {
		return elkObject.getIri();
	}
	
	public int generateHashCode() {
		return combinedHashCode(getClass(), getIri());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkEntityView<?>) {
			ElkEntityView<?> otherView = (ElkEntityView<?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getIri().equals(otherView.getIri());
		}
		return false;
	}

	public abstract <O> O accept(ElkEntityVisitor<O> visitor);

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return accept((ElkEntityVisitor<O>) visitor);
	}

}
