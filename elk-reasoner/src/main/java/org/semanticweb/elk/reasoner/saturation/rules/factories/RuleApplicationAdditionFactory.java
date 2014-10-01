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

import org.semanticweb.elk.reasoner.saturation.ConclusionAbsenceCheckingWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInitializingInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextNotSaturatedCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.NonRedundantRuleApplicationVisitorFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.RuleApplicationVisitorFactory;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link RuleApplicationFactory} that adds the produced {@link Conclusion}s
 * to the respective {@link Context} (creating new if necessary) and applies
 * non-redundant rules, which in turn produce new {@link Conclusion}s for which
 * this process repeats if they have not been processed already. This
 * {@link RuleApplicationFactory} should not produce {@link Conclusion}s for
 * which the source {@link Context} is already saturated.
 * 
 * @author Frantisek Simancik
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 * @author Pavel Klinov
 * 
 */
public class RuleApplicationAdditionFactory extends
		AbstractRuleApplicationFactory<Context> {

	private final RuleApplicationVisitorFactory ruleAppVisitorFactory_;

	public RuleApplicationAdditionFactory(SaturationState<?> saturationState) {
		// this factory applies non-redundant rules by default
		this(saturationState, new NonRedundantRuleApplicationVisitorFactory());
	}

	public RuleApplicationAdditionFactory(SaturationState<?> saturationState,
			RuleApplicationVisitorFactory factory) {
		super(saturationState);
		ruleAppVisitorFactory_ = factory;
	}

	protected final RuleApplicationVisitorFactory getRuleApplicationVisitorFactory() {
		return ruleAppVisitorFactory_;
	}

	@Override
	SaturationStateWriter<Context> getFinalWriter(
			SaturationStateWriter<? extends Context> writer) {
		// only write conclusions that are absent
		return new ConclusionAbsenceCheckingWriter<Context>(writer,
				getSaturationState());
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
						ruleAppVisitorFactory_.create(ruleVisitor, writer));
	}
}
