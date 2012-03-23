package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.reasoner.rules.SaturatedClassExpression;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to initialize reverting of inferences in contexts. It works by
 * identifying indexed class expressions in the context for which some entries
 * have been deleted, and reverting inferences with respect to these entries.
 * The reverting of inferences will be continued in the next stage using
 * {@link SaturationDeletionEngine} to avoid the problem with the concurrent
 * access to the derived indexed class expressions of the context.
 * 
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the contexts with which this engine works
 */
public class SaturationDeletionInitEngine<J extends SaturatedClassExpression>
		implements InputProcessor<J> {

	/**
	 * The engine for revering inferences
	 */
	protected final RuleUnApplicationEngine ruleUnApplicationEngine;

	public SaturationDeletionInitEngine(
			RuleUnApplicationEngine ruleUnApplicationEngine) {
		this.ruleUnApplicationEngine = ruleUnApplicationEngine;
	}

	public void submit(J job) throws InterruptedException {
		ruleUnApplicationEngine.processContextDeletions(job);
	}

	public void process() throws InterruptedException {
		// nothing to do here
	}

	public boolean canProcess() {
		// the jobs are immediately processed, so there is nothing to process
		return false;
	}

}
