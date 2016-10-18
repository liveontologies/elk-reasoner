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
package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.loading.TestChangesLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.slf4j.Logger;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public interface RandomWalkRunnerIO<T> {

	void revertChanges(Reasoner reasoner, IncrementalChange<T> change);

	void printAxiom(T axiom, Logger logger, LogLevel level);

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
					new TestChangesLoader(axioms, IncrementalChangeType.ADD),
					new SimpleStageExecutor());

			reasoner.setAllowIncrementalMode(false);

			return reasoner;
		}

		@Override
		public void loadChanges(final Reasoner reasoner,
				final IncrementalChange<ElkAxiom> change) {
			reasoner.registerAxiomLoader(
					new TestAxiomLoaderFactory(new TestChangesLoader(
							change.getAdditions(), change.getDeletions())));
		}

		@Override
		public void printAxiom(ElkAxiom axiom, Logger logger, LogLevel level) {
			LoggerWrap.log(logger, level, OwlFunctionalStylePrinter.toString(axiom));
		}

		@Override
		public void revertChanges(Reasoner reasoner,
				IncrementalChange<ElkAxiom> change) {
			reasoner.registerAxiomLoader(
					new TestAxiomLoaderFactory(new TestChangesLoader(
							change.getDeletions(), change.getAdditions())));
		}

	}
}
