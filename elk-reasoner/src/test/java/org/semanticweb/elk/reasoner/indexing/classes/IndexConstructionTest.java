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
package org.semanticweb.elk.reasoner.indexing.classes;

import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;

import junit.framework.TestCase;

public class IndexConstructionTest extends TestCase {

	final ElkObject.Factory objectFactory = new ElkObjectEntityRecyclingFactory();

	public IndexConstructionTest(String testName) {
		super(testName);
	}

	public void testNothing() {
		// TODO: remove once there is at least one real test
	}

	// public void testIndexer() {
	// ElkClass a = objectFactory.getClass(new ElkFullIri("A"));
	// ElkClass b = objectFactory.getClass(new ElkFullIri("B"));
	// ElkClass c = objectFactory.getClass(new ElkFullIri("C"));
	// ElkClass d = objectFactory.getClass(new ElkFullIri("D"));
	// ElkObjectProperty r = objectFactory.getObjectProperty(new ElkFullIri(
	// "R"));
	//
	// OntologyIndex index = new OntologyIndexImpl();
	// ElkAxiomProcessor inserter = index.getAxiomInserter();
	// ElkAxiomProcessor deleter = index.getAxiomDeleter();
	//
	// inserter.visit(objectFactory.getSubClassOfAxiom(
	// objectFactory.getObjectIntersectionOf(a, b), d));
	// inserter.visit(objectFactory.getEquivalentClassesAxiom(
	// objectFactory.getObjectSomeValuesFrom(r, c), a));
	//
	// IndexedClassExpression A = index.getIndexed(a);
	// IndexedClassExpression B = index.getIndexed(b);
	// IndexedClassExpression C = index.getIndexed(c);
	// IndexedPropertyChain R = index.getIndexed(r);
	// assertEquals(2, A.negativeOccurrenceNo);
	// assertEquals(1, A.positiveOccurrenceNo);
	// assertEquals(1, B.negativeOccurrenceNo);
	// assertEquals(0, B.positiveOccurrenceNo);
	// assertEquals(1, C.negativeOccurrenceNo);
	// assertEquals(1, C.positiveOccurrenceNo);
	// assertEquals(2, R.occurrenceNo);
	// assertTrue(A.getNegConjunctionsByConjunct().containsKey(B));
	// assertSame(C.getNegExistentials().iterator().next().getRelation(), R);
	//
	// deleter.visit(objectFactory.getEquivalentClassesAxiom(
	// objectFactory.getObjectSomeValuesFrom(r, c), a));
	//
	// assertEquals(1, A.negativeOccurrenceNo);
	// assertEquals(0, A.positiveOccurrenceNo);
	// assertEquals(0, R.occurrenceNo);
	// assertNull(C.getNegExistentials());
	// assertNull(index.getIndexed(c));
	// assertNull(index.getIndexed(r));
	//
	// deleter.visit(objectFactory.getSubClassOfAxiom(
	// objectFactory.getObjectIntersectionOf(a, b), d));
	// assertEquals(0, A.negativeOccurrenceNo);
	// assertNull(A.getNegConjunctionsByConjunct());
	// assertNull(index.getIndexed(a));
	// }

	// public void testDisjoints() {
	// ElkClass a = objectFactory.getClass(new ElkFullIri("A"));
	// ElkClass b = objectFactory.getClass(new ElkFullIri("B"));
	// ElkClass c = objectFactory.getClass(new ElkFullIri("C"));
	//
	// ElkDisjointClassesAxiom axiom = objectFactory.getDisjointClassesAxiom(
	// a, b, c, b);
	//
	// OntologyIndex index = new OntologyIndexImpl();
	// ElkAxiomProcessor inserter = index.getAxiomInserter();
	// ElkAxiomProcessor deleter = index.getAxiomDeleter();
	//
	// inserter.visit(axiom);
	//
	// IndexedClassExpression A = index.getIndexed(a);
	// IndexedClassExpression B = index.getIndexed(b);
	// IndexedClassExpression C = index.getIndexed(c);
	//
	// assertEquals(1, A.getDisjointnessAxioms().size());
	// assertEquals(2, B.getDisjointnessAxioms().size());
	// assertEquals(1, C.getDisjointnessAxioms().size());
	//
	// IndexedDisjointnessAxiom disAxiom = A.getDisjointnessAxioms().get(0);
	// assertSame(disAxiom, B.getDisjointnessAxioms().get(0));
	// assertSame(disAxiom, B.getDisjointnessAxioms().get(1));
	// assertSame(disAxiom, C.getDisjointnessAxioms().get(0));
	//
	// deleter.visit(axiom);
	//
	// assertNull(A.getDisjointnessAxioms());
	// assertNull(B.getDisjointnessAxioms());
	// assertNull(C.getDisjointnessAxioms());
	//
	// assertNull(index.getIndexed(a));
	// assertNull(index.getIndexed(b));
	// assertNull(index.getIndexed(c));
	// }

	// public void testConjunctionSharing() {
	// ElkClass a = objectFactory.getClass(new ElkFullIri("A"));
	// ElkClass b = objectFactory.getClass(new ElkFullIri("B"));
	// ElkClass c = objectFactory.getClass(new ElkFullIri("C"));
	// ElkClass d = objectFactory.getClass(new ElkFullIri("D"));
	//
	// ElkClassExpression x = objectFactory.getObjectIntersectionOf(a, b, c);
	// ElkClassExpression y = objectFactory.getObjectIntersectionOf(
	// objectFactory.getObjectIntersectionOf(b, a), c);
	//
	// OntologyIndex index = new OntologyIndexImpl();
	// ElkAxiomProcessor inserter = index.getAxiomInserter();
	//
	// inserter.visit(objectFactory.getSubClassOfAxiom(x, d));
	// inserter.visit(objectFactory.getSubClassOfAxiom(y, d));
	//
	// assertSame(index.getIndexed(x), index.getIndexed(y));
	// assertEquals(2, index.getIndexed(x).getToldSuperClassExpressions()
	// .size());
	// }

}
