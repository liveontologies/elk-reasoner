/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.indexing.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.util.logging.ElkMessage;

/**
 * An object that indexes axioms into a given ontology index. Each instance can
 * either only add or only remove axioms.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkAxiomIndexerVisitor.class);

	/**
	 * The object through which the changes in the index are recorded
	 */
	private final IndexUpdater indexUpdater_;

	/**
	 * The IndexedObjectCache that this indexer writes to.
	 */
	private final IndexedObjectCache ontologyIndex;

	/**
	 * 1 if adding axioms, -1 if removing axioms
	 */
	private final int multiplicity;

	/**
	 * The ElkObjectIndexer used for indexing a neutral, a positive, and a
	 * negative occurrence of an elk object respectively.
	 */
	private final ElkObjectIndexerVisitor neutralIndexer, positiveIndexer,
			negativeIndexer;

	/**
	 * We'll update it's occurrence when indexing disjointness axioms
	 */
	private final IndexedClass owlNothing_;

	/**
	 * @param ontologyIndex
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public ElkAxiomIndexerVisitor(IndexedObjectCache ontologyIndex,
			IndexedClass owlNothing, IndexUpdater updater, boolean insert) {
		this.ontologyIndex = ontologyIndex;
		this.owlNothing_ = owlNothing;
		this.indexUpdater_ = updater;
		this.multiplicity = insert ? 1 : -1;
		this.neutralIndexer = new ElkObjectIndexerVisitor(
				new UpdateCacheFilter(multiplicity, 0, 0));
		this.positiveIndexer = new ElkObjectIndexerVisitor(
				new UpdateCacheFilter(multiplicity, multiplicity, 0));
		this.negativeIndexer = new ElkObjectIndexerVisitor(
				new UpdateCacheFilter(multiplicity, 0, multiplicity));
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {

		IndexedClassExpression subIndexedClass = subElkClass
				.accept(negativeIndexer);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(positiveIndexer);

		(new IndexedSubClassOfAxiom(subIndexedClass, superIndexedClass))
				.updateOccurrenceNumbers(indexUpdater_, multiplicity);
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type) {

		IndexedClassExpression indexedIndividual = individual
				.accept(negativeIndexer);

		IndexedClassExpression indexedType = type.accept(positiveIndexer);

		(new IndexedSubClassOfAxiom(indexedIndividual, indexedType))
				.updateOccurrenceNumbers(indexUpdater_, multiplicity);
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {

		IndexedPropertyChain subIndexedProperty = subElkProperty
				.accept(negativeIndexer);

		IndexedObjectProperty superIndexedProperty = (IndexedObjectProperty) superElkProperty
				.accept(positiveIndexer);

		if (multiplicity == 1) {
			subIndexedProperty.addToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty.addToldSubObjectProperty(subIndexedProperty);
		} else {
			subIndexedProperty
					.removeToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty
					.removeToldSubObjectProperty(subIndexedProperty);
		}
	}

	@Override
	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> disjointClasses) {

		// treat this as a positive occurrence of owl:Nothing
		if (owlNothing_ == null)
			throw new RuntimeException("owlNothing_");

		if (indexUpdater_ == null)
			throw new RuntimeException("updater");

		owlNothing_.updateOccurrenceNumbers(indexUpdater_, multiplicity,
				multiplicity, 0);

		List<IndexedClassExpression> indexed = new ArrayList<IndexedClassExpression>(
				disjointClasses.size());
		for (ElkClassExpression c : disjointClasses) {
			indexed.add(c.accept(negativeIndexer));
		}
		(new IndexedDisjointnessAxiom(indexed)).updateOccurrenceNumbers(
				indexUpdater_, multiplicity);
	}

	@Override
	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty) {

		IndexedObjectProperty indexedReflexiveProperty = (IndexedObjectProperty) reflexiveProperty
				.accept(positiveIndexer);

		indexedReflexiveProperty.reflexiveAxiomOccurrenceNo += multiplicity;
	}

	@Override
	public IndexedClass indexClassDeclaration(ElkClass ec) {
		return (IndexedClass) ec.accept(neutralIndexer);
	}

	@Override
	public IndexedObjectProperty indexObjectPropertyDeclaration(
			ElkObjectProperty ep) {
		return (IndexedObjectProperty) ep.accept(neutralIndexer);
	}

	@Override
	public IndexedIndividual indexNamedIndividualDeclaration(
			ElkNamedIndividual eni) {
		return eni.accept(neutralIndexer);
	}

	@Override
	public void visit(ElkAxiom elkAxiom) {
		try {
			elkAxiom.accept(this);
			if (LOGGER_.isTraceEnabled())
				LOGGER_.trace("indexing "
						+ OwlFunctionalStylePrinter.toString(elkAxiom)
						+ " with multiplicity = " + multiplicity);
		} catch (ElkIndexingException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn(new ElkMessage(e.getMessage()
						+ " Axiom ignored:\n"
						+ OwlFunctionalStylePrinter.toString(elkAxiom),
						"reasoner.indexing.axiomIgnored"));
		}
	}

	/**
	 * A filter that is applied after the given indexed object has been
	 * retrieved from the cache. It is used to update the occurrence counts of
	 * the indexed object, add it to the cache in case of first occurrence, and
	 * remove it from the cache in case of last occurrence.
	 * 
	 * 
	 * @author Frantisek Simancik
	 * 
	 */
	private class UpdateCacheFilter implements IndexedObjectFilter {

		protected final int increment, positiveIncrement, negativeIncrement;

		UpdateCacheFilter(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment = increment;
			this.positiveIncrement = positiveIncrement;
			this.negativeIncrement = negativeIncrement;
		}

		@Override
		public IndexedClassExpression filter(IndexedClassExpression ice) {
			IndexedClassExpression result = ontologyIndex.filter(ice);

			if (!result.occurs() && increment > 0)
				ontologyIndex.add(result);

			result.updateOccurrenceNumbers(indexUpdater_, increment,
					positiveIncrement, negativeIncrement);

			if (!result.occurs() && increment < 0)
				ontologyIndex.remove(result);

			return result;
		}

		@Override
		public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
			IndexedPropertyChain result = ontologyIndex.filter(ipc);

			if (!result.occurs() && increment > 0)
				ontologyIndex.add(result);

			result.updateOccurrenceNumber(increment);

			if (!result.occurs() && increment < 0)
				ontologyIndex.remove(result);

			return result;
		}
	}

}
