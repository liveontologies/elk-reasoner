/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.AbstractContextSaturationFactory;
import org.semanticweb.elk.reasoner.saturation.ClassExpressionSaturationListener;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.SubContextPremises;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;

/**
 * TODO
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class SubContextSaturationFactory<J extends SubContextSaturationJob<?>> extends 	AbstractContextSaturationFactory<J, SubContextRuleApplicationInput> {

	public SubContextSaturationFactory(
			RuleApplicationFactory<?, SubContextRuleApplicationInput> ruleAppFactory,
			int maxWorkers, ClassExpressionSaturationListener<J> listener) {
		super(ruleAppFactory, maxWorkers, listener);
	}

	@Override
	protected SubContextRuleApplicationInput createRuleApplicationInput(J job) {
		return new SubContextRuleApplicationInput(job.getInput(), job.getSubRoot());
	}

	@Override
	protected boolean isJobFinished(J job, Context jobContext) {
		if (jobContext == null || !jobContext.isInitialized() || !jobContext.isSaturated()) {
			return false;
		}
		
		SubContextPremises subContext = jobContext.getSubContextPremisesByObjectProperty().get(job.getSubRoot());
		
		return subContext != null && subContext.isInitialized();
	}

}
