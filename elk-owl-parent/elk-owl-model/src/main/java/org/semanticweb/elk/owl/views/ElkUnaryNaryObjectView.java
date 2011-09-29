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

import org.semanticweb.elk.owl.interfaces.ElkObject;

/**
 * An abstract view class for instances {@link ElkObject} that has one main
 * sub-object and multiple unordered sub-objects
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the main sub-object
 * @param <S>
 *            the type of the unordered sub-objects
 */
public abstract class ElkUnaryNaryObjectView<T extends ElkObject, S extends ElkObject, U extends ElkObject>
		extends ElkObjectView<T> {

	/**
	 * Constructing {@link ElkNaryObjectView} from {@link ElkObject}
	 * 
	 * @param refElkUnaryNaryObject
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-objects
	 */
	public ElkUnaryNaryObjectView(T refElkUnaryNaryObject,
			ElkObjectViewer subObjectViewer) {
		super(refElkUnaryNaryObject, subObjectViewer);
	}

	/**
	 * Get the main sub-object of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract S getUnarySubObject();

	/**
	 * Get the unordered sub-objects of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract Iterable<? extends U> getNarySubObjects();

	/**
	 * Converting the main sub-object through {@link subObjectViewer}
	 * 
	 * @return
	 */
	protected S getUnarySubObjectView() {
		return subObjectViewer.getView(getUnarySubObject());
	}

	/**
	 * Converting the sub-objects through {@link subObjectViewer}
	 * 
	 * @return
	 */
	protected List<? extends U> getNarySubObjectViews() {
		List<U> result = new ArrayList<U>();
		for (U obj : getNarySubObjects())
			result.add(this.subObjectViewer.getView(obj));
		return result;
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getUnarySubObjectView(),
				subObjectViewer.naryHashCode(getNarySubObjectViews()));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkUnaryNaryObjectView<?, ?, ?>) {
			ElkUnaryNaryObjectView<?, ?, ?> otherView = (ElkUnaryNaryObjectView<?, ?, ?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& this.getUnarySubObjectView().equals(
							otherView.getUnarySubObjectView())
					&& this.subObjectViewer.naryEquals(
							this.getNarySubObjectViews(),
							otherView.getNarySubObjectViews());
		}
		return false;
	}
}