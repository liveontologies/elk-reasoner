package org.semanticweb.elk.owlapi;

/*-
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyBuilder;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NoOpReadWriteLock;

/**
 * Optimized implementation of some common methods from {@link OWLManager} that
 * cache values for frequent use in tests.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class TestOWLManager {

	private static final OWLOntologyManager MASTER_MANAGER_ = OWLManager
			.createOWLOntologyManager();
	protected static final OWLDataFactory df = MASTER_MANAGER_
			.getOWLDataFactory();

	public static OWLOntologyManager createOWLOntologyManager() {
		OWLOntologyManager man = new OWLOntologyManagerImpl(df,
				new NoOpReadWriteLock());
		man.getOntologyFactories()
				.set(new OWLOntologyFactoryImpl(new OWLOntologyBuilder() {
					private static final long serialVersionUID = -7962454739789851685L;

					@Override
					public OWLOntology createOWLOntology(OWLOntologyManager om,
							OWLOntologyID id) {
						return new OWLOntologyImpl(om, id);
					}
				}));
		man.getOntologyParsers().set(MASTER_MANAGER_.getOntologyParsers());
		man.getOntologyStorers().set(MASTER_MANAGER_.getOntologyStorers());
		man.getIRIMappers().set(MASTER_MANAGER_.getIRIMappers());
		return man;
	}

	public static OWLDataFactory getOWLDataFactory() {
		return df;
	}

}
