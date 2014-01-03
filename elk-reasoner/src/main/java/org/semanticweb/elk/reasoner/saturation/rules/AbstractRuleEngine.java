package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRuleEngine implements
		InputProcessor<IndexedClassExpression>, RuleEngine {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleEngine.class);

	private final ConclusionVisitor<?> conclusionProcessor_;

	public AbstractRuleEngine(ConclusionVisitor<?> conclusionProcessor) {
		this.conclusionProcessor_ = conclusionProcessor;
	}

	@Override
	public void process() throws InterruptedException {
		for (;;) {
			if (Thread.currentThread().isInterrupted())
				break;
			Context nextContext = getNextActiveContext();
			if (nextContext == null) {
				break;
			}
			process(nextContext);
		}
	}

	/**
	 * Process all pending {@link Conclusions} the given {@link Context}
	 * 
	 * @param context
	 *            the context in which to process the scheduled items
	 */
	protected void process(Context context) {
		for (;;) {
			Conclusion conclusion = context.takeToDo();			
			if (conclusion == null)
				return;
			LOGGER_.trace("{}: processing conclusion {}", context, conclusion);
			conclusion.accept(conclusionProcessor_, context);
		}
	}

	/**
	 * @return the next {@link Context} to be processed by this
	 *         {@link RuleEngine}
	 */
	abstract Context getNextActiveContext();

}
