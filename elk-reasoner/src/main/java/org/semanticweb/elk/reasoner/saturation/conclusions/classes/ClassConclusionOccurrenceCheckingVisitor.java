package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.ClassConclusionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that checks if visited {@link ClassConclusion} is
 * contained the given {@link ClassConclusionSet}. The visit method returns {@link
 * true} if the {@link ClassConclusionSet} is occurs in the {@link ClassConclusionSet} and
 * {@link false} otherwise.
 * 
 * @see ClassConclusionInsertionVisitor
 * @see ClassConclusionDeletionVisitor
 * @see MirrorConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionOccurrenceCheckingVisitor extends
		AbstractClassConclusionVisitor<ClassConclusionSet, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionOccurrenceCheckingVisitor.class);

	// TODO: make this by combining the visitor in order to avoid overheads when
	// logging is switched off
	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion, ClassConclusionSet context) {
		boolean result = context.containsConclusion(conclusion);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: check occurrence of {}: {}", context,
					conclusion, result ? "success" : "failure");
		}
		return result;
	}

}
