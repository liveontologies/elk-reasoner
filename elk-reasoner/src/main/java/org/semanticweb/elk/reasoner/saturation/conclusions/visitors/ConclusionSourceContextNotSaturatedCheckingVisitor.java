package org.semanticweb.elk.reasoner.saturation.conclusions.visitors;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ConclusionVisitor} that returns {@code true} if the source of the
 * {@link Conclusion} relative to the given parameter {@link Context} is not
 * saturated.
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
public class ConclusionSourceContextNotSaturatedCheckingVisitor extends
		AbstractConclusionVisitor<Context, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ConclusionSourceContextNotSaturatedCheckingVisitor.class);

	@Override
	Boolean defaultVisit(Conclusion conclusion, Context context) {
		boolean result = !conclusion.getSourceContext(context).isSaturated();
		LOGGER_.trace(
				"{}: check that source context is not saturated for {}: {}",
				context, conclusion, result ? "success" : "failure");
		return result;
	}
}
