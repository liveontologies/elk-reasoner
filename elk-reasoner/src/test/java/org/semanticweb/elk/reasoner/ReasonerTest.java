/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.syntax.*;

import junit.framework.TestCase;

public class ReasonerTest extends TestCase {
	public ReasonerTest( String testName ) {
		super( testName );
	}
	
	public void testIndexer() {
		ElkClassExpression human = ElkClass.create("Human");
		ElkObjectPropertyExpression has = ElkObjectProperty.create("has");
		ElkClassExpression heart = ElkClass.create("Heart");
		ElkClassExpression organ = ElkClass.create("Organ");
		ElkClassExpression heartAndOrgan = ElkObjectIntersectionOf.create(heart, organ);
		ElkClassExpression hasHeartAndOrgan = ElkObjectSomeValuesFrom.create(has, heartAndOrgan);		   

		Indexer indexer = new Indexer();
		indexer.addClassAxiom(ElkSubClassOfAxiom.create(human, hasHeartAndOrgan));		   

		assertFalse(indexer.getConcept(heart) == null);
		assertTrue(indexer.getConcept(heartAndOrgan).getToldSuperConcepts().contains(indexer.getConcept(heart)));
		assertTrue(indexer.getConcept(heartAndOrgan).getToldSuperConcepts().contains(indexer.getConcept(organ)));
		assertTrue(indexer.getConcept(human).getToldSuperConcepts().contains(indexer.getConcept(hasHeartAndOrgan)));

		assertTrue(indexer.getConcept(heart).getConjunctions().isEmpty());

		indexer.addClassAxiom(ElkEquivalentClassesAxiom.create(human, hasHeartAndOrgan));

		assertTrue(indexer.getConcept(heartAndOrgan).getToldSuperConcepts().size() == 2);
		assertFalse(indexer.getConcept(heart).getConjunctions().isEmpty());
		assertNotSame(indexer.getConcept(human), indexer.getConcept(hasHeartAndOrgan));

		ElkClassExpression heart2 = ElkClass.create("Heart2");
		indexer.addClassAxiom(ElkEquivalentClassesAxiom.create(heart, heart2));

		assertSame(indexer.getConcept(heart), indexer.getConcept(heart2));
	}

	public void testExistentials() {
		ElkClass a = ElkClass.create("A");
		ElkClass b = ElkClass.create("B");
		ElkClass c = ElkClass.create("C");
		ElkClass d = ElkClass.create("D");
		ElkObjectProperty r = ElkObjectProperty.create("R");
		ElkObjectProperty s = ElkObjectProperty.create("S");
		
		Reasoner reasoner = new Reasoner();
		reasoner.indexer.addClassAxiom(ElkEquivalentClassesAxiom.create(b, c));
		reasoner.indexer.addClassAxiom(ElkSubClassOfAxiom.create(a, ElkObjectSomeValuesFrom.create(r, b)));
		reasoner.indexer.addClassAxiom(ElkSubClassOfAxiom.create(ElkObjectSomeValuesFrom.create(s, c), d));
		reasoner.indexer.addObjectPropertyAxiom(ElkSubObjectPropertyOfAxiom.create(r, s));
		
		Concept A = reasoner.indexer.getConcept(a);
		Concept D = reasoner.indexer.getConcept(d);
		reasoner.indexer.reduceRoleHierarchy();
		reasoner.saturator.saturate(A);
		assertTrue("A contains D", reasoner.saturator.getContext(A).derived.contains(D));
	}
	
	public void testConjunctions() {
		ElkClass a = ElkClass.create("A");
		ElkClass b = ElkClass.create("B");
		ElkClass c = ElkClass.create("C");
		ElkClass d = ElkClass.create("D");
		
		Reasoner reasoner = new Reasoner();
		reasoner.indexer.addClassAxiom(ElkSubClassOfAxiom.create(a, b));
		reasoner.indexer.addClassAxiom(ElkSubClassOfAxiom.create(a, c));
		reasoner.indexer.addClassAxiom(ElkSubClassOfAxiom.create(ElkObjectIntersectionOf.create(b, c), d));
		
		Concept A = reasoner.indexer.getConcept(a);
		Concept B = reasoner.indexer.getConcept(b);
		Concept C = reasoner.indexer.getConcept(c);
		Concept D = reasoner.indexer.getConcept(d);
		Concept I = reasoner.indexer.getConcept(ElkObjectIntersectionOf.create(b, c));
		
		assertTrue("A SubClassOf B", A.getToldSuperConcepts().contains(B));
		assertTrue("A SubClassOf C", A.getToldSuperConcepts().contains(C));
		assertFalse("A SubClassOf D", A.getToldSuperConcepts().contains(D));
		assertTrue("I SubClassOf D", I.getToldSuperConcepts().contains(D));
		
		Context context = reasoner.saturator.getContext(A);
		reasoner.saturator.saturate(A);
		assertTrue("A contains A", context.derived.contains(A));
		assertTrue("A contains B", context.derived.contains(B));
		assertTrue("A contains C", context.derived.contains(C));
		assertTrue("A contains I", context.derived.contains(I));
		assertTrue("A contains D", context.derived.contains(D));
	}
	
}