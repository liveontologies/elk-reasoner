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
 * @author Frantisek Simancik
 * 
 */
public class ElkAxiomIndexerVisitor extends AbstractElkAxiomIndexerVisitor {

	private final IndexedObjectCache objectCache;
	private final int multiplicity;
	private final ElkObjectIndexerVisitor neutralIndexer,
		positiveIndexer, negativeIndexer;
	

	/**
	 * @param objectCache
	 * @param insert specifies whether this objects inserts or deletes axioms
	 */
	public ElkAxiomIndexerVisitor(IndexedObjectCache objectCache, boolean insert) {
		this.objectCache = objectCache; 
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

		if (multiplicity == 1) {
			subIndexedClass.addToldSuperClassExpression(superIndexedClass);
		}
		else {
			subIndexedClass.removeToldSuperClassExpression(superIndexedClass);
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
			subIndexedProperty.addToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty.addToldSubObjectProperty(subIndexedProperty);
		}
		else {
			subIndexedProperty.removeToldSuperObjectProperty(superIndexedProperty);
			superIndexedProperty.removeToldSubObjectProperty(subIndexedProperty);
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


	
	
	
	private class UpdateCacheFilter implements IndexedObjectFilter {

		protected final int increment, positiveIncrement, negativeIncrement;

		UpdateCacheFilter(int increment, int positiveIncrement,
				int negativeIncrement) {
			this.increment = increment;
			this.positiveIncrement = positiveIncrement;
			this.negativeIncrement = negativeIncrement;
		}

		public IndexedClassExpression filter(IndexedClassExpression ice) {
			IndexedClassExpression result = objectCache.filter(ice);

			if (!result.occurs() && increment > 0)
				objectCache.add(result);

			result.updateOccurrenceNumbers(increment,
					positiveIncrement, negativeIncrement);

			if (!result.occurs() && increment < 0)
				objectCache.remove(result);

			return result;
		}

		public IndexedPropertyChain filter(IndexedPropertyChain ipc) {
			IndexedPropertyChain result = objectCache.filter(ipc);

			if (!result.occurs() && increment > 0)
				objectCache.add(result);

			result.updateOccurrenceNumber(increment);

			if (!result.occurs() && increment < 0)
				objectCache.remove(result);

			return result;
		}
	}

}
