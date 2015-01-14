package org.semanticweb.elk.reasoner.indexing.caching;

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
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedIndividual;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;

/**
 * A container for OWL objects occurring in the ontology, such as class
 * expressions, individuals, and property expressions in their "compiled"
 * (indexed) representation.
 * 
 * @author "Yevgeny Kazakov"
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

}
