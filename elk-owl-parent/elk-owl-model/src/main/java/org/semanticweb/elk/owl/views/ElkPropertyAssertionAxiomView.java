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
 * An abstract view class for instances {@link ElkObject} that are property
 * assertions
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 * @param <S>
 *            the type of the property
 * @param <H>
 *            the type of the first instance
 * @param <U>
 *            the type of the second instance
 * 
 */
public abstract class ElkPropertyAssertionAxiomView<T extends ElkObject, S extends ElkObject, H extends ElkObject, U extends ElkObject>
		extends ElkObjectView<T> {

	/**
	 * Constructing {@link ElkBinaryObjectView} from {@link ElkObject}
	 * 
	 * @param refElkPropertyAssertionAxiom
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for the sub-objects
	 */
	public ElkPropertyAssertionAxiomView(T refElkPropertyAssertionAxiom,
			ElkObjectViewer subObjectViewer) {
		super(refElkPropertyAssertionAxiom, subObjectViewer);
	}

	/**
	 * Get the property of the wrapped elk assertion axiom
	 * 
	 * @return
	 */
	abstract S getProperty();

	/**
	 * Get the first instance of the wrapped elk assertion axiom
	 * 
	 * @return
	 */
	abstract H getFirstInstance();

	/**
	 * Get the second instance of the wrapped elk assertion axiom
	 * 
	 * @return
	 */
	abstract U getSecondInstance();

	/**
	 * Convert the property through {@link subObjectViewer}
	 * 
	 * @return resulting view object
	 */
	protected S getPropertyView() {
		return subObjectViewer.getView(getPropertyView());
	}

	/**
	 * Convert the first instance through {@link subObjectViewer}
	 * 
	 * @return resulting view object
	 */
	protected H getFirstInstanceView() {
		return subObjectViewer.getView(getFirstInstance());
	}

	/**
	 * Convert the second instance through {@link subObjectViewer}
	 * 
	 * @return resulting view object
	 */
	protected U getSecondInstanceView() {
		return subObjectViewer.getView(getSecondInstance());
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getPropertyView(),
				getFirstInstanceView(), getSecondInstanceView());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkPropertyAssertionAxiomView<?, ?, ?, ?>) {
			ElkPropertyAssertionAxiomView<?, ?, ?, ?> otherView = (ElkPropertyAssertionAxiomView<?, ?, ?, ?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getPropertyView().equals(otherView.getPropertyView())
					&& getFirstInstanceView().equals(
							otherView.getFirstInstanceView())
					&& getSecondInstanceView().equals(
							otherView.getSecondInstanceView());
		}
		return false;
	}
}
