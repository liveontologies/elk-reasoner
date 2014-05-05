/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.SubContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleEngineWithStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.factories.WorkerLocalTodo;

/**
 * Starts the rule application process by initializing the given sub-context.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RuleEngineForSubContextSaturation extends
		AbstractRuleEngineWithStatistics<SubContextRuleApplicationInput> {

	/**
	 * a {@link SaturationStateWriter} to produce new {@link Conclusion}s and
	 * query for active {@link Context}s
	 */
	private final SaturationStateWriter<?> writer_;


	protected RuleEngineForSubContextSaturation(
			ConclusionVisitor<? super Context, Boolean> conclusionProcessor,
			WorkerLocalTodo localTodo, SaturationStateWriter<?> writer,
			SaturationStatistics aggregatedStatistics,
			SaturationStatistics localStatistics) {
		super(conclusionProcessor, localTodo, aggregatedStatistics,
				localStatistics);
		this.writer_ = writer;
	}

	@Override
	public void submit(SubContextRuleApplicationInput job) {
		writer_.produce(job.getRoot(), new SubContextInitializationImpl(job.getSubRoot()));
	}

	@Override
	protected Context getNextActiveContext() {
		return writer_.pollForActiveContext();
	}

	protected final SaturationStateWriter<?> getWriter() {
		return writer_;
	}

}
