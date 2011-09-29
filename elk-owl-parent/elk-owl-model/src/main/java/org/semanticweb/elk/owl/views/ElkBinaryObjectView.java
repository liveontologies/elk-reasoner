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
 * An abstract view class for instances {@link ElkObject} that has two
 * sub-objects
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the first sub-object
 * @param <H>
 *            the type of the second sub-object
 * 
 */
public abstract class ElkBinaryObjectView<T extends ElkObject, S extends ElkObject, H extends ElkObject>
		extends ElkObjectView<T> {

	/**
	 * Constructing {@link ElkBinaryObjectView} from {@link ElkObject}
	 * 
	 * @param refElkBinaryObject
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-objects
	 */
	public ElkBinaryObjectView(T refElkBinaryObject,
			ElkObjectViewer subObjectViewer) {
		super(refElkBinaryObject, subObjectViewer);
	}

	/**
	 * Get the first sub-object of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract S getFirstElkSubObject();

	/**
	 * Get the second sub-object of the input wrapped elk object
	 * 
	 * @return
	 */
	abstract H getSecondElkSubObject();

	/**
	 * Convert the first sub-object through {@link subObjectViewer}
	 * 
	 * @return resulting view object
	 */
	protected S getFirstElkSubObjectView() {
		return subObjectViewer.getView(getFirstElkSubObject());
	}

	/**
	 * Convert the second sub-object through {@link subObjectViewer}
	 * 
	 * @return resulting view object
	 */
	protected H getSecondElkSubObjectView() {
		return subObjectViewer.getView(getSecondElkSubObject());
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getFirstElkSubObjectView(),
				getSecondElkSubObjectView());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkBinaryObjectView<?, ?, ?>) {
			ElkBinaryObjectView<?, ?, ?> otherView = (ElkBinaryObjectView<?, ?, ?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getFirstElkSubObjectView().equals(
							otherView.getFirstElkSubObject())
					&& getSecondElkSubObjectView().equals(
							otherView.getSecondElkSubObject());
		}
		return false;
	}
}
