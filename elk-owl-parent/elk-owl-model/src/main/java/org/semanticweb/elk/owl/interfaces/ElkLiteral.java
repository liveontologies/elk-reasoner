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
package org.semanticweb.elk.owl.interfaces;

import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;

/**
 * Corresponds to a <a href="http://www.w3.org/TR/owl2-syntax/#Literals"
 * >Literal<a> in the OWL 2 specification.
 * 
 * The OWL specification distinguishes three specific kinds of literals: typed
 * literals, plain literals without language tag, and plain literals with
 * language tag. However, plain literals without language tag are the same as
 * typed literals of type "rdf:PlainLiteral" i.e. they are considered
 * structurally identical. Thus, we really have two kinds of literals: typed
 * ones with and without language tag. The relevant constraint is: every literal
 * is either of type "rdf:PlainLiteral" or has an empty language tag. This
 * cannot be captured in an interface declaration, hence we only have one
 * interface for literals and leave it to the implementation to enforce the
 * restriction on language tag usage.
 * 
 * Also note that the semantic interpretation of literals is not part of the
 * structural model of OWL. For example, the literals "1"^^xsd:integer,
 * "+1"^^xsd:integer, and "1"^^xsd:shortint are all different.
 * 
 * @author Markus Kroetzsch
 */
public interface ElkLiteral extends ElkObject {

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
	 * Get the language tag of this literal or an empty string if no such tag is
	 * given. See the interface documentation for further information.
	 * 
	 * @return The language tag of this literal, if any.
	 */
	public String getLanguageTag();

	/**
	 * Accept an ElkLiteralVisitor.
	 * 
	 * @param visitor
	 * @return
	 */
	public <O> O accept(ElkLiteralVisitor<O> visitor);

}
