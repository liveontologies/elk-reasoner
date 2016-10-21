/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.saturation.rules.factories;

import org.semanticweb.elk.Reference;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionTracingContextNotSaturatedCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ContextInitializingClassConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.RuleApplicationClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInferenceConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * A {@link RuleApplicationFactory} that adds the produced
 * {@link ClassConclusion}s to the respective {@link Context} (creating new if
 * necessary) and applies rules, which in turn produce new
 * {@link ClassConclusion}s for which this process repeats if they have not been
 * processed already. This {@link RuleApplicationFactory} should not produce
 * {@link ClassConclusion}s for which the source {@link Context} is already
 * saturated.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * 
 */
public class RuleApplicationAdditionFactory<I extends RuleApplicationInput>
		extends
			AbstractRuleApplicationFactory<Context, I> {

	public RuleApplicationAdditionFactory(final InterruptMonitor interrupter,
			SaturationState<?> saturationState) {
		super(interrupter, saturationState);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ClassInference.Visitor<Boolean> getInferenceProcessor(
			Reference<Context> activeContext, RuleVisitor<?> ruleVisitor,
			SaturationStateWriter<? extends Context> writer,
			SaturationStatistics localStatistics) {
		return new ClassInferenceConclusionVisitor<Boolean>(
				// measuring time, if necessary
				SaturationUtils.getTimedConclusionVisitor(
						SaturationUtils.compose(
								// count processed conclusions, if necessary
								SaturationUtils
										.getClassInferenceCountingVisitor(
												localStatistics),
								// insert conclusions initializing contexts if
								// necessary
								new ContextInitializingClassConclusionInsertionVisitor(
										activeContext, writer),
								// if new, check that the source of the
								// conclusion is
								// not saturated (this is only needed for
								// debugging)
								new ClassConclusionTracingContextNotSaturatedCheckingVisitor(
										activeContext, getSaturationState()),
								// count conclusions used in the rules, if
								// necessary
								SaturationUtils
										.getClassConclusionCountingVisitor(
												localStatistics),
								// and apply all rules
								new RuleApplicationClassConclusionVisitor(
										activeContext,
										getSaturationState().getOntologyIndex()
												.getContextInitRuleHead(),
										ruleVisitor, writer)),
						localStatistics));
	}
}
