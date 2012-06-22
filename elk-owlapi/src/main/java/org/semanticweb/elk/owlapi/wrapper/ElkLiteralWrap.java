/*
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.wrapper;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Implements the {@link ElkLiteral} interface by wrapping instances of
 * {@link OWLLiteral}
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <T>
 *            the type of the wrapped object
 */
public class ElkLiteralWrap<T extends OWLLiteral> extends ElkObjectWrap<T>
		implements ElkLiteral {

	public ElkLiteralWrap(T owlLiteral) {
		super(owlLiteral);
	}

	@Override
	public String getLexicalForm() {
		return this.owlObject.getLiteral();
	}

	public String getLanguage() {
		return this.owlObject.getLang();
	}

	public ElkDatatype getDatatype() {
		return converter.convert(this.owlObject.getDatatype());
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkLiteralVisitor<O> visitor) {
		return visitor.visit(this);
	}
}