/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;

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
 * pavel.klinov@uni-ulm.de
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
	 * pavel.klinov@uni-ulm.de
	 */
	class ElkAPIBasedIO implements RandomWalkRunnerIO<ElkAxiom> {

		@Override
		public Reasoner createReasoner(Iterable<ElkAxiom> axioms) {
			Reasoner reasoner = TestReasonerUtils.createTestReasoner(new SimpleStageExecutor());
			
			reasoner.setIncrementalMode(false);
			reasoner.registerOntologyLoader(new TestAxiomLoader(axioms));
			reasoner.registerOntologyChangesLoader(new EmptyChangesLoader());
			
			return reasoner;
		}

		@Override
		public void loadChanges(final Reasoner reasoner,
				final IncrementalChange<ElkAxiom> change) {
			reasoner.registerOntologyChangesLoader(new IncrementalChangeLoader(change, DIRECTION.FORWARD));
		}

		@Override
		public void printAxiom(ElkAxiom axiom, Logger logger, Level level) {
			logger.log(level, "Current axiom: "
					+ OwlFunctionalStylePrinter.toString(axiom));
		}

		@Override
		public void revertChanges(Reasoner reasoner,
				IncrementalChange<ElkAxiom> change) {
			reasoner.registerOntologyChangesLoader(new IncrementalChangeLoader(change, DIRECTION.BACKWARD));
		}
		
	}	
}

