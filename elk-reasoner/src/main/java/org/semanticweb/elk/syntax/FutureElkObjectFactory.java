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
/**
 * @author Yevgeny Kazakov, May 27, 2011
 */
package org.semanticweb.elk.syntax;

import java.util.List;
import java.util.concurrent.Future;

import org.semanticweb.elk.syntax.interfaces.ElkAnonymousIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkClass;
import org.semanticweb.elk.syntax.interfaces.ElkClassExpression;
import org.semanticweb.elk.syntax.interfaces.ElkDeclarationAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkDisjointUnionAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkEntity;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentClassesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkEquivalentObjectPropertiesAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasSelf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectHasValue;
import org.semanticweb.elk.syntax.interfaces.ElkObjectIntersectionOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectInverseOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectOneOf;
import org.semanticweb.elk.syntax.interfaces.ElkObjectProperty;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyChain;
import org.semanticweb.elk.syntax.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.syntax.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.syntax.interfaces.ElkSubObjectPropertyOfAxiom;
import org.semanticweb.elk.syntax.interfaces.ElkTransitiveObjectPropertyAxiom;

/**
 * Factory for ElkObjects designed to delay the creation of Elk objects. The
 * constructors return "lazy" representation of expressions by wrapping into an
 * appropriate instance of the {@link Future} interface. The objects themselves
 * can later be retrieved by calling the <tt>get</tt> method. Typically, such
 * objects can be created concurrently in another thread using a lookup table to
 * achieve maximal sharing and improved performance.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public interface FutureElkObjectFactory {

	/**
	 * Returns a dummy {@link Future} representation for an already constructed
	 * object. When the method <tt>get</tt> is called for the result, it should
	 * return the original input object.
	 * 
	 * @param <E>
	 *            the type of the object
	 * @param object
	 *            the object
	 * @return the {@link Future} wrapping of the object.
	 */
	public <E> Future<E> getFuture(E object);

	public Future<ElkObjectProperty> getFutureElkObjectProperty(
			String objectPropertyIri);

	public Future<ElkObjectInverseOf> getFutureElkObjectInverseOf(
			Future<? extends ElkObjectProperty> futureObjectProperty);

	public Future<ElkNamedIndividual> getFutureElkNamedIndividual(
			String individualIri);

	public Future<ElkAnonymousIndividual> getFutureElkAnonymousIndividual(
			String individualNodeId);

	public Future<ElkClass> getFutureElkClass(String classIri);

	public Future<ElkObjectPropertyChain> getFutureElkObjectPropertyChain(
			List<Future<? extends ElkObjectPropertyExpression>> futureObjectPropertyExpressions);

	public Future<ElkObjectIntersectionOf> getFutureElkObjectIntersectionOf(
			List<Future<? extends ElkClassExpression>> futureClassExpressions);

	public Future<ElkObjectIntersectionOf> getFutureElkObjectIntersectionOf(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions);

	public Future<ElkObjectOneOf> getFutureElkObjectOneOf(
			List<Future<? extends ElkIndividual>> futureIndividuals);

	public Future<ElkObjectOneOf> getFutureElkObjectOneOf(
			Future<? extends ElkIndividual> firstFutureIndividual,
			Future<? extends ElkIndividual>... otherFutureIndividuals);

	public Future<ElkObjectSomeValuesFrom> getFutureElkObjectSomeValuesFrom(
			Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
			Future<? extends ElkClassExpression> futureClassExpression);

	public Future<ElkObjectHasValue> getFutureElkObjectHasValue(
			Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
			Future<? extends ElkIndividual> futureIndividual);

	public Future<ElkObjectHasSelf> getFutureElkObjectHasSelf(
			Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression);

	public Future<ElkDeclarationAxiom> getFutureElkDeclarationAxiom(
			Future<? extends ElkEntity> futureEntity);

	public Future<ElkTransitiveObjectPropertyAxiom> getFutureElkTransitiveObjectPropertyAxiom(
			Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression);

	public Future<ElkEquivalentObjectPropertiesAxiom> getFutureElkEquivalentObjectPropertiesAxiom(
			List<Future<? extends ElkObjectPropertyExpression>> futureEquivalentObjectPropertyExpressions);

	public Future<ElkEquivalentObjectPropertiesAxiom> getFutureElkEquivalentObjectPropertiesAxiom(
			Future<? extends ElkObjectPropertyExpression> firstFutureObjectPropertyExpression,
			Future<? extends ElkObjectPropertyExpression> secondFutureObjectPropertyExpression,
			Future<? extends ElkObjectPropertyExpression>... otherFutureObjectPropertyExpressions);
	
	public Future<ElkDisjointObjectPropertiesAxiom> getFutureElkDisjointObjectPropertiesAxiom(
			List<Future<? extends ElkObjectPropertyExpression>> futureDisjointObjectPropertyExpressions);

	public Future<ElkDisjointObjectPropertiesAxiom> getFutureElkDisjointObjectPropertiesAxiom(
			Future<? extends ElkObjectPropertyExpression> firstFutureObjectPropertyExpression,
			Future<? extends ElkObjectPropertyExpression> secondFutureObjectPropertyExpression,
			Future<? extends ElkObjectPropertyExpression>... otherFutureObjectPropertyExpressions);

	public Future<ElkSubObjectPropertyOfAxiom> getFutureElkSubObjectPropertyOfAxiom(
			Future<? extends ElkSubObjectPropertyExpression> futureSubObjectPropertyExpression,
			Future<? extends ElkObjectPropertyExpression> futureSuperObjectPropertyExpression);

	public Future<ElkEquivalentClassesAxiom> getFutureElkEquivalentClassesAxiom(
			List<Future<? extends ElkClassExpression>> futureEquivalentClassExpressions);

	public Future<ElkEquivalentClassesAxiom> getFutureElkEquivalentClassesAxiom(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions);

	public Future<ElkDisjointClassesAxiom> getFutureElkDisjointClassesAxiom(
			List<Future<? extends ElkClassExpression>> futureClassExpressions);

	public Future<ElkDisjointClassesAxiom> getFutureElkDisjointClassesAxiom(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions);

	public Future<ElkDisjointUnionAxiom> getFutureElkDisjointUnionAxiom(
			List<Future<? extends ElkClassExpression>> futureClassExpressions);

	public Future<ElkDisjointUnionAxiom> getFutureElkDisjointUnionAxiom(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions);

	public Future<ElkSubClassOfAxiom> getFutureElkSubClassOfAxiom(
			Future<? extends ElkClassExpression> futureSubClassExpression,
			Future<? extends ElkClassExpression> futureSuperClassExpression);

}
