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

import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;

/**
 * An {@link AxiomLoader} that additionally saves the loaded axioms into two
 * collections. The first one keeps changing axioms that can be added or removed
 * by the incremental changes. The second one keeps the remaining axioms.
 * 
 * The first collection contains only instances of {@link ElkClassAxiom}
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class ClassAxiomTrackingLoader extends TestAxiomLoader {

	protected final AxiomLoader loader_;
	/**
	 * stores axioms that can be added and removed by incremental changes
	 */
	protected final OnOffVector<ElkAxiom> changingAxioms_;

	/**
	 * stores axioms that should not be added or remove
	 */
	protected final List<ElkAxiom> staticAxioms_;

	public ClassAxiomTrackingLoader(AxiomLoader loader,
			OnOffVector<ElkAxiom> trackedAxioms, List<ElkAxiom> untrackedAxioms) {
		this.loader_ = loader;
		this.changingAxioms_ = trackedAxioms;
		this.staticAxioms_ = untrackedAxioms;
	}

	ClassAxiomTrackingLoader(AxiomLoader loader) {
		this(loader, new OnOffVector<ElkAxiom>(127), new ArrayList<ElkAxiom>());
	}

	public OnOffVector<ElkAxiom> getChangingAxioms() {
		return this.changingAxioms_;
	}

	public List<ElkAxiom> getStaticAxioms() {
		return this.staticAxioms_;
	}

	@Override
	public void load(final ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {

		final ElkAxiomProcessor wrappedAxiomInserter = new ElkAxiomProcessor() {
			@Override
			public void visit(ElkAxiom elkAxiom) {
				axiomInserter.visit(elkAxiom);
				// currently we only allow class axioms to be changed
				if (elkAxiom instanceof ElkClassAxiom) {
					changingAxioms_.add(elkAxiom);
				} else {
					staticAxioms_.add(elkAxiom);
				}
			}
		};
		loader_.load(wrappedAxiomInserter, axiomDeleter);

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
