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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An implementation of {@link FutureElkObjectFactory} interface where the
 * {@link Future} objects simply cache all corresponding constructors of Elk
 * objects when created and the objects are constructed when <tt>get</tt> method
 * is called.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class FutureElkObjectFactoryImpl implements FutureElkObjectFactory {

	private <E> List<E> unfutureList(List<Future<? extends E>> futureList)
			throws InterruptedException, ExecutionException {
		int size = futureList.size();
		List<E> result = new ArrayList<E>(size);
		for (int i = 0; i < size; i++)
			result.add(futureList.get(i).get());
		return result;
	}

	/**
	 * An abstract class to simplify the implementation of the {@link Future}
	 * interface for delayed creation of objects.
	 * 
	 * @author Yevgeny Kazakov
	 * 
	 * @param <E>
	 */
	abstract class DelayedConstructor<E> implements Future<E> {

		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		public boolean isCancelled() {
			return false;
		}

		public boolean isDone() {
			return true;
		}

		public abstract E get() throws InterruptedException, ExecutionException;

		public E get(long timeout, TimeUnit unit) throws InterruptedException,
				ExecutionException, TimeoutException {
			return get();
		}
	}

	/**
	 * Dummy implementation for the Future class which simply saves the given
	 * element in a field.
	 * 
	 * @author Yevgeny Kazakov
	 * 
	 * @param <E>
	 *            the type of the element
	 */
	class DummyFuture<E> extends DelayedConstructor<E> {
		private final E element;

		DummyFuture(final E element) {
			this.element = element;
		}

		public E get() {
			return element;
		}
	}

	public <E> Future<E> getFuture(E e) {
		return new DummyFuture<E>(e);
	}

	class FutureElkObjectProperty extends DelayedConstructor<ElkObjectProperty> {

		protected final String objectPropertyIri;

		FutureElkObjectProperty(final String objectPropertyIri) {
			this.objectPropertyIri = objectPropertyIri;
		}

		public ElkObjectProperty get() {
			return ElkObjectProperty.create(objectPropertyIri);
		}
	}

	public Future<ElkObjectProperty> getFutureElkObjectProperty(
			final String objectPropertyIri) {
		return new FutureElkObjectProperty(objectPropertyIri);
	}

	class FutureElkObjectInverseOf extends
			DelayedConstructor<ElkObjectInverseOf> {

		protected final Future<? extends ElkObjectProperty> futureObjectProperty;

		FutureElkObjectInverseOf(
				Future<? extends ElkObjectProperty> futureObjectProperty) {
			this.futureObjectProperty = futureObjectProperty;
		}

		public ElkObjectInverseOf get() throws InterruptedException,
				ExecutionException {
			return ElkObjectInverseOf.create(futureObjectProperty.get());
		}
	}

	public Future<ElkObjectInverseOf> getFutureElkObjectInverseOf(
			Future<? extends ElkObjectProperty> futureObjectProperty) {
		return new FutureElkObjectInverseOf(futureObjectProperty);
	}

	class FutureElkObjectPropertyChain extends
			DelayedConstructor<ElkObjectPropertyChain> {

		protected final List<Future<? extends ElkObjectPropertyExpression>> futureObjectPropertyExpressions;

		FutureElkObjectPropertyChain(
				final List<Future<? extends ElkObjectPropertyExpression>> futureObjectPropertyExpressions) {
			this.futureObjectPropertyExpressions = futureObjectPropertyExpressions;
		}

		public ElkObjectPropertyChain get() throws InterruptedException,
				ExecutionException {
			return ElkObjectPropertyChain
					.create(unfutureList(futureObjectPropertyExpressions));
		}
	}

	public Future<ElkObjectPropertyChain> getFutureElkObjectPropertyChain(
			final List<Future<? extends ElkObjectPropertyExpression>> futureObjectPropertyExpressions) {
		return new FutureElkObjectPropertyChain(futureObjectPropertyExpressions);
	}

	class FutureElkNamedIndividual extends
			DelayedConstructor<ElkNamedIndividual> {

		protected final String individualIri;

		FutureElkNamedIndividual(final String individualIri) {
			this.individualIri = individualIri;
		}

		public ElkNamedIndividual get() {
			return ElkNamedIndividual.create(individualIri);
		}

	}

	public Future<ElkNamedIndividual> getFutureElkNamedIndividual(
			final String individualIri) {
		return new FutureElkNamedIndividual(individualIri);
	}

	class FutureElkAnonymousIndividual extends
			DelayedConstructor<ElkAnonymousIndividual> {

		protected final String individualNodeId;

		FutureElkAnonymousIndividual(final String individualNodeId) {
			this.individualNodeId = individualNodeId;
		}

		public ElkAnonymousIndividual get() {
			return ElkAnonymousIndividual.create(individualNodeId);
		}

	}

	public Future<ElkAnonymousIndividual> getFutureElkAnonymousIndividual(
			final String individualNodeId) {
		return new FutureElkAnonymousIndividual(individualNodeId);
	}

	class FutureElkClass extends DelayedConstructor<ElkClass> {

		protected final String classIri;

		FutureElkClass(final String classIri) {
			this.classIri = classIri;
		}

		public ElkClass get() {
			return ElkClass.create(classIri);
		}

	}

	public Future<ElkClass> getFutureElkClass(final String classIri) {
		return new FutureElkClass(classIri);
	}

	class FutureElkObjectIntersectionOf extends
			DelayedConstructor<ElkObjectIntersectionOf> {

		protected final List<Future<? extends ElkClassExpression>> futureClassExpressions;

		FutureElkObjectIntersectionOf(
				List<Future<? extends ElkClassExpression>> futureClassExpressions) {
			this.futureClassExpressions = futureClassExpressions;
		}

		public ElkObjectIntersectionOf get() throws InterruptedException,
				ExecutionException {
			return ElkObjectIntersectionOf
					.create(unfutureList(futureClassExpressions));
		}

	}

	public Future<ElkObjectIntersectionOf> getFutureElkObjectIntersectionOf(
			final List<Future<? extends ElkClassExpression>> futureClassExpressions) {
		return new FutureElkObjectIntersectionOf(futureClassExpressions);
	}

	public Future<ElkObjectIntersectionOf> getFutureElkObjectIntersectionOf(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions) {
		List<Future<? extends ElkClassExpression>> futureClassExpressions = new ArrayList<Future<? extends ElkClassExpression>>(
				2 + otherFutureClassExpressions.length);
		futureClassExpressions.add(firstFutureClassExpression);
		futureClassExpressions.add(secondFutureClassExpression);
		for (int i = 0; i > otherFutureClassExpressions.length; i++) {
			futureClassExpressions.add(otherFutureClassExpressions[i]);
		}
		return new FutureElkObjectIntersectionOf(futureClassExpressions);
	}

	class FutureElkObjectSomeValuesFrom extends
			DelayedConstructor<ElkObjectSomeValuesFrom> {
		protected final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression;
		protected final Future<? extends ElkClassExpression> futureClassExpression;

		FutureElkObjectSomeValuesFrom(
				final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
				final Future<? extends ElkClassExpression> futureClassExpression) {
			this.futureObjectPropertyExpression = futureObjectPropertyExpression;
			this.futureClassExpression = futureClassExpression;
		}

		public ElkObjectSomeValuesFrom get() throws InterruptedException,
				ExecutionException {
			return ElkObjectSomeValuesFrom.create(
					futureObjectPropertyExpression.get(),
					futureClassExpression.get());
		}

	}

	public Future<ElkObjectSomeValuesFrom> getFutureElkObjectSomeValuesFrom(
			final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
			final Future<? extends ElkClassExpression> futureClassExpression) {
		return new FutureElkObjectSomeValuesFrom(
				futureObjectPropertyExpression, futureClassExpression);
	}

	class FutureElkObjectHasValue extends
			DelayedConstructor<ElkObjectHasValue> {
		protected final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression;
		protected final Future<? extends ElkIndividual> futureIndividual;

		FutureElkObjectHasValue(
				final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
				final Future<? extends ElkIndividual> futureIndividual) {
			this.futureObjectPropertyExpression = futureObjectPropertyExpression;
			this.futureIndividual = futureIndividual;
		}

		public ElkObjectHasValue get() throws InterruptedException,
				ExecutionException {
			return ElkObjectHasValue.create(
					futureObjectPropertyExpression.get(),
					futureIndividual.get());
		}

	}

	public Future<ElkObjectHasValue> getFutureElkObjectHasValue(
			final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression,
			final Future<? extends ElkIndividual> futureIndividual) {
		return new FutureElkObjectHasValue(
				futureObjectPropertyExpression, futureIndividual);
	}

	class FutureElkDeclarationAxiom extends
			DelayedConstructor<ElkDeclarationAxiom> {

		protected final Future<? extends ElkEntity> futureEntity;

		FutureElkDeclarationAxiom(final Future<? extends ElkEntity> futureEntity) {
			this.futureEntity = futureEntity;
		}

		public ElkDeclarationAxiom get() throws InterruptedException,
				ExecutionException {
			return ElkDeclarationAxiom.create(futureEntity.get());
		}

	}

	public Future<ElkDeclarationAxiom> getFutureElkDeclarationAxiom(
			Future<? extends ElkEntity> futureEntity) {
		return new FutureElkDeclarationAxiom(futureEntity);
	}

	class FutureElkTransitiveObjectPropertyAxiom extends
			DelayedConstructor<ElkTransitiveObjectPropertyAxiom> {

		protected final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression;

		FutureElkTransitiveObjectPropertyAxiom(
				final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression) {
			this.futureObjectPropertyExpression = futureObjectPropertyExpression;
		}

		public ElkTransitiveObjectPropertyAxiom get()
				throws InterruptedException, ExecutionException {
			return ElkTransitiveObjectPropertyAxiom
					.create(futureObjectPropertyExpression.get());
		}

	}

	public Future<ElkTransitiveObjectPropertyAxiom> getFutureElkTransitiveObjectPropertyAxiom(
			final Future<? extends ElkObjectPropertyExpression> futureObjectPropertyExpression) {
		return new FutureElkTransitiveObjectPropertyAxiom(
				futureObjectPropertyExpression);
	}

	class FutureElkSubObjectPropertyOfAxiom extends
			DelayedConstructor<ElkSubObjectPropertyOfAxiom> {

		protected final Future<? extends ElkObjectPropertyExpression> futureSubObjectPropertyExpression;
		protected final Future<? extends ElkObjectPropertyExpression> futureSuperObjectPropertyExpression;

		FutureElkSubObjectPropertyOfAxiom(
				final Future<? extends ElkObjectPropertyExpression> futureSubObjectPropertyExpression,
				final Future<? extends ElkObjectPropertyExpression> futureSuperObjectPropertyExpression) {
			this.futureSubObjectPropertyExpression = futureSubObjectPropertyExpression;
			this.futureSuperObjectPropertyExpression = futureSuperObjectPropertyExpression;
		}

		public ElkSubObjectPropertyOfAxiom get() {
			try {
				return ElkSubObjectPropertyOfAxiom.create(
						futureSubObjectPropertyExpression.get(),
						futureSuperObjectPropertyExpression.get());
			} catch (InterruptedException e) {
				return null;
			} catch (ExecutionException e) {
				return null;
			}
		}

	}

	public Future<ElkSubObjectPropertyOfAxiom> getFutureElkSubObjectPropertyOfAxiom(
			final Future<? extends ElkObjectPropertyExpression> futureSubObjectPropertyExpression,
			final Future<? extends ElkObjectPropertyExpression> futureSuperObjectPropertyExpression) {
		return new FutureElkSubObjectPropertyOfAxiom(
				futureSubObjectPropertyExpression,
				futureSuperObjectPropertyExpression);
	}

	class FutureElkEquivalentClassesAxiom extends
			DelayedConstructor<ElkEquivalentClassesAxiom> {
		protected final List<Future<? extends ElkClassExpression>> futureEquivalentClassExpressions;

		FutureElkEquivalentClassesAxiom(
				final List<Future<? extends ElkClassExpression>> futureEquivalentClassExpressions) {
			this.futureEquivalentClassExpressions = futureEquivalentClassExpressions;
		}

		public ElkEquivalentClassesAxiom get() throws InterruptedException,
				ExecutionException {
			return ElkEquivalentClassesAxiom
					.create(unfutureList(futureEquivalentClassExpressions));
		}

	}

	public Future<ElkEquivalentClassesAxiom> getFutureElkEquivalentClassesAxiom(
			final List<Future<? extends ElkClassExpression>> futureEquivalentClassExpressions) {
		return new FutureElkEquivalentClassesAxiom(
				futureEquivalentClassExpressions);
	}

	public Future<ElkEquivalentClassesAxiom> getFutureElkEquivalentClassesAxiom(
			Future<? extends ElkClassExpression> firstFutureClassExpression,
			Future<? extends ElkClassExpression> secondFutureClassExpression,
			Future<? extends ElkClassExpression>... otherFutureClassExpressions) {
		List<Future<? extends ElkClassExpression>> futureEquivalentClassExpressions = new ArrayList<Future<? extends ElkClassExpression>>(
				2 + otherFutureClassExpressions.length);
		futureEquivalentClassExpressions.add(firstFutureClassExpression);
		futureEquivalentClassExpressions.add(secondFutureClassExpression);
		for (int i = 0; i > otherFutureClassExpressions.length; i++) {
			futureEquivalentClassExpressions
					.add(otherFutureClassExpressions[i]);
		}
		return new FutureElkEquivalentClassesAxiom(
				futureEquivalentClassExpressions);
	}

	class FutureElkSubClassOfAxiom extends
			DelayedConstructor<ElkSubClassOfAxiom> {

		protected final Future<? extends ElkClassExpression> futureSubClassExpression;
		protected final Future<? extends ElkClassExpression> futureSuperClassExpression;

		FutureElkSubClassOfAxiom(
				final Future<? extends ElkClassExpression> futureSubClassExpression,
				final Future<? extends ElkClassExpression> futureSuperClassExpression) {
			this.futureSubClassExpression = futureSubClassExpression;
			this.futureSuperClassExpression = futureSuperClassExpression;
		}

		public ElkSubClassOfAxiom get() throws InterruptedException,
				ExecutionException {
			return ElkSubClassOfAxiom.create(futureSubClassExpression.get(),
					futureSuperClassExpression.get());
		}

	}

	public Future<ElkSubClassOfAxiom> getFutureElkSubClassOfAxiom(
			final Future<? extends ElkClassExpression> futureSubClassExpression,
			final Future<? extends ElkClassExpression> futureSuperClassExpression) {
		return new FutureElkSubClassOfAxiom(futureSubClassExpression,
				futureSuperClassExpression);
	}

}
