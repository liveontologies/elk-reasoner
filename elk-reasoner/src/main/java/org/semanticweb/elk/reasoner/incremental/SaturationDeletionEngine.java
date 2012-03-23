package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to perform reverting of inferences within contexts. It is intended
 * to be executed after the initialization stage using
 * {@link SaturationDeletionInitEngine}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationDeletionEngine implements InputProcessor<Void> {

	/**
	 * The engine for revering inferences
	 */
	protected final RuleUnApplicationEngine ruleUnApplicationEngine;

	public SaturationDeletionEngine(
			RuleUnApplicationEngine ruleUnApplicationEngine) {
		this.ruleUnApplicationEngine = ruleUnApplicationEngine;
	}

	public void submit(Void job) throws InterruptedException {
		// nothing to do here
	}

	public void process() throws InterruptedException {
		ruleUnApplicationEngine.process();
	}

	public boolean canProcess() {
		return ruleUnApplicationEngine.canProcess();
	}

}
