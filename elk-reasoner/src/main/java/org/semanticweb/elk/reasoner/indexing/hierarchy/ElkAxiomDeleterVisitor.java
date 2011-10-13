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

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkAnnotationProperty;
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
class ElkAxiomDeleterVisitor extends ElkAxiomIndexerVisitor {

	// logger for events
	private static final Logger LOGGER_ = Logger
			.getLogger(ElkAxiomDeleterVisitor.class);

	private final IndexedObjectCanonizer canonizer;
	private final ElkObjectIndexerVisitor elkObjectIndexer;

	public ElkAxiomDeleterVisitor(IndexedObjectCanonizer canonizer) {
		this.canonizer = canonizer;
		this.elkObjectIndexer = new ElkObjectIndexerVisitor(
				new FailingIndexedObjectFilter(canonizer));
	}

	@Override
	protected void indexSubClassOfAxiom(ElkClassExpression subElkClass,
			ElkClassExpression superElkClass) {
		IndexedClassExpression subIndexedClass = subElkClass
				.accept(elkObjectIndexer);
		IndexedClassExpression superIndexedClass = superElkClass
				.accept(elkObjectIndexer);

		if (subIndexedClass != null && superIndexedClass != null) {
			subIndexedClass.updateOccurrenceNumbers(-1, 0, -1, canonizer);
			superIndexedClass.updateOccurrenceNumbers(-1, -1, 0, canonizer);
			subIndexedClass.removeToldSuperClassExpression(superIndexedClass);
		}
	}

	@Override
	protected void indexSubObjectPropertyOfAxiom(
			ElkSubObjectPropertyExpression subElkProperty,
			ElkObjectPropertyExpression superElkProperty) {
		IndexedPropertyChain subIndexedProperty = subElkProperty
				.accept(elkObjectIndexer);
		IndexedObjectProperty superIndexedProperty = (IndexedObjectProperty) superElkProperty
				.accept(elkObjectIndexer);

		subIndexedProperty.updateOccurrenceNumber(-1, canonizer);
		superIndexedProperty.updateOccurrenceNumber(-1, canonizer);
		subIndexedProperty.removeToldSuperObjectProperty(superIndexedProperty);
		superIndexedProperty.removeToldSubObjectProperty(subIndexedProperty);
	}

	@Override
	protected void indexDeclarationAxiom(ElkEntity entity) {
		entity.accept(entityDeleter);
	}

	private final ElkEntityVisitor<Void> entityDeleter = new ElkEntityVisitor<Void>() {

		public Void visit(ElkClass elkClass) {
			IndexedClassExpression ice = elkClass.accept(elkObjectIndexer);
			if (ice != null)
				ice.updateOccurrenceNumbers(-1, 0, 0, canonizer);
			return null;
		}

		public Void visit(ElkDatatype elkDatatype) {
			LOGGER_.warn(ElkDatatype.class.getSimpleName()
					+ " is supported only partially.");
			return null;
		}

		public Void visit(ElkObjectProperty elkObjectProperty) {
			IndexedPropertyChain ipc = elkObjectProperty
					.accept(elkObjectIndexer);
			if (ipc != null)
				ipc.updateOccurrenceNumber(-1, canonizer);
			return null;
		}

		public Void visit(ElkDataProperty elkDataProperty) {
			LOGGER_.warn(ElkDataProperty.class.getSimpleName()
					+ " is supported only partially.");
			return null;
		}

		public Void visit(ElkNamedIndividual elkNamedIndividual) {
			throw new IndexingException(
					ElkNamedIndividual.class.getSimpleName() + " not supported");
		}

		public Void visit(ElkAnnotationProperty elkAnnotationProperty) {
			// annotations are ignored during indexing
			return null;
		}
	};

}