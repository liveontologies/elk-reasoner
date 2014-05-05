/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleEngineWithStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.reasoner.saturation.rules.factories.WorkerLocalTodo;
import org.semanticweb.elk.reasoner.saturation.tracing.LocalTracingSaturationState.TracedContext;

/**
 * Can either init tracing by producing the context initialization conclusion or
 * resume tracing by producing conclusions which were missing in the main
 * saturation state during the previous tracing pass.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingRuleEngine<I extends RuleApplicationInput> extends AbstractRuleEngineWithStatistics<I> {

	private final SaturationStateWriter<? extends TracedContext> tracingWriter_;
	
	private final Conclusion contextInitConclusion_;
	
	protected TracingRuleEngine(
			ConclusionVisitor<? super Context, Boolean> conclusionProcessor,
			WorkerLocalTodo localTodo,
			SaturationStateWriter<? extends TracedContext> writer,
			ContextInitialization contextInit,
			SaturationStatistics aggregatedStatistics,
			SaturationStatistics localStatistics) {
		super(conclusionProcessor, localTodo, aggregatedStatistics,
				localStatistics);
		tracingWriter_ = writer;
		contextInitConclusion_ = contextInit;
	}

	@Override
	public void submit(I job) {
		TracedContext context = tracingWriter_.getSaturationState().getContext(job.getRoot());
		
		if (context == null || context.getMissingSubConclusions().isEmpty()) {
			// the beginning of tracing
			tracingWriter_.produce(job.getRoot(), contextInitConclusion_);
		}
		else {
			// resuming tracing
			for (IndexedClassExpression root : context.getMissingSubConclusions().keySet()) {
				for (Conclusion missing : context.getMissingSubConclusions().get(root)) {
					tracingWriter_.produce(root, missing);
				}
			}
		}
	}

	@Override
	protected Context getNextActiveContext() {
		return tracingWriter_.pollForActiveContext();
	}
	

}
