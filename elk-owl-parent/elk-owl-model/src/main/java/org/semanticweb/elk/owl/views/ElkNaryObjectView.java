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
 * An abstract view class for instances {@link ElkObject} that has multiple
 * unordered sub-objects
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the sub-objects
 */
public abstract class ElkNaryObjectView<T extends ElkObject, S extends ElkObject>
		extends ElkObjectView<T> {

	/**
	 * Constructing {@link ElkNaryObjectView} from {@link ElkObject}
	 * 
	 * @param refElkNaryObject
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-objects
	 */
	public ElkNaryObjectView(T refElkNaryObject, ElkObjectViewer subObjectViewer) {
		super(refElkNaryObject, subObjectViewer);
	}

	/**
	 * Get the sub-objects of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract Iterable<? extends S> getElkSubObjects();

	/**
	 * Converting the sub-objects through {@link subObjectViewer}
	 * 
	 * @return
	 */
	protected List<? extends S> getElkSubObjectViews() {
		List<S> result = new ArrayList<S>();
		for (S obj : getElkSubObjects())
			result.add(this.subObjectViewer.getView(obj));
		return result;
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(),
				subObjectViewer.naryHashCode(getElkSubObjectViews()));
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkNaryObjectView<?, ?>) {
			ElkNaryObjectView<?, ?> otherView = (ElkNaryObjectView<?, ?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& this.subObjectViewer.naryEquals(
							this.getElkSubObjectViews(),
							otherView.getElkSubObjectViews());
		}
		return false;
	}
}