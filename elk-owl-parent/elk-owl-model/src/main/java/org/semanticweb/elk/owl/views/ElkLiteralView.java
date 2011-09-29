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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Implements a view for instances of {@link ElkLiteral}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped elk object
 */

public class ElkLiteralView<T extends ElkLiteral> extends ElkObjectView<T>
		implements ElkLiteral {

	/**
	 * Constructing {@link ElkLiteralView} from {@link ElkLiteral} using a
	 * sub-object viewer
	 * 
	 * @param refElkLiteral
	 *            the reference elk object for which the view object is
	 *            constructed
	 * 
	 * @param subObjectViewer
	 *            the viewer for sub-objects
	 */
	public ElkLiteralView(T refElkLiteral, ElkObjectViewer subObjectViewer) {
		super(refElkLiteral, subObjectViewer);
	}

	public String getLexicalForm() {
		return this.elkObject.getLexicalForm();
	}

	public ElkDatatype getDatatype() {
		return subObjectViewer.getView(this.elkObject.getDatatype());
	}

	public String getLanguageTag() {
		return this.elkObject.getLanguageTag();
	}

	public int generateHashCode() {
		return combinedHashCode(getClass(), getLexicalForm(), getDatatype(),
				getLanguageTag());
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof ElkLiteralView<?>) {
			ElkLiteralView<?> otherView = (ElkLiteralView<?>) other;
			return (getClass() == other.getClass())
					&& (this.subObjectViewer == otherView.subObjectViewer)
					&& getLexicalForm().equals(otherView.getLexicalForm())
					&& getDatatype().equals(otherView.getDatatype())
					&& getLanguageTag().equals(otherView.getLanguageTag());
		}
		return false;
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkLiteralVisitor<O> visitor) {
		return visitor.visit(this);
	}

}