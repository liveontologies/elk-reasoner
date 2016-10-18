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
package org.semanticweb.elk.loading;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;

/**
 * An {@link AxiomLoader} that treats all kinds of axioms as dynamic (changing)
 * 
 * @see ClassAxiomTrackingLoader and
 *      {@link ClassAndIndividualAxiomTrackingLoader}
 * 
 * @author Pavel Klinov
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class AllAxiomTrackingOntologyLoader extends TestAxiomLoader {

	protected final AxiomLoader loader_;
	/**
	 * stores axioms that can be added and removed by incremental changes
	 */
	protected final OnOffVector<ElkAxiom> changingAxioms_;

	public AllAxiomTrackingOntologyLoader(AxiomLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms) {
		this.loader_ = loader;
		this.changingAxioms_ = trackedAxioms;
	}

	AllAxiomTrackingOntologyLoader(AxiomLoader loader) {
		this(loader, new OnOffVector<ElkAxiom>(127));
	}

	public OnOffVector<ElkAxiom> getChangingAxioms() {
		return this.changingAxioms_;
	}

	@Override
	public void load(final ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {

		final ElkAxiomProcessor trackingAxiomInserter = new ElkAxiomProcessor() {

			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);
				changingAxioms_.add(elkAxiom);
			}
		};

		loader_.load(trackingAxiomInserter, axiomDeleter);
	}

	@Override
	public boolean isLoadingFinished() {
		return loader_.isLoadingFinished();
	}

	@Override
	public void dispose() {
		loader_.dispose();
	}
}
