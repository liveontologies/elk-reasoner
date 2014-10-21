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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedAxiomFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedClassExpressionFilter;
import org.semanticweb.elk.reasoner.indexing.visitors.IndexedPropertyChainFilter;

/**
 * An object that indexes axioms into a given ontology index. Each instance can
 * either only add or only remove axioms.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 */
public class MainAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor
		implements ElkAxiomIndexingVisitor {

	/**
	 * The index in which the changes are recorded
	 */
	private final ModifiableOntologyIndex index_;

	/**
	 * The IndexedObjectCache that this indexer writes to.
	 */
	private final IndexedObjectCache objectCache_;

	/**
	 * 1 if adding axioms, -1 if removing axioms
	 */
	private final int multiplicity_;

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
	 * @param index
	 *            the {@link ModifiableOntologyIndex} used for indexing axioms
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public MainAxiomIndexerVisitor(ModifiableOntologyIndex index, boolean insert) {
		this.index_ = index;
		this.objectCache_ = index.getIndexedObjectCache();
		this.owlNothing_ = index.getIndexedOwlNothing();
		this.multiplicity_ = insert ? 1 : -1;
		final IndexedPropertyChainFilter propertyOccurrenceUpdateFilter = new PropertyOccurrenceUpdateFilter(
				multiplicity_);
		this.neutralIndexer = new IndexObjectConverter(
				new ClassOccurrenceUpdateFilter(multiplicity_, 0, 0),
				propertyOccurrenceUpdateFilter);
		IndexObjectConverterFactory negativeIndexerFactory = new IndexObjectConverterFactory() {
			@Override
			public IndexObjectConverter create(
					IndexObjectConverter complementary) {
				return new IndexObjectConverter(
						new ClassOccurrenceUpdateFilter(multiplicity_, 0,
								multiplicity_), propertyOccurrenceUpdateFilter,
						complementary);
			}
		};
		this.positiveIndexer = new IndexObjectConverter(
				new ClassOccurrenceUpdateFilter(multiplicity_, multiplicity_, 0),
				propertyOccurrenceUpdateFilter, negativeIndexerFactory);
		this.negativeIndexer = positiveIndexer.getComplementaryConverter();
		this.axiomUpdateFilter = new AxiomOccurrenceUpdateFilter(multiplicity_);
	}

	@Override
	public int getMultiplicity() {
		return multiplicity_;
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {
		axiomUpdateFilter
				.visit(new IndexedSubClassOfAxiom(subElkClass
						.accept(negativeIndexer), superElkClass
						.accept(positiveIndexer)));
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type) {

		axiomUpdateFilter.visit(new IndexedSubClassOfAxiom(individual
				.accept(negativeIndexer), type.accept(positiveIndexer)));
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {

		axiomUpdateFilter.visit(new IndexedSubObjectPropertyOfAxiom(
				subElkProperty.accept(negativeIndexer),
				(IndexedObjectProperty) superElkProperty
						.accept(positiveIndexer)));
	}

	@Override
	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> disjointClasses) {

		// treat this as a positive occurrence of owl:Nothing
		if (owlNothing_ == null)
			throw new NullPointerException("owlNothing not provided!");

		if (index_ == null)
			throw new NullPointerException("indexUpdater not provided!");

		owlNothing_.updateAndCheckOccurrenceNumbers(index_, multiplicity_,
				multiplicity_, 0);

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

		axiomUpdateFilter.visit(new IndexedReflexiveObjectPropertyAxiom(
				(IndexedObjectProperty) reflexiveProperty
						.accept(positiveIndexer)));

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

	/**
	 * A {@link ClassOccurrenceUpdateFilter}, which is responsible for updating
	 * the occurrence counters of {@link IndexedClassExpression}s, as well as
	 * for adding such objects to the {@link IndexedObjectCache} when its
	 * occurrences becomes non-zero, and removing from the
	 * {@link IndexedObjectCache}, when its occurrences becomes zero.
	 */
	private class ClassOccurrenceUpdateFilter implements
			IndexedClassExpressionFilter {

		private final int increment_, positiveIncrement_, negativeIncrement_;

		ClassOccurrenceUpdateFilter(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment_ = increment;
			this.positiveIncrement_ = positiveIncrement;
			this.negativeIncrement_ = negativeIncrement;
		}

		public <T extends IndexedClassExpression> T update(T ice) {
			if (!ice.occurs() && increment_ > 0)
				index_.add(ice);

			if (!ice.updateAndCheckOccurrenceNumbers(index_, increment_,
					positiveIncrement_, negativeIncrement_)) {
				// revert the change
				if (!ice.occurs() && increment_ > 0)
					index_.remove(ice);
				throw new ElkUnexpectedIndexingException(ice
						+ ": cannot index class expression! " + "[inc="
						+ increment_ + "; posIncr=" + positiveIncrement_
						+ "; negIncr=" + negativeIncrement_ + "]");
			}

			if (!ice.occurs() && increment_ < 0) {
				index_.remove(ice);
			}

			return ice;
		}

		@Override
		public IndexedClass visit(IndexedClass element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedIndividual visit(IndexedIndividual element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedObjectComplementOf visit(IndexedObjectComplementOf element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedObjectIntersectionOf visit(
				IndexedObjectIntersectionOf element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedObjectSomeValuesFrom visit(
				IndexedObjectSomeValuesFrom element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedObjectUnionOf visit(IndexedObjectUnionOf element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedDataHasValue visit(IndexedDataHasValue element) {
			return update(objectCache_.visit(element));
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

		private final int increment_;

		PropertyOccurrenceUpdateFilter(int increment) {
			this.increment_ = increment;
		}

		public <T extends IndexedPropertyChain> T update(T ipc) {
			if (!ipc.occurs() && increment_ > 0)
				index_.add(ipc);

			if (!ipc.updateAndCheckOccurrenceNumbers(increment_)) {
				// revert the change
				if (!ipc.occurs() && increment_ > 0)
					index_.remove(ipc);
				throw new ElkUnexpectedIndexingException(ipc
						+ ": cannot index property!" + "[inc=" + increment_
						+ "]");
			}

			if (!ipc.occurs() && increment_ < 0)
				index_.remove(ipc);

			return ipc;
		}

		@Override
		public IndexedObjectProperty visit(IndexedObjectProperty element) {
			return update(objectCache_.visit(element));
		}

		@Override
		public IndexedBinaryPropertyChain visit(
				IndexedBinaryPropertyChain element) {
			return update(objectCache_.visit(element));
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

		private final int increment_;

		AxiomOccurrenceUpdateFilter(int increment) {
			this.increment_ = increment;
		}

		public <T extends IndexedAxiom> T update(T axiom) {
			if (!axiom.occurs() && increment_ > 0) {
				index_.add(axiom);
			}

			if (!axiom.updateOccurrenceNumbers(index_, increment_)) {
				// revert the change
				if (!axiom.occurs() && increment_ > 0) {
					index_.remove(axiom);
				}
				throw new ElkUnexpectedIndexingException(axiom
						+ ": cannot index axiom!" + "[inc=" + increment_ + "]");
			}

			if (!axiom.occurs() && increment_ < 0)
				index_.remove(axiom);

			return axiom;
		}

		@Override
		public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

		@Override
		public IndexedSubObjectPropertyOfAxiom visit(
				IndexedSubObjectPropertyOfAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

		@Override
		public IndexedReflexiveObjectPropertyAxiom visit(
				IndexedReflexiveObjectPropertyAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

		@Override
		public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

	}

}
