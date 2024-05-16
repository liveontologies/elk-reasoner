package org.semanticweb.elk.reasoner.indexing.model;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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

import java.util.Collection;

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * A container for OWL objects occurring in the ontology, such as class
 * expressions, individuals, and property expressions in their "compiled"
 * (indexed) representation.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @see IndexedObject
 *
 */
public interface IndexedObjectCache {

	/**
	 * @return the {@link IndexedClass}es corresponding to all {@link ElkClass}
	 *         es occurring in the ontology (including {@code owl:Thing} and
	 *         {@code owl:Nothing})
	 */
	public Collection<? extends IndexedClass> getClasses();

	/**
	 * @return the {@link IndexedIndividual}s corresponding to all
	 *         {@link ElkIndividual}s occurring in the ontology.
	 */
	public Collection<? extends IndexedIndividual> getIndividuals();

	/**
	 * @return the {@link IndexedObjectProperty}s corresponding to all
	 *         {@link ElkObjectProperty}s occurring in the ontology.
	 */
	public Collection<? extends IndexedObjectProperty> getObjectProperties();

	/**
	 * @return the {@link IndexedClassExpression}s corresponding to all
	 *         {@link ElkClassExpression}s occurring in the ontology (including
	 *         {@code owl:Thing} and {@code owl:Nothing})
	 */
	public Collection<? extends IndexedClassExpression> getClassExpressions();

	/**
	 * @return the {@link IndexedPropertyChain}s corresponding to all
	 *         {@link ElkSubObjectPropertyExpression}s occurring in the
	 *         ontology.
	 */
	public Collection<? extends IndexedPropertyChain> getPropertyChains();

	/**
	 * @return the {@link IndexedClass} corresponding to {@code owl:Thing} if it
	 *         occurs in the ontology or {@code null} if {@code owl:Thing} does
	 *         not occur in the ontology.
	 */
	public IndexedClass getOwlThing();

	/**
	 * @return the {@link IndexedClass} corresponding to {@code owl:Nothing} if
	 *         it occurs in the ontology or {@code null} if {@code owl:Nothing}
	 *         does not occur in the ontology.
	 */
	public IndexedClass getOwlNothing();

	/**
	 * @return the {@link IndexedObjectProperty} corresponding to
	 *         {@code owl:topObjectProperty}.
	 */
	public IndexedObjectProperty getOwlTopObjectProperty();

	/**
	 * @return the {@link IndexedObjectProperty} corresponding to
	 *         {@code owl:bottomObjectProperty}.
	 */
	public IndexedObjectProperty getOwlBottomObjectProperty();

	/**
	 * Registers a given {@link ChangeListener} with this
	 * {@link IndexedObjectCache}
	 * 
	 * @param listener
	 *            a {@link ChangeListener} to be added
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         registered
	 */
	public boolean addListener(ChangeListener listener);

	/**
	 * Removes a given {@link ChangeListener} from this {@link IndexedObjectCache}
	 * 
	 * @param listener
	 *            a {@link ChangeListener} to be removed
	 * @return {@code true} if the operation was successful and {@code false}
	 *         otherwise; if {@code false} is return, the listener was not
	 *         removed
	 */
	public boolean removeListener(ChangeListener listener);

	/**
	 * The listener for changes in {@link IndexedObjectCache}
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface ChangeListener {

		void classAddition(IndexedClass cls);

		void classRemoval(IndexedClass cls);

		void individualAddition(IndexedIndividual ind);

		void individualRemoval(IndexedIndividual ind);

		void objectPropertyAddition(IndexedObjectProperty prop);

		void objectPropertyRemoval(IndexedObjectProperty prop);

		void classExpressionAddition(IndexedClassExpression expr);

		void classExpressionRemoval(IndexedClassExpression expr);

		void propertyChainAddition(IndexedPropertyChain chain);

		void propertyChainRemoval(IndexedPropertyChain chain);

	}

}
