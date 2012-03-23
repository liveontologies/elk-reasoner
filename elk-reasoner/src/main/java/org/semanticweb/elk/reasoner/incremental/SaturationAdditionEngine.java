package org.semanticweb.elk.reasoner.incremental;

import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * The engine to perform re-application of inferences within contexts. It is
 * indented to be executed after the initialization stage using
 * {@link SaturationAdditionInitEngine}.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class SaturationAdditionEngine implements InputProcessor<Void> {

	/**
	 * The engine for re-applying the inferences
	 */
	protected final RuleReApplicationEngine ruleReApplicationEngine;

	public SaturationAdditionEngine(
			RuleReApplicationEngine ruleReApplicationEngine) {
		this.ruleReApplicationEngine = ruleReApplicationEngine;
	}

	public void submit(Void job) throws InterruptedException {
		// nothing to do here
	}

	public void process() throws InterruptedException {
		ruleReApplicationEngine.process();
	}

	public boolean canProcess() {
		return ruleReApplicationEngine.canProcess();
	}

}
