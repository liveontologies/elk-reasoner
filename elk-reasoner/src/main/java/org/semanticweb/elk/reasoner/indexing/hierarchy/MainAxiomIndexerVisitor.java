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

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
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
	 *  
	 */
	private final IndexedAxiomFactory indexedAxiomFactory_;

	/**
	 * @param index
	 *            the {@link ModifiableOntologyIndex} used for indexing axioms
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public MainAxiomIndexerVisitor(ModifiableOntologyIndex index, boolean insert) {
		this(index, new PlainIndexedAxiomFactory(), insert);
	}
	
	public MainAxiomIndexerVisitor(ModifiableOntologyIndex index, IndexedAxiomFactory indexedAxiomFactory, boolean insert) {
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
		this.indexedAxiomFactory_ = indexedAxiomFactory;
	}	

	@Override
	public int getMultiplicity() {
		return multiplicity_;
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass, ElkAxiom assertedAxiom) {
		// If this is uncommented, deleting a subsumption C => D would
		// effectively replace it by C => T. This means there will be fewer
		// rule deletions during the deletion stage.
		/*
		 * IndexedClassExpression subIndexedClass = multiplicity_ > 0 ?
		 * subElkClass .accept(negativeIndexer) :
		 * subElkClass.accept(noOpConverter);
		 */

		IndexedClassExpression subIndexedClass = subElkClass
				.accept(negativeIndexer);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(positiveIndexer);

		//axiomUpdateFilter.visit(new IndexedSubClassOfAxiom(subIndexedClass,	superIndexedClass, assertedAxiom));
		axiomUpdateFilter.visit(indexedAxiomFactory_.createSubClassOfAxiom(subIndexedClass, superIndexedClass, assertedAxiom));
	}

	@Override
	public void indexClassAssertion(ElkIndividual individual,
			ElkClassExpression type, ElkAxiom assertedAxiom) {

		IndexedClassExpression indexedIndividual = individual
				.accept(negativeIndexer);

		IndexedClassExpression indexedType = type.accept(positiveIndexer);

		//axiomUpdateFilter.visit(new IndexedSubClassOfAxiom(indexedIndividual, indexedType, assertedAxiom));
		axiomUpdateFilter.visit(indexedAxiomFactory_.createSubClassOfAxiom(indexedIndividual, indexedType, assertedAxiom));
	}

	@Override
	public void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {

		IndexedPropertyChain subIndexedProperty = subElkProperty
				.accept(negativeIndexer);

		IndexedObjectProperty superIndexedProperty = (IndexedObjectProperty) superElkProperty
				.accept(positiveIndexer);

		if (multiplicity_ == 1) {
			subIndexedProperty.addToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty.addToldSubPropertyChain(subIndexedProperty);
		} else {
			subIndexedProperty
					.removeToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty
					.removeToldSubObjectProperty(subIndexedProperty);
		}
	}

	@Override
	public void indexDisjointClassExpressions(
			List<? extends ElkClassExpression> disjointClasses, ElkAxiom axiom) {

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

		//axiomUpdateFilter.visit(new IndexedDisjointnessAxiom(indexed));
		axiomUpdateFilter.visit(indexedAxiomFactory_.createDisjointnessAxiom(indexed, axiom));
	}

	@Override
	public void indexReflexiveObjectProperty(
			ElkObjectPropertyExpression reflexiveProperty) {

		IndexedObjectProperty indexedReflexiveProperty = (IndexedObjectProperty) reflexiveProperty
				.accept(positiveIndexer);

		if (indexedReflexiveProperty.reflexiveAxiomOccurrenceNo == 0
				&& multiplicity_ > 0)
			// first occurrence of reflexivity axiom
			index_.addReflexiveProperty(indexedReflexiveProperty);

		indexedReflexiveProperty.reflexiveAxiomOccurrenceNo += multiplicity_;

		if (indexedReflexiveProperty.reflexiveAxiomOccurrenceNo == 0
				&& multiplicity_ < 0)
			// no occurrence of reflexivity axiom
			index_.removeReflexiveProperty(indexedReflexiveProperty);
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

		protected final int increment, positiveIncrement, negativeIncrement;

		ClassOccurrenceUpdateFilter(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment = increment;
			this.positiveIncrement = positiveIncrement;
			this.negativeIncrement = negativeIncrement;
		}

		public <T extends IndexedClassExpression> T update(T ice) {
			if (!ice.occurs() && increment > 0)
				index_.add(ice);

			ice.updateAndCheckOccurrenceNumbers(index_, increment,
					positiveIncrement, negativeIncrement);

			if (!ice.occurs() && increment < 0) {
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
		protected final int increment;

		PropertyOccurrenceUpdateFilter(int increment) {
			this.increment = increment;
		}

		public <T extends IndexedPropertyChain> T update(T ipc) {
			if (!ipc.occurs() && increment > 0)
				index_.add(ipc);

			ipc.updateAndCheckOccurrenceNumbers(increment);

			if (!ipc.occurs() && increment < 0)
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
		protected final int increment;

		AxiomOccurrenceUpdateFilter(int increment) {
			this.increment = increment;
		}

		public <T extends IndexedAxiom> T update(T axiom) {
			if (!axiom.occurs() && increment > 0)
				index_.add(axiom);

			axiom.updateOccurrenceNumbers(index_, increment);

			if (!axiom.occurs() && increment < 0)
				index_.remove(axiom);

			return axiom;
		}

		@Override
		public IndexedSubClassOfAxiom visit(IndexedSubClassOfAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

		@Override
		public IndexedDisjointnessAxiom visit(IndexedDisjointnessAxiom axiom) {
			return update(objectCache_.visit(axiom));
		}

	}

}
