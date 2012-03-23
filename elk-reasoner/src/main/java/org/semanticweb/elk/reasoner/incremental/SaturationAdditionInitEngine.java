package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to initialize re-application of inferences in contexts. It works
 * by identifying indexed class expressions in the context for which some
 * entries have been added, and re-applying inferences with respect to these
 * entries. The re-application of inferences will be continued in the next stage
 * using {@link SaturationAdditionEngine} to avoid the problem with the
 * concurrent access to the derived indexed class expressions of the context.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the contexts with which this engine works
 */
public class SaturationAdditionInitEngine<J extends SaturatedClassExpression>
		implements InputProcessor<J> {

	/**
	 * The engine for re-applying the inferences
	 */
	protected final RuleReApplicationEngine ruleReApplicationEngine;

	public SaturationAdditionInitEngine(
			RuleReApplicationEngine ruleReApplicationEngine) {
		this.ruleReApplicationEngine = ruleReApplicationEngine;
	}

	public void submit(J job) throws InterruptedException {
		ruleReApplicationEngine.processContextAdditions(job);
	}

	public void process() throws InterruptedException {
		// nothing to do here
	}

	public boolean canProcess() {
		// the jobs are immediately processed, so there is nothing to process
		return false;
	}

}
