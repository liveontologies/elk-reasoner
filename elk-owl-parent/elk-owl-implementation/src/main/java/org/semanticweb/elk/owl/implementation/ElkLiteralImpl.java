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
package org.semanticweb.elk.owl.implementation;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkObjectVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ELK implementation of ElkLiteral.
 * 
 * @author Markus Kroetzsch
 */
public class ElkLiteralImpl extends ElkObjectImpl implements ElkLiteral {

	protected final String lexicalForm;
	protected final ElkDatatype datatype;

	/* package-private */ElkLiteralImpl(String lexicalForm, ElkDatatype datatype) {
		this.lexicalForm = lexicalForm;
		this.datatype = datatype;
		this.structuralHashCode = HashGenerator.combineListHash(
				datatype.structuralHashCode(), lexicalForm.hashCode());
	}

	public boolean structuralEquals(Object object) {
		if (this == object) {
			return true;
		} else if (object instanceof ElkLiteral) {
			return lexicalForm.equals(((ElkLiteral) object).getLexicalForm())
					&& datatype.equals(((ElkLiteral) object).getDatatype());
		} else {
			return false;
		}
	}

	public String getLexicalForm() {
		return lexicalForm;
	}

	public ElkDatatype getDatatype() {
		return datatype;
	}

	public <O> O accept(ElkLiteralVisitor<O> visitor) {
		return visitor.visit(this);
	}

	public <O> O accept(ElkObjectVisitor<O> visitor) {
		return visitor.visit(this);
	}

}
