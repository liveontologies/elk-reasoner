/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInitializingInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextNotSaturatedCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.NonRedundantRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.tracing.TraceStore;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.Inference;
import org.semanticweb.elk.reasoner.saturation.tracing.inferences.visitors.InferenceInsertionVisitor;

/**
 * Same as {@link RuleApplicationAdditionFactory} but also records all produced
 * {@link Inference}s using a specified {@link TraceStore.Writer}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RuleApplicationFactoryWithTracing extends AbstractRuleApplicationFactory<Context> {

	private final TraceStore.Writer inferenceWriter_;
	
	public RuleApplicationFactoryWithTracing(
			SaturationState<? extends Context> saturationState, TraceStore.Writer inferenceWriter) {
		super(saturationState);
		inferenceWriter_ = inferenceWriter;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ConclusionVisitor<? super Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor,
			SaturationStateWriter<? extends Context> writer,
			SaturationStatistics localStatistics) {
		return SaturationUtils
				.compose(
						// count processed conclusions, if necessary
						SaturationUtils
								.getProcessedConclusionCountingVisitor(localStatistics),
						// write the inference information
						new InferenceInsertionVisitor(inferenceWriter_),
						// insert conclusions initializing contexts if necessary
						new ConclusionInitializingInsertionVisitor(writer),
						// if new, check that the source of the conclusion is
						// not saturated (this is only needed for debugging)
						new ConclusionSourceContextNotSaturatedCheckingVisitor(
								getSaturationState()),
						// count conclusions used in the rules, if necessary
						SaturationUtils
								.getUsedConclusionCountingVisitor(localStatistics),
						// and apply all non-redundant rules
						new NonRedundantRuleApplicationConclusionVisitor(
								ruleVisitor, writer));
	}
}