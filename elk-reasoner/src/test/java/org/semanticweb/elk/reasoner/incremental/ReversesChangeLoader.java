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

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.SimpleElkAxiomChange;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

public class ReversesChangeLoader implements ChangesLoader {

	protected static final Logger LOGGER_ = Logger
			.getLogger(TrackingChangesLoader.class);

	/**
	 * The change to be used by this {@link ChangesLoader}
	 */
	private final IncrementalChange change_;

	/**
	 * change listener if any is needed
	 */
	private AxiomChangeListener listener_;

	public ReversesChangeLoader(IncrementalChange change) {
		this.change_ = change;
	}

	@Override
	public Loader getLoader(final ElkAxiomProcessor axiomInserter,
			final ElkAxiomProcessor axiomDeleter) {

		return new Loader() {

			private final Iterator<ElkAxiom> additions_ = change_
					.getAdditions().iterator();
			private final Iterator<ElkAxiom> deletions_ = change_
					.getDeletions().iterator();

			@Override
			public void load() throws ElkLoadingException {
				// first add back the deleted axioms, then remove the added
				// axioms
				while (deletions_.hasNext()) {
					if (Thread.currentThread().isInterrupted())
						break;
					ElkAxiom axiom = deletions_.next();
					axiomInserter.visit(axiom);
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace("adding: "
								+ OwlFunctionalStylePrinter.toString(axiom));
					if (listener_ != null)
						listener_.notify(new SimpleElkAxiomChange(axiom, 1));
				}
				while (additions_.hasNext()) {
					if (Thread.currentThread().isInterrupted())
						break;
					ElkAxiom axiom = additions_.next();
					axiomDeleter.visit(axiom);
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace("removing: "
								+ OwlFunctionalStylePrinter.toString(axiom));
					if (listener_ != null)
						listener_.notify(new SimpleElkAxiomChange(axiom, -1));
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
		this.listener_ = listener;
	}

}
