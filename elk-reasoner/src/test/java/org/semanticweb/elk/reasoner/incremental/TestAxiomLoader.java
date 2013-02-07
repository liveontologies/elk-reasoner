package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.util.Iterator;

import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.BaseIncrementalReasoningCorrectnessTest.CHANGE;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TestAxiomLoader implements OntologyLoader, ChangesLoader {

	private final Iterable<ElkAxiom> axioms_;
	
	private final CHANGE type_;

	public TestAxiomLoader(Iterable<ElkAxiom> axioms) {
		this.axioms_ = axioms;
		type_ = CHANGE.ADD;
	}
	
	public TestAxiomLoader(Iterable<ElkAxiom> axioms, CHANGE type) {
		this.axioms_ = axioms;
		type_ = type;
	}

	//OntologyLoader method
	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomLoader) {

		return new Loader() {

			private final Iterator<ElkAxiom> axiomIterator = axioms_.iterator();

			@Override
			public void load() throws ElkLoadingException {
				while (axiomIterator.hasNext()) {
					if (Thread.currentThread().isInterrupted())
						break;
					axiomLoader.visit(axiomIterator.next());
				}
			}

			@Override
			public void dispose() {
				// nothing to do
			}

		};
	}

	@Override
	public void registerChangeListener(AxiomChangeListener listener) {
	}
	
	//ChangesLoader method
	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter,
			final ElkAxiomProcessor axiomDeleter) {
		
		
		return new Loader() {
			
			private final Iterator<ElkAxiom> axiomIterator = axioms_.iterator();

			@Override
			public void load() throws ElkLoadingException {
				
				while (axiomIterator.hasNext()) {
					if (Thread.currentThread().isInterrupted())
						break;
					switch (type_) {
					case ADD:
						axiomInserter.visit(axiomIterator.next());
						break;
					case DELETE:
						axiomDeleter.visit(axiomIterator.next());
						break;
					}
				}
			}

			@Override
			public void dispose() {
			}
		};
	}

}
