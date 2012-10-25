/**
 * 
 */
package org.semanticweb.elk.reasoner;

import java.util.concurrent.Executors;

import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TestReasonerUtils {

	public static Reasoner createTestReasoner(ReasonerStageExecutor stageExecutor, int maxWorkers) {
		return new Reasoner(stageExecutor, Executors.newSingleThreadExecutor(), maxWorkers);
	}
}
