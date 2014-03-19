/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInitializingInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextNotSaturatedCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.RuleApplicationVisitorFactory;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationAdditionFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
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
public class RuleApplicationFactoryWithTracing extends
		RuleApplicationAdditionFactory<RuleApplicationInput> {

	private final TraceStore.Writer inferenceWriter_;

	public RuleApplicationFactoryWithTracing(
			SaturationState<? extends Context> saturationState,
			TraceStore.Writer inferenceWriter) {
		super(saturationState);
		inferenceWriter_ = inferenceWriter;
	}

	public RuleApplicationFactoryWithTracing(
			SaturationState<? extends Context> saturationState,
			TraceStore.Writer inferenceWriter,
			RuleApplicationVisitorFactory factory) {
		super(saturationState, factory);
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
						// and apply rules
						getRuleApplicationVisitorFactory().create(ruleVisitor,
								writer));
	}
}