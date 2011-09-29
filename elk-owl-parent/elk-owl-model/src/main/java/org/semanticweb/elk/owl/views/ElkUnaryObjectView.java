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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * An abstract view class for instances {@link ElkObject} that has one
 * sub-object
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the sub-object
 */
public abstract class ElkUnaryObjectView<T extends ElkObject, S extends ElkObject>
		extends ElkObjectView<T> {

	/**
	 * Constructing {@link ElkUnaryObjectView} from {@link ElkObject}
	 * 
	 * @param refElkObject
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-object
	 */
	public ElkUnaryObjectView(T refElkObject, ElkObjectViewer subObjectViewer) {
		super(refElkObject, subObjectViewer);
	}

	/**
	 * Get the sub-object of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract S getElkSubObject();

	/**
	 * Convert the sub-object through {@link subObjectViewer}
	 * 
	 * @return
	 */
	protected S getElkSubObjectView() {
		return subObjectViewer.getView(getElkSubObject());
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getElkSubObjectView());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkNaryObjectView<?, ?>) {
			ElkUnaryObjectView<?, ?> otherView = (ElkUnaryObjectView<?, ?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getElkSubObjectView()
							.equals(otherView.getElkSubObject());
		}
		return false;
	}

}
