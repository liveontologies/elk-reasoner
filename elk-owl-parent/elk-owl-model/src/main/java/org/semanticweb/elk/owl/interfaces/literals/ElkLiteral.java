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
package org.semanticweb.elk.owl.interfaces.literals;

import org.semanticweb.elk.owl.interfaces.ElkAnnotationValue;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;

/**
 * Corresponds to a <a href="http://www.w3.org/TR/owl2-syntax/#Literals"
 * >Literal<a> in the OWL 2 specification.
 * 
 * The OWL specification distinguishes three specific kinds of literals: typed
 * literals, plain literals without language tag, and plain literals with
 * language tag. However, plain literals are identified with typed literals of
 * type "rdf:PlainLiteral" where the language tag is represented as part of the
 * lexical form. For example, "Some text"@de is represented as
 * "Some text@de"^^rdf:PlainLiteral and "Another text" is represented as
 * "Another text@"^^rdf:PlainLiteral. These forms are considered structurally
 * identical.
 * 
 * Therefore, all literals can be considered as consisting of a lexical form and
 * a datatype.
 * 
 * Also note that the semantic interpretation of literals is not part of the
 * structural model of OWL. For example, the literals "1"^^xsd:integer,
 * "+1"^^xsd:integer, and "1"^^xsd:shortint are all different.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkLiteral extends ElkObject, ElkAnnotationValue {

	/**
	 * Get the lexical form of this literal.
	 * 
	 * @return The lexical form of this literal.
	 */
	public String getLexicalForm();
	
	/**
	 * Get the datatype of this literal. Note that "untyped" literals use the
	 * datatype rdf:PlainLiteral in OWL.
	 * 
	 * @return The datatype of this literal.
	 */
	public ElkDatatype getDatatype();

	/**
	 * Accept an {@link ElkLiteralVisitor}.
	 * 
	 * @param visitor
	 *            the visitor that can work with this axiom type
	 * @return the output of the visitor
	 */
	public <O> O accept(ElkLiteralVisitor<O> visitor);

}
