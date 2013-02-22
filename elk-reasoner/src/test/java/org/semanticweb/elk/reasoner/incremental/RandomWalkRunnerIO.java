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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangeLoader.DIRECTION;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface RandomWalkRunnerIO<T> {

	void revertChanges(Reasoner reasoner, IncrementalChange<T> change);

	void printAxiom(T axiom, Logger logger, Level level);

	Reasoner createReasoner(Iterable<T> axioms);

	void loadChanges(Reasoner reasoner, IncrementalChange<T> change);

	/**
	 * 
	 * A simple implementation based on the internal API
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class ElkAPIBasedIO implements RandomWalkRunnerIO<ElkAxiom> {

		@Override
		public Reasoner createReasoner(Iterable<ElkAxiom> axioms) {
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(
					new TestAxiomLoader(axioms), new SimpleStageExecutor());

			reasoner.setIncrementalMode(false);
			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());

			return reasoner;
		}

		@Override
		public void loadChanges(final Reasoner reasoner,
				final IncrementalChange<ElkAxiom> change) {
			reasoner.registerOntologyChangesLoader(new IncrementalChangeLoader(
					change, DIRECTION.FORWARD));
		}

		@Override
		public void printAxiom(ElkAxiom axiom, Logger logger, Level level) {
			logger.log(level, OwlFunctionalStylePrinter.toString(axiom));
		}

		@Override
		public void revertChanges(Reasoner reasoner,
				IncrementalChange<ElkAxiom> change) {
			reasoner.registerOntologyChangesLoader(new IncrementalChangeLoader(
					change, DIRECTION.BACKWARD));
		}

	}
}
