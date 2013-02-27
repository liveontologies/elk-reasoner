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
 * @author Yevgeny Kazakov, May 13, 2011
 */
package org.semanticweb.elk.reasoner.indexing;

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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.util.collections.chains.Chain;

/**
 * Interface for public methods of the index of the ontology.
 * 
 * @author Yevgeny Kazakov
 * @author Frantisek Simancik
 * 
 */
public interface OntologyIndex {

	/**
	 * Return the indexed representation of the given {@link ElkClassExpression}
	 * 
	 * @param elkClassExpression
	 *            an input {@link ElkClassExpression}
	 * @return the {@link IndexedClassExpression} corresponding to the input if
	 *         it occurs in the indexed ontology, or {@code null} if the input
	 *         is not contained in the indexed ontology
	 */
	IndexedClassExpression getIndexed(ElkClassExpression elkClassExpression);

	/**
	 * Return the indexed representation of the given
	 * {@link ElkSubObjectPropertyExpression}
	 * 
	 * @param elkSubObjectPropertyExpression
	 *            an input {@link ElkSubObjectPropertyExpression}
	 * @return the {@link IndexedPropertyChain} corresponding to the input if it
	 *         occurs in the indexed ontology, or {@code null} if the input is
	 *         not contained in the indexed ontology
	 */
	IndexedPropertyChain getIndexed(
			ElkSubObjectPropertyExpression elkSubObjectPropertyExpression);

	/**
	 * Return the indexed representation of {@code owl:Thing}
	 * 
	 * @return the {@link IndexedClass} corresponding to {@code owl:Thing}. It
	 *         is assumed that {@code owl:Thing} occurs (i.e., is declared) in
	 *         every ontology.
	 */
	IndexedClass getIndexedOwlThing();

	/**
	 * Return the indexed representation of {@code owl:Nothing}
	 * 
	 * @return the {@link IndexedClass} corresponding to {@code owl:Nothing}. It
	 *         is assumed that {@code owl:Nothing} contains (i.e., is declared)
	 *         in every ontology.
	 */
	IndexedClass getIndexedOwlNothing();

	/**
	 * @return the {@link IndexedClassExpression}s for all
	 *         {@link ElkClassExpression}s occurring in the ontology (including
	 *         {@code owl:Thing} and {@code owl:Nothing}) or added/removed from
	 *         the ontology since the last commit of the differential index
	 */
	Collection<IndexedClassExpression> getIndexedClassExpressions();

	/**
	 * @return the {@link IndexedClass}es for all {@link ElkClass}es occurring
	 *         in the ontology (including {@code owl:Thing} and
	 *         {@code owl:Nothing} )
	 */
	Collection<IndexedClass> getIndexedClasses();

	/**
	 * @return the {@link IndexedIndividual}s for all {@link ElkIndividual}s
	 *         occurring in the ontology.
	 */
	Collection<IndexedIndividual> getIndexedIndividuals();

	/**
	 * @return the {@link IndexedPropertyChain}s for all
	 *         {@link ElkSubObjectPropertyExpression}s occurring in the
	 *         ontology.
	 */
	Collection<IndexedPropertyChain> getIndexedPropertyChains();

	/**
	 * @return the {@link IndexedObjectProperty}s for all
	 *         {@link ElkObjectProperty}s occurring in the ontology.
	 */
	Collection<IndexedObjectProperty> getIndexedObjectProperties();

	/**
	 * @return the {@link IndexedObjectProperty}s for all
	 *         {@link ElkObjectProperty}s occurring in the reflexivity axioms of
	 *         the ontology.
	 */
	Collection<IndexedObjectProperty> getReflexiveObjectProperties();

	/**
	 * @return the first context initialization rule assigned to this
	 *         {@link OntologyIndex}, or {@code null} if there no such rules;
	 *         all other rules can be obtained by traversing over
	 *         {@link LinkRule#next()}
	 */
	LinkRule<Context> getContextInitRuleHead();

	/**
	 * @return a {@link Chain} view of context initialization rules assigned to
	 *         this {@link OntologyIndex}; it can be used for inserting new
	 *         rules or deleting existing ones
	 */
	Chain<ChainableRule<Context>> getContextInitRuleChain();

	/**
	 * @return the {@link IndexedObjectCache} associated with this index
	 */
//	IndexedObjectCache getIndexedObjectCache();

//	Collection<IndexedAxiom> getIndexedAxioms();

}