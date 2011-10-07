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
import org.semanticweb.elk.owl.interfaces.ElkDataProperty;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObjectProperty;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.owl.visitors.ElkEntityVisitor;

/**
 * @author Frantisek Simancik
 * 
 */
class ElkAxiomInserterVisitor extends ElkAxiomIndexerVisitor {

	private final IndexedObjectCanonizer canonizer;
	private final ElkObjectIndexerVisitor elkObjectIndexer;

	public ElkAxiomInserterVisitor(IndexedObjectCanonizer indexedObjectCanonizer) {
		this.canonizer = indexedObjectCanonizer;
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(
				new ConstructingIndexedObjectFilter(indexedObjectCanonizer));
	}

	@Override
	protected void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {
		IndexedClassExpression subIndexedClass = subElkClass
				.accept(elkObjectIndexer);
		IndexedClassExpression superIndexedClass = superElkClass
				.accept(elkObjectIndexer);

		subIndexedClass.updateOccurrenceNumbers(1, 0, 1, canonizer);
		superIndexedClass.updateOccurrenceNumbers(1, 1, 0, canonizer);
		subIndexedClass.addToldSuperClassExpression(superIndexedClass);
	}

	@Override
	protected void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {
		IndexedPropertyChain subIndexedProperty = subElkProperty
				.accept(elkObjectIndexer);
		IndexedObjectProperty superIndexedProperty = (IndexedObjectProperty) superElkProperty
				.accept(elkObjectIndexer);

		subIndexedProperty.updateOccurrenceNumber(1, canonizer);
		superIndexedProperty.updateOccurrenceNumber(1, canonizer);
		subIndexedProperty.addToldSuperObjectProperty(superIndexedProperty);
		superIndexedProperty.addToldSubObjectProperty(subIndexedProperty);
	}

	@Override
	protected void indexDeclarationAxiom(ElkEntity entity) {
		entity.accept(entityAdder);
	};

	private final ElkEntityVisitor<Void> entityAdder = new ElkEntityVisitor<Void>() {

		public Void visit(ElkClass elkClass) {
			IndexedClassExpression ice = elkClass.accept(elkObjectIndexer);
			ice.updateOccurrenceNumbers(1, 0, 0, canonizer);
			return null;
		}

		public Void visit(ElkDatatype elkDatatype) {
			// TODO Auto-generated method stub
			return null;
		}

		public Void visit(ElkObjectProperty elkObjectProperty) {
			IndexedPropertyChain ipc = elkObjectProperty
					.accept(elkObjectIndexer);
			ipc.updateOccurrenceNumber(1, canonizer);
			return null;
		}

		public Void visit(ElkDataProperty elkDataProperty) {
			// TODO Auto-generated method stub
			return null;
		}

		public Void visit(ElkNamedIndividual elkNamedIndividual) {
			// TODO Auto-generated method stub
			return null;
		}
	};

}
