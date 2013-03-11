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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * An {@link OntologyLoader} that additionally saves the loaded axioms into two
 * collections. The first one keeps changing axioms that can be added or removed
 * by the incremental changes. The second one keeps the remaining axioms.
 * 
 * The first collection contains only instances of {@link ElkClassAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ClassAxiomTrackingOntologyLoader implements OntologyLoader {

	protected final OntologyLoader loader_;
	/**
	 * stores axioms that can be added and removed by incremental changes
	 */
	protected final OnOffVector<ElkAxiom> changingAxioms_;

	/**
	 * stores axioms that should not be added or remove
	 */
	protected final List<ElkAxiom> staticAxioms_;

	public ClassAxiomTrackingOntologyLoader(OntologyLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms, List<ElkAxiom> untrackedAxioms) {
		this.loader_ = loader;
		this.changingAxioms_ = trackedAxioms;
		this.staticAxioms_ = untrackedAxioms;
	}

	ClassAxiomTrackingOntologyLoader(OntologyLoader loader) {
		this(loader, new OnOffVector<ElkAxiom>(127), new ArrayList<ElkAxiom>());
	}

	public OnOffVector<ElkAxiom> getChangingAxioms() {
		return this.changingAxioms_;
	}

	public List<ElkAxiom> getStaticAxioms() {
		return this.staticAxioms_;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter) {

		final ElkAxiomProcessor processor = new ElkAxiomProcessor() {

			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);
				// currently we only allow class axioms to be changed
				if (elkAxiom instanceof ElkClassAxiom) {
					changingAxioms_.add(elkAxiom);
				} else
					staticAxioms_.add(elkAxiom);
			}
		};

		return loader_.getLoader(processor);
	}

}
