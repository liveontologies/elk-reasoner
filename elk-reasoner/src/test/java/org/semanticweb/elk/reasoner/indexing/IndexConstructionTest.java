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
package org.semanticweb.elk.reasoner.indexing;

import junit.framework.TestCase;

import org.semanticweb.elk.syntax.ElkAxiomProcessor;
import org.semanticweb.elk.syntax.ElkClass;
import org.semanticweb.elk.syntax.ElkClassExpression;
import org.semanticweb.elk.syntax.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.ElkObjectProperty;
import org.semanticweb.elk.syntax.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.ElkSubClassOfAxiom;

public class IndexConstructionTest extends TestCase {

	public IndexConstructionTest(String testName) {
		super(testName);
	}

	public void testIndexer() {
		ElkClass a = ElkClass.create("A");
		ElkClass b = ElkClass.create("B");
		ElkClass c = ElkClass.create("C");
		ElkClass d = ElkClass.create("D");
		ElkObjectProperty r = ElkObjectProperty.create("R");
		
		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();
		ElkAxiomProcessor deleter = index.getAxiomDeleter();
		
		inserter.process(ElkSubClassOfAxiom.create(ElkObjectIntersectionOf.create(a, b), d));
		inserter.process(ElkEquivalentClassesAxiom.create(ElkObjectSomeValuesFrom.create(r, c), a));
		
		IndexedClassExpression A = index.getIndexed(a);
		IndexedClassExpression B = index.getIndexed(b);
		IndexedClassExpression C = index.getIndexed(c);
		IndexedObjectProperty R = index.getIndexed(r);
		assertEquals(2, A.negativeOccurrenceNo);
		assertEquals(1, A.positiveOccurrenceNo);
		assertEquals(1, B.negativeOccurrenceNo);
		assertEquals(0, B.positiveOccurrenceNo);
		assertEquals(1, C.negativeOccurrenceNo);
		assertEquals(1, C.positiveOccurrenceNo);
		assertEquals(2, R.occurrenceNo);
		assertTrue(A.getNegConjunctionsByConjunct().containsKey(B));
		assertTrue(C.getNegExistentials().get(0).getRelation() == R);
		
		deleter.process(ElkSubClassOfAxiom.create(c, a));
		deleter.process(ElkEquivalentClassesAxiom.create(ElkObjectSomeValuesFrom.create(r, c), a));
		
		assertEquals(1, A.negativeOccurrenceNo);
		assertEquals(0, A.positiveOccurrenceNo);
		assertEquals(0, R.occurrenceNo);
		assertNull(C.getNegExistentials());
		assertNull(index.getIndexed(c));
		assertNull(index.getIndexed(r));
		
		deleter.process(ElkSubClassOfAxiom.create(ElkObjectIntersectionOf.create(a, b), d));
		assertEquals(0, A.negativeOccurrenceNo);
		assertNull(A.getNegConjunctionsByConjunct());
		assertNull(index.getIndexed(a));
	}
	
	public void testConjunctionSharing() {
		ElkClass a = ElkClass.create("A");
		ElkClass b = ElkClass.create("B");
		ElkClass c = ElkClass.create("C");
		ElkClass d = ElkClass.create("D");
		
		ElkClassExpression x = ElkObjectIntersectionOf.create(a, b, c);
		ElkClassExpression y = ElkObjectIntersectionOf.create(ElkObjectIntersectionOf.create(b, a), c);
		
		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();
		
		inserter.process(ElkSubClassOfAxiom.create(x, d));
		inserter.process(ElkSubClassOfAxiom.create(y, d));
		
		assertSame(index.getIndexed(x), index.getIndexed(y));
		assertEquals(2, index.getIndexed(x).getToldSuperClassExpressions().size());
	}

}
