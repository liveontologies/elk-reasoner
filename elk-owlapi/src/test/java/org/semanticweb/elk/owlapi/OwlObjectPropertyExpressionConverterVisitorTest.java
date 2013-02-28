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
package org.semanticweb.elk.owlapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owlapi.wrapper.OwlObjectPropertyExpressionConverterVisitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

/**
 * Testing correctness of {@link OWLObjectPropertyExpressionConverterVisitor} 
 * 
 * @author Frantisek Simancik
 *
 */
public class OwlObjectPropertyExpressionConverterVisitorTest {
	/**
	 * Testing correctness of converting nested inverses
	 */
	@Test
	public void testNestedInverses() {
		OWLDataFactory factory = new OWLDataFactoryImpl();
		OWLObjectProperty r1 = factory.getOWLObjectProperty(IRI.create("R"));
		OWLObjectPropertyExpression s1 = factory.getOWLObjectInverseOf(factory.getOWLObjectInverseOf(r1));
		
		ElkObjectPropertyExpression r2 = r1.accept(OwlObjectPropertyExpressionConverterVisitor.getInstance());
		ElkObjectPropertyExpression s2 = s1.accept(OwlObjectPropertyExpressionConverterVisitor.getInstance());

		assertTrue(r2 instanceof ElkObjectProperty);
		assertTrue(s2 instanceof ElkObjectProperty);
		assertEquals(((ElkObjectProperty) r2).getIri(), ((ElkObjectProperty) s2).getIri());
	}
}
