package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleEngine;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRuleEngine implements
		InputProcessor<IndexedClassExpression>, RuleEngine {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(AbstractRuleEngine.class);

	private final ConclusionVisitor<Context, ?> conclusionProcessor_;

	public AbstractRuleEngine(ConclusionVisitor<Context, ?> conclusionProcessor) {
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
	 *            the active {@link Context} with unprocessed
	 *            {@link Conclusions}
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
	 * Removes and returns the next active {@link Context} that has unprocessed
	 * {@link Conclusion}s. The letter can be retrieved using
	 * {@link #getNextConclusion(Context)}
	 * 
	 * @return the next active {@link Context} to be processed by this
	 *         {@link RuleEngine}
	 */
	abstract Context getNextActiveContext();

}
