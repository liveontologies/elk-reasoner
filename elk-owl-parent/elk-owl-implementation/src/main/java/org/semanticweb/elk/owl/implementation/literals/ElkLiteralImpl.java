/*
 * #%L
 * ELK OWL Model Implementation
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
package org.semanticweb.elk.owl.implementation.literals;

import org.semanticweb.elk.owl.implementation.ElkObjectImpl;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;

/**
 * Base implementation of {@link ElkLiteral}, corresponds to rdfs:Literal.
 * 
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 */
public class ElkLiteralImpl extends ElkObjectImpl implements ElkLiteral {

	protected final String lexicalForm;

	public ElkLiteralImpl(String lexicalForm) {
		this.lexicalForm = lexicalForm;
	}

	@Override
	public String getLexicalForm() {
		return lexicalForm;
	}

	@Override
	public ElkDatatype getDatatype() {
		return ElkDatatypeMap.RDFS_LITERAL;
	}

	@Override
	public <O> O accept(ElkLiteralVisitor<O> visitor) {
		return visitor.visit(this);
	}

	@Override
	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
