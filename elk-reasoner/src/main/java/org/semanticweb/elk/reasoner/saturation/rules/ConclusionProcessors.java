package org.semanticweb.elk.reasoner.saturation.rules;

import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AllRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextProcessorVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.NonRedundantRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.context.ContextProcessor;

public class ConclusionProcessors {

	/**
	 * @param ruleVisitor
	 *            A {@link RuleVisitor} used for rule application
	 * @param producer
	 *            A {@link ConclusionProducer} to be used for rule applications
	 * @return {@link ConclusionVisitor} that inserts given {@link Conclusion}s
	 *         to the {@link Context}, and if it was new, applies all
	 *         non-redundant rules to
	 */
	public static ConclusionVisitor<Boolean> getInsertionConclusionProcessor(
			RuleVisitor ruleVisitor, ConclusionProducer producer) {
		return new CombinedConclusionVisitor(
		// add conclusion to the context
				new ConclusionInsertionVisitor(),
				// if new, apply the rules
				new NonRedundantRuleApplicationConclusionVisitor(ruleVisitor,
						producer));
	}

	/**
	 * /**
	 * 
	 * @param ruleVisitor
	 *            A {@link RuleVisitor} used for rule application
	 * @param producer
	 *            A {@link ConclusionProducer} to be used for rule applications
	 * @return {@link ConclusionVisitor} that applies all rules to the
	 *         {@link Conclusion} if it occurs in the {@link Context}, and
	 *         deletes this {@link Conclusion} from the {@link Context}
	 */
	public static ConclusionVisitor<Boolean> getDeletionConclusionProcessor(
			RuleVisitor ruleVisitor, ConclusionProducer producer) {
		return new CombinedConclusionVisitor(
				new CombinedConclusionVisitor(
				// check if conclusion occurs in the context
						new ConclusionOccurranceCheckingVisitor(),
						// if so, apply the rules, including those that are
						// redundant
						new AllRuleApplicationConclusionVisitor(ruleVisitor,
								producer)),
				// after processing, delete the conclusion
				new ConclusionDeletionVisitor());
	}

	/**
	 * Creates a {@link ConclusionVisitor} that processes the source context of
	 * the successfully processed {@link Conclusion} using the provided
	 * {@link ContextProcessor}
	 * 
	 * @param conclusionProcessor
	 * @param contextProcessor
	 * @return
	 */
	public static ConclusionVisitor<Boolean> getTrackingConclusionProcessor(
			ConclusionVisitor<Boolean> conclusionProcessor,
			ContextProcessor contextProcessor) {
		return new CombinedConclusionVisitor(conclusionProcessor,
				new ConclusionSourceContextProcessorVisitor(contextProcessor));
	}

}
