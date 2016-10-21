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

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;

/**
 * A simple {@link AxiomLoader} which internally keeps sets of axioms to be
 * added
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class TestLoader extends TestAxiomLoader {

	// axioms to be added
	private final Queue<ElkAxiom> axioms_ = new LinkedList<ElkAxiom>();

	public TestLoader() {
		// Empty.
	}

	public TestLoader(Iterable<ElkAxiom> axioms) {
		for (ElkAxiom axiom : axioms) {
			add(axiom);
		}
	}

	public TestLoader add(final ElkAxiom axiom) {
		axioms_.add(axiom);
		return this;
	}

	@Override
	public void load(ElkAxiomProcessor axiomInserter,
			ElkAxiomProcessor axiomDeleter) throws ElkLoadingException {
		for (;;) {
			ElkAxiom axiom = axioms_.poll();
			if (axiom == null)
				break;
			axiomInserter.visit(axiom);
		}

	}

	@Override
	public boolean isLoadingFinished() {
		return axioms_.isEmpty();
	}

}
