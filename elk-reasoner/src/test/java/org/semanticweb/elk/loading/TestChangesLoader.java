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
package org.semanticweb.elk.loading;

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangeType;

/**
 * A simple {@link AxiomLoader} which internally keeps sets of axioms to be
 * added or removed
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 * @author Peter Skocovsky
 */
public class TestChangesLoader extends TestAxiomLoader {

	// axioms that are coming in the changes
	private final Queue<ElkAxiom> axioms_ = new LinkedList<ElkAxiom>();
	// sequence of changes: true - additions, false - deletions
	private final Queue<Boolean> changes_ = new LinkedList<Boolean>();

	public TestChangesLoader() {
		// Empty.
	}

	public TestChangesLoader(Iterable<ElkAxiom> axioms,
			IncrementalChangeType type) {
		for (ElkAxiom axiom : axioms) {
			switch (type) {
			case ADD:
				add(axiom);
				break;
			case DELETE:
				remove(axiom);
				break;
			}
		}
	}

	public TestChangesLoader(Iterable<ElkAxiom> additions,
			Iterable<ElkAxiom> deletions) {
		for (ElkAxiom addition : additions) {
			add(addition);
		}

		for (ElkAxiom deletion : deletions) {
			remove(deletion);
		}
	}

	@Override
	public void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
		for (;;) {
			ElkAxiom axiom = axioms_.poll();
			if (axiom == null)
				break;
			boolean isAdded = changes_.poll();
			if (isAdded)
				axiomInserter.visit(axiom);
			else
				axiomDeleter.visit(axiom);
		}
	}

	@Override
	public boolean isLoadingFinished() {
		return axioms_.isEmpty();
	}

	public TestChangesLoader add(final ElkAxiom axiom) {
		axioms_.add(axiom);
		changes_.add(true);
		return this;
	}

	public TestChangesLoader remove(final ElkAxiom axiom) {
		axioms_.add(axiom);
		changes_.add(false);
		return this;
	}

}