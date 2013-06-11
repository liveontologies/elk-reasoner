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

import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * An {@link OntologyLoader} that treats all kinds of axioms as dynamic (changing)
 * 
 * @see ClassAxiomTrackingOntologyLoader and {@link ClassAndIndividualAxiomTrackingOntologyLoader}
 * 
 * @author Pavel Klinov
 * 
 */
public class AllAxiomTrackingOntologyLoader implements OntologyLoader {

	protected final OntologyLoader loader_;
	/**
	 * stores axioms that can be added and removed by incremental changes
	 */
	protected final OnOffVector<ElkAxiom> changingAxioms_;


	public AllAxiomTrackingOntologyLoader(OntologyLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms) {
		this.loader_ = loader;
		this.changingAxioms_ = trackedAxioms;
	}

	AllAxiomTrackingOntologyLoader(OntologyLoader loader) {
		this(loader, new OnOffVector<ElkAxiom>(127));
	}

	public OnOffVector<ElkAxiom> getChangingAxioms() {
		return this.changingAxioms_;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter) {

		final ElkAxiomProcessor processor = new ElkAxiomProcessor() {

			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);
				changingAxioms_.add(elkAxiom);
			}
		};

		return loader_.getLoader(processor);
	}

}
