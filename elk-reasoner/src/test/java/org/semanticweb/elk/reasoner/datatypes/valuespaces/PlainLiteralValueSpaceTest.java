/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class PlainLiteralValueSpaceTest extends AbstractValueSpaceTest {

	@Test
	public void emptySpaces() throws Exception {
		
	}

	@Test
	public void nonEmptySpaces() throws Exception {
	}

	@Test
	public void contains() throws Exception {
		assertTrue(contains(
				"DatatypeRestriction(rdf:PlainLiteral xsd:minLength \"3\"^^xsd:integer xsd:maxLength \"3\"^^xsd:integer)",
				"DataOneOf(\"Foo\"@us)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:string xsd:length \"3\"^^xsd:integer)",
				"DatatypeRestriction(xsd:string xsd:pattern \"[A-Z]oo\")"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:string xsd:pattern \"[a-z]+\")",
				"DatatypeRestriction(rdf:PlainLiteral xsd:pattern \"foo*\")"));
		assertTrue(contains(
				"DatatypeRestriction(rdf:PlainLiteral xsd:minLength \"3\"^^xsd:integer xsd:maxLength \"3\"^^xsd:integer)",
				"DatatypeRestriction(xsd:string xsd:length \"3\"^^xsd:integer)"));
	}

	@Test
	public void doesNotContain() throws Exception {
		assertFalse(contains(
				"DatatypeRestriction(xsd:string xsd:minLength \"3\"^^xsd:integer xsd:maxLength \"10\"^^xsd:integer)",
				"DataOneOf(\"Foo\"@us)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:string xsd:length \"3\"^^xsd:integer)",
				"DatatypeRestriction(rdf:PlainLiteral xsd:minLength \"3\"^^xsd:integer xsd:maxLength \"3\"^^xsd:integer)"));
	}
	
	@Test
	public void facetRestrictionValues() throws Exception {
	}
	
	
}
