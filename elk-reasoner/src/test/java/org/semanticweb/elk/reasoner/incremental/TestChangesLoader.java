/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.loading.AxiomChangeListener;
import org.semanticweb.elk.loading.ChangesLoader;
import org.semanticweb.elk.loading.ElkLoadingException;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.SimpleElkAxiomChange;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * A simple {@link ChangesLoader} which internally keeps sets of axioms to be
 * added or removed
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class TestChangesLoader implements ChangesLoader, OntologyLoader {

	// axioms that are coming in the changes
	private final Queue<ElkAxiom> axioms_ = new LinkedList<ElkAxiom>();
	// sequence of changes: true - additions, false - deletions
	private final Queue<Boolean> changes_ = new LinkedList<Boolean>();
	private AxiomChangeListener listener_ = null;

	public TestChangesLoader add(final ElkAxiom axiom) {
		if (listener_ != null) {
			listener_.notify(new SimpleElkAxiomChange(axiom, 1));
		}
		axioms_.add(axiom);
		changes_.add(true);
		return this;
	}

	public TestChangesLoader remove(final ElkAxiom axiom) {
		if (listener_ != null) {
			listener_.notify(new SimpleElkAxiomChange(axiom, -1));
		}
		axioms_.add(axiom);
		changes_.add(false);
		return this;
	}

	public void clear() {
		axioms_.clear();
		changes_.clear();
	}

	@Override
	public Loader getLoader(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) {
		return new TestLoader(axiomInserter, axiomDeleter);
	}

	@Override
	public Loader getLoader(ElkAxiomProcessor axiomLoader) {
		return new TestLoader(axiomLoader, new ElkAxiomProcessor() {
			@Override
			public void visit(ElkAxiom elkAxiom) {
				// does nothing
			}
		});
	}

	@Override
	public void registerChangeListener(AxiomChangeListener listener) {
		listener_ = listener;
	}

	/**
	 * 
	 */
	class TestLoader implements Loader {

		private final ElkAxiomProcessor inserter_, deleter_;

		TestLoader(ElkAxiomProcessor inserter, ElkAxiomProcessor deleter) {
			inserter_ = inserter;
			deleter_ = deleter;
		}

		@Override
		public void load() throws ElkLoadingException {
			for (;;) {
				ElkAxiom axiom = axioms_.poll();
				if (axiom == null)
					break;
				boolean isAdded = changes_.poll();
				if (isAdded)
					inserter_.visit(axiom);
				else
					deleter_.visit(axiom);
			}
		}

		@Override
		public void dispose() {
		}
	}
}