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

import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;

/**
 * An object that indexes axioms into a given IndexedObjectCache. Each instance
 * can either only add or only remove axioms.
 * 
 * @author Frantisek Simancik
 * 
 */
public class ElkAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor {

	/**
	 * The IndexedObjectCache that this indexer writes to.
	 */
	private final IndexedObjectLookup objectLookup;

	/**
	 * The object through which the changes in the index are recordered
	 */
	private final IndexUpdater indexUpdater;

	/**
	 * 1 if adding axioms, -1 if removing axioms
	 */
	private final int multiplicity;

	/**
	 * The ElkObjectIndexer used for indexing a netural, a positive, and a
	 * negative occurrence of an elk object respectively.
	 */
	private final ElkObjectIndexerVisitor neutralIndexer, positiveIndexer,
			negativeIndexer;

	/**
	 * @param objectLookup
	 * @param insert
	 *            specifies whether this objects inserts or deletes axioms
	 */
	public ElkAxiomIndexerVisitor(IndexedObjectLookup objectLookup,
			IndexUpdater indexUpdater, boolean insert) {
		this.objectLookup = objectLookup;
		this.indexUpdater = indexUpdater;
		this.multiplicity = insert ? 1 : -1;
		this.neutralIndexer = new ElkObjectIndexerVisitor(
				new ObjectLookupUpdater(multiplicity, 0, 0));
		this.positiveIndexer = new ElkObjectIndexerVisitor(
				new ObjectLookupUpdater(multiplicity, multiplicity, 0));
		this.negativeIndexer = new ElkObjectIndexerVisitor(
				new ObjectLookupUpdater(multiplicity, 0, multiplicity));
	}

	/**
	 * Create the axiom indexer which applies the index changes immediately to
	 * the indexed objects.
	 * 
	 * @param objectLookup
	 *            lookup for indexed objects
	 * @param insert
	 * @return
	 */
	public static ElkAxiomIndexerVisitor getDirectAxiomIndexer(
			IndexedObjectLookup objectLookup, boolean insert) {
		return new ElkAxiomIndexerVisitor(objectLookup,
				new DirectIndexUpdater(), insert);
	}

	public static ElkAxiomIndexerVisitor getIncrementalAxiomIndexer(
			IndexedObjectLookup objectLookup, IndexChange indexChange,
			boolean insert) {
		return new ElkAxiomIndexerVisitor(objectLookup,
				new IncrementalIndexUpdater(indexChange), insert);
	}

	@Override
	public void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {

		IndexedClassExpression subIndexedClass = subElkClass
				.accept(negativeIndexer);

		IndexedClassExpression superIndexedClass = superElkClass
				.accept(positiveIndexer);

		if (multiplicity == 1) {
			indexUpdater.addToldSuperClassExpression(subIndexedClass,
					superIndexedClass);
		} else {
			indexUpdater.removeToldSuperClassExpression(subIndexedClass,
					superIndexedClass);
		}
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
			indexUpdater.addToldSuperObjectProperty(subIndexedProperty,
					superIndexedProperty);
			indexUpdater.addToldSubObjectProperty(superIndexedProperty,
					subIndexedProperty);
		} else {
			indexUpdater.removeToldSuperObjectProperty(subIndexedProperty,
					superIndexedProperty);
			indexUpdater.removeToldSubObjectProperty(superIndexedProperty,
					subIndexedProperty);
		}
	}

	@Override
	public void indexClassDeclaration(ElkClass ec) {
		ec.accept(neutralIndexer);
	}

	@Override
	public void indexObjectPropertyDeclaration(ElkObjectProperty ep) {
		ep.accept(neutralIndexer);
	}

	@Override
	public void indexNamedIndividualDeclaration(ElkNamedIndividual eni) {
		eni.accept(neutralIndexer);
	}

	/**
	 * A filter that is applied after the given indexed object has been
	 * retrieved from the cache. It is used to update the occurrence counts of
	 * the indexed object, add it to the cache in case of first occurrence, and
	 * remove it from the cache in case of last occurrence no more.
	 * 
	 * 
	 * @author Frantisek Simancik
	 * 
	 */
	private class ObjectLookupUpdater implements IndexedObjectFilter {

		protected final int increment, positiveIncrement, negativeIncrement;

		ObjectLookupUpdater(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment = increment;
			this.positiveIncrement = positiveIncrement;
			this.negativeIncrement = negativeIncrement;
		}

		public IndexedClassExpression filter(IndexedClassExpression ice) {
			IndexedClassExpression result = objectLookup.filter(ice);

			if (!result.occurs() && increment > 0)
				objectLookup.add(result);

			result.updateOccurrenceNumbers(indexUpdater, increment,
					positiveIncrement, negativeIncrement);

			if (!result.occurs() && increment < 0)
				objectLookup.remove(result);

			return result;
		}

		public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
			IndexedPropertyChain result = objectLookup.filter(ipc);

			if (!result.occurs() && increment > 0)
				objectLookup.add(result);

			result.updateOccurrenceNumber(increment);

			if (!result.occurs() && increment < 0)
				objectLookup.remove(result);

			return result;
		}
	}

}
