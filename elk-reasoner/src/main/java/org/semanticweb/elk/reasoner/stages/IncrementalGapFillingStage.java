/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;

import java.util.Collection;

import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturation;
import org.semanticweb.elk.reasoner.saturation.rules.ContextCompletionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationFactory;

/**
 * FIXME Better name
 * 
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalGapFillingStage extends AbstractReasonerStage {

	private ClassExpressionSaturation<IndexedClassExpression> completion_;

	public IncrementalGapFillingStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Incremental Gap Filling";
	}

	@Override
	public void printInfo() {
		// TODO Auto-generated method stub

	}

	@Override
	boolean preExecute() {
		if (!super.preExecute()) {
			return false;
		}

		RuleApplicationFactory ruleAppFactory = new ContextCompletionFactory(
				reasoner.saturationState);
		Collection<IndexedClassExpression> inputs = reasoner.saturationState
				.getNotSaturatedContexts();

		completion_ = new ClassExpressionSaturation<IndexedClassExpression>(
				inputs, reasoner.getProcessExecutor(), workerNo,
				reasoner.getProgressMonitor(), ruleAppFactory);

		return true;
	}

	@Override
	void executeStage() throws ElkException {
		for (;;) {
			completion_.process();

			if (!spuriousInterrupt())
				break;
		}
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute()) {
			return false;
		}

		completion_ = null;

		return true;
	}

}
