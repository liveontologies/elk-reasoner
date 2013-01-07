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
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainFilter;
import org.semanticweb.elk.util.logging.ElkMessage;

/**
 * An object that indexes axioms into a given ontology index. Each instance can
 * either only add or only remove axioms.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
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
	private final IndexedObjectCache objectCache;

	/**
	 * 1 if adding axioms, -1 if removing axioms
	 */
	private final int multiplicity;

	/**
	 * {@link IndexObjectConverter}s used for indexing a neutral, a positive,
	 * and a negative occurrence of {@link IndexedClassExpression}s.
	 */
	private final IndexObjectConverter neutralIndexer, positiveIndexer,
			negativeIndexer;

	/**
	 * An {@link IndexedAxiomFilter} used to update occurrences of
	 * {@link IndexedAxiom}s
	 */
	private final IndexedAxiomFilter axiomUpdateFilter;

	/**
	 * A reference to the indexed {@code owl:Nothing}, which occurrence counters
	 * is updated when creating {@link IndexedDisjointnessAxiom}s, which
	 * implicitly assumed to contain {@code owl:Nothing} positively. This can be
	 * used to detect if axioms can cause inconsistency: if {@code owl:Nothing}
	 * never occurs positively in this way, then no inconsistency can be caused.
	 */
	private final IndexedClass owlNothing_;

	/**
	 * @param objectCache
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public ElkAxiomIndexerVisitor(IndexedObjectCache objectCache,
			IndexedClass owlNothing, IndexUpdater updater, boolean insert) {
		this.objectCache = objectCache;
		this.owlNothing_ = owlNothing;
		this.indexUpdater_ = updater;
		this.multiplicity = insert ? 1 : -1;
		IndexedPropertyChainFilter propertyOccurrenceUpdateFilter = new PropertyOccurrenceUpdateFilter(
				multiplicity);
		this.neutralIndexer = new IndexObjectConverter(
				new ClassOccurrenceUpdateFilter(multiplicity, 0, 0),
				propertyOccurrenceUpdateFilter);
		this.positiveIndexer = new IndexObjectConverter(
				new ClassOccurrenceUpdateFilter(multiplicity, multiplicity, 0),
				propertyOccurrenceUpdateFilter);
		this.negativeIndexer = new IndexObjectConverter(
				new ClassOccurrenceUpdateFilter(multiplicity, 0, multiplicity),
				propertyOccurrenceUpdateFilter);
		this.axiomUpdateFilter = new AxiomOccurrenceUpdateFilter(multiplicity);
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {

		IndexedClassExpression subIndexedClass = subElkClass
				.accept(negativeIndexer);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(positiveIndexer);

		axiomUpdateFilter.visit(new IndexedSubClassOfAxiom(subIndexedClass,
				superIndexedClass));
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type) {

		IndexedClassExpression indexedIndividual = individual
				.accept(negativeIndexer);

		IndexedClassExpression indexedType = type.accept(positiveIndexer);

		axiomUpdateFilter.visit(new IndexedSubClassOfAxiom(indexedIndividual,
				indexedType));
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
			throw new NullPointerException("owlNothing not provided!");

		if (indexUpdater_ == null)
			throw new NullPointerException("indexUpdater not provided!");

		owlNothing_.updateAndCheckOccurrenceNumbers(indexUpdater_,
				multiplicity, multiplicity, 0);

		List<IndexedClassExpression> indexed = new ArrayList<IndexedClassExpression>(
				disjointClasses.size());
		for (ElkClassExpression c : disjointClasses) {
			indexed.add(c.accept(negativeIndexer));
		}

		axiomUpdateFilter.visit(new IndexedDisjointnessAxiom(indexed));
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
		} catch (ElkIndexingUnsupportedException e) {
			if (LOGGER_.isEnabledFor(Level.WARN))
				LOGGER_.warn(new ElkMessage(e.getMessage()
						+ " Axiom ignored:\n"
						+ OwlFunctionalStylePrinter.toString(elkAxiom),
						"reasoner.indexing.axiomIgnored"));
		}
	}

	/**
	 * A {@link ClassOccurrenceUpdateFilter}, which is responsible for updating
	 * the occurrence counters of {@link IndexedClassExpression}s, as well as
	 * for adding such objects to the {@link IndexedObjectCache} when its
	 * occurrences becomes non-zero, and removing from the
	 * {@link IndexedObjectCache}, when its occurrences becomes zero.
	 */
	private class ClassOccurrenceUpdateFilter implements
			IndexedClassExpressionFilter {

		protected final int increment, positiveIncrement, negativeIncrement;

		ClassOccurrenceUpdateFilter(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment = increment;
			this.positiveIncrement = positiveIncrement;
			this.negativeIncrement = negativeIncrement;
		}

		public <T extends IndexedClassExpression> T update(T ice) {
			if (!ice.occurs() && increment > 0)
				indexUpdater_.add(ice);

			ice.updateAndCheckOccurrenceNumbers(indexUpdater_, increment,
					positiveIncrement, negativeIncrement);

			if (!ice.occurs() && increment < 0)
				indexUpdater_.remove(ice);

			return ice;
		}

		@Override
		public IndexedClass visit(IndexedClass element) {
			return update(objectCache.visit(element));
		}

		@Override
		public IndexedIndividual visit(IndexedIndividual element) {
			return update(objectCache.visit(element));
		}

		@Override
		public IndexedObjectIntersectionOf visit(
				IndexedObjectIntersectionOf element) {
			return update(objectCache.visit(element));
		}

		@Override
		public IndexedObjectSomeValuesFrom visit(
				IndexedObjectSomeValuesFrom element) {
			return update(objectCache.visit(element));
		}

		@Override
		public IndexedDataHasValue visit(IndexedDataHasValue element) {
			return update(objectCache.visit(element));
		}

	}

	/**
	 * A {@link PropertyOccurrenceUpdateFilter}, which responsible for updating
	 * the occurrence counter of {@link IndexedPropertyChain}s, as well as for
	 * adding such objects to the index, when its occurrence counter becomes
	 * non-zero, and remove from the index, when its occurrence counter becomes
	 * zero.
	 * 
	 * @author Frantisek Simancik
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class PropertyOccurrenceUpdateFilter implements
			IndexedPropertyChainFilter {
		protected final int increment;

		PropertyOccurrenceUpdateFilter(int increment) {
			this.increment = increment;
		}

		public <T extends IndexedPropertyChain> T update(T ipc) {
			if (!ipc.occurs() && increment > 0)
				indexUpdater_.add(ipc);

			ipc.updateOccurrenceNumber(increment);

			if (!ipc.occurs() && increment < 0)
				indexUpdater_.remove(ipc);

			return ipc;
		}

		@Override
		public IndexedObjectProperty visit(IndexedObjectProperty element) {
			return update(objectCache.visit(element));
		}

		@Override
		public IndexedBinaryPropertyChain visit(
				IndexedBinaryPropertyChain element) {
			return update(objectCache.visit(element));
		}

	}

	/**
	 * A {@link AxiomOccurrenceUpdateFilter}, which responsible for updating the
	 * occurrence counter of {@link IndexedAxiom}s, as well as for adding such
	 * objects to the index, when its occurrence counter becomes non-zero, and
	 * remove from the index, when its occurrence counter becomes zero.
	 * 
	 * @author Frantisek Simancik
	 * @author "Yevgeny Kazakov"
	 * 
	 */
	private class AxiomOccurrenceUpdateFilter implements IndexedAxiomFilter {
		protected final int increment;

		AxiomOccurrenceUpdateFilter(int increment) {
			this.increment = increment;
		}

		public <T extends IndexedAxiom> T update(T axiom) {
			if (!axiom.occurs() && increment > 0)
				indexUpdater_.add(axiom);

			axiom.updateOccurrenceNumbers(indexUpdater_, increment);

			if (!axiom.occurs() && increment < 0)
				indexUpdater_.remove(axiom);

			return axiom;
		}

		@Override
		public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
			return update(objectCache.visit(axiom));
		}

		@Override
		public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
			return update(objectCache.visit(axiom));
		}

	}

}
