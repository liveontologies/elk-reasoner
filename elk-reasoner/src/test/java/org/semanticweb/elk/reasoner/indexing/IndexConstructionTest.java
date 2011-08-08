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
import org.semanticweb.elk.syntax.implementation.ElkClassImpl;
import org.semanticweb.elk.syntax.implementation.ElkEquivalentClassesAxiomImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectIntersectionOfImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectPropertyImpl;
import org.semanticweb.elk.syntax.implementation.ElkObjectSomeValuesFromImpl;
import org.semanticweb.elk.syntax.implementation.ElkSubClassOfAxiomImpl;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;

public class IndexConstructionTest extends TestCase {

	public IndexConstructionTest(String testName) {
		super(testName);
	}

	public void testIndexer() {
		ElkClass a = ElkClassImpl.create("A");
		ElkClass b = ElkClassImpl.create("B");
		ElkClass c = ElkClassImpl.create("C");
		ElkClass d = ElkClassImpl.create("D");
		ElkObjectProperty r = ElkObjectPropertyImpl.create("R");

		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();
		ElkAxiomProcessor deleter = index.getAxiomDeleter();

		inserter.process(ElkSubClassOfAxiomImpl.create(
				ElkObjectIntersectionOfImpl.create(a, b), d));
		inserter.process(ElkEquivalentClassesAxiomImpl.create(
				ElkObjectSomeValuesFromImpl.create(r, c), a));

		IndexedClassExpression A = index.getIndexedClassExpression(a);
		IndexedClassExpression B = index.getIndexedClassExpression(b);
		IndexedClassExpression C = index.getIndexedClassExpression(c);
		IndexedObjectProperty R = index.getIndexedObjectProperty(r);
		assertEquals(2, A.negativeOccurrenceNo);
		assertEquals(1, A.positiveOccurrenceNo);
		assertEquals(1, B.negativeOccurrenceNo);
		assertEquals(0, B.positiveOccurrenceNo);
		assertEquals(1, C.negativeOccurrenceNo);
		assertEquals(1, C.positiveOccurrenceNo);
		assertEquals(2, R.occurrenceNo);
		assertTrue(A.getNegConjunctionsByConjunct().containsKey(B));
		assertTrue(C.getNegExistentials().get(0).getRelation() == R);

		deleter.process(ElkEquivalentClassesAxiomImpl.create(
				ElkObjectSomeValuesFromImpl.create(r, c), a));

		assertEquals(1, A.negativeOccurrenceNo);
		assertEquals(0, A.positiveOccurrenceNo);
		assertEquals(0, R.occurrenceNo);
		assertNull(C.getNegExistentials());
		assertNull(index.getIndexedEntity(c));
		assertNull(index.getIndexedEntity(r));

		deleter.process(ElkSubClassOfAxiomImpl.create(
				ElkObjectIntersectionOfImpl.create(a, b), d));
		assertEquals(0, A.negativeOccurrenceNo);
		assertNull(A.getNegConjunctionsByConjunct());
		assertNull(index.getIndexedEntity(a));
	}

	public void testConjunctionSharing() {
		ElkClass a = ElkClassImpl.create("A");
		ElkClass b = ElkClassImpl.create("B");
		ElkClass c = ElkClassImpl.create("C");
		ElkClass d = ElkClassImpl.create("D");

		ElkClassExpression x = ElkObjectIntersectionOfImpl.create(a, b, c);
		ElkClassExpression y = ElkObjectIntersectionOfImpl.create(
				ElkObjectIntersectionOfImpl.create(b, a), c);

		OntologyIndex index = new SerialOntologyIndex();
		ElkAxiomProcessor inserter = index.getAxiomInserter();

		inserter.process(ElkSubClassOfAxiomImpl.create(x, d));
		inserter.process(ElkSubClassOfAxiomImpl.create(y, d));

		assertSame(index.getIndexedClassExpression(x),
				index.getIndexedClassExpression(y));
		assertEquals(2, index.getIndexedClassExpression(x)
				.getToldSuperClassExpressions().size());
	}

}
