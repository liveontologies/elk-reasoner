package org.semanticweb.elk.reasoner.saturation.conclusions.classes;

import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.ClassConclusionSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link ClassConclusion.Visitor} that removes the visited {@link ClassConclusion} from
 * the given {@link ClassConclusionSet}. The visit method returns {@link true} if the
 * {@link ClassConclusionSet} was modified as the result of this operation, i.e., the
 * {@link ClassConclusion} was contained in the {@link ClassConclusionSet}.
 * 
 * @see ClassConclusionInsertionVisitor
 * @see ClassConclusionOccurrenceCheckingVisitor
 * 
 * @author "Yevgeny Kazakov"
 */
public class ClassConclusionDeletionVisitor extends
		AbstractClassConclusionVisitor<ClassConclusionSet, Boolean> {

	// logger for events
	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(ClassConclusionDeletionVisitor.class);

	// TODO: make this by combining the visitor in order to avoid overheads when
	// logging is switched off
	@Override
	protected Boolean defaultVisit(ClassConclusion conclusion,
			ClassConclusionSet conclusions) {
		boolean result = conclusions.removeConclusion(conclusion);
		if (LOGGER_.isTraceEnabled()) {
			LOGGER_.trace("{}: deleting {}: {}", conclusions, conclusion,
					result ? "success" : "failure");
		}
		return result;
	}

}
