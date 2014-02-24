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

import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ComposedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInitializingInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.NonRedundantRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link RuleApplicationFactory} that works similarly to
 * {@link RuleApplicationAdditionFactory} except for marking the source
 * {@link Context} of the produced {@link Conclusion}s as not saturated.
 * 
 * @author Yevgeny Kazakov
 * @author Pavel Klinov
 */
public class RuleApplicationAdditionUnSaturationFactory extends
		RuleApplicationAdditionFactory {

	public RuleApplicationAdditionUnSaturationFactory(
			SaturationState saturationState) {
		super(saturationState);
	}

	@Override
	@SuppressWarnings("unchecked")
	ConclusionVisitor<Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor, SaturationStateWriter writer,
			SaturationStatistics localStatistics) {
		// the visitor used for inserting conclusion
		return new ComposedConclusionVisitor<Context>(
		// insert conclusions initializing contexts if necessary
				new ConclusionInitializingInsertionVisitor(writer),
				// if new, mark the source context as unsaturated
				new ConclusionSourceContextUnsaturationVisitor(writer),
				// afterwards, apply all non-redundant rules, collecting
				// statistics if necessary
				SaturationUtils.getUsedConclusionCountingProcessor(
						new NonRedundantRuleApplicationConclusionVisitor(
								ruleVisitor, writer), localStatistics));
	}
}
