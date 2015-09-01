/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules.factories;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextExistenceCheckingWriter;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.RuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * A {@link RuleApplicationFactory} that deletes the produced {@link Conclusion}
 * s from the respective {@link Context} and applies rules, which in turn
 * produce {@link Conclusion} s for which this process repeats if they have not
 * been processed already. This {@link RuleApplicationFactory} never creates new
 * {@link Context}s.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleApplicationDeletionFactory extends
		AbstractRuleApplicationFactory<Context, RuleApplicationInput> {

	public RuleApplicationDeletionFactory(
			SaturationState<? extends Context> saturationState) {
		super(saturationState);
	}

	@Override
	protected SaturationStateWriter<? extends Context> getBaseWriter(
			ContextCreationListener creationListener,
			ContextModificationListener modificationListener) {
		// writer cannot create new contexts
		return getSaturationState().getContextModifyingWriter(
				modificationListener);
	}

	@Override
	protected SaturationStateWriter<Context> getFinalWriter(
			SaturationStateWriter<? extends Context> writer) {
		// only write to exiting contexts
		return new ContextExistenceCheckingWriter<Context>(writer,
				getSaturationState());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ConclusionVisitor<? super Context, Boolean> getConclusionProcessor(
			RuleVisitor<?> ruleVisitor,
			SaturationStateWriter<? extends Context> writer,
			SaturationStatistics localStatistics) {
		return SaturationUtils
				.compose(
				// count processed conclusions, if necessary
						SaturationUtils
								.getProcessedConclusionCountingVisitor(localStatistics),
						// check if conclusion occurs in the context and proceed
						new ConclusionOccurrenceCheckingVisitor(),
						// count conclusions used in the rules, if necessary
						SaturationUtils
								.getUsedConclusionCountingVisitor(localStatistics),
						// apply rules
						new RuleApplicationConclusionVisitor(ruleVisitor,
								writer),
						// after processing, delete the conclusion
						new ConclusionDeletionVisitor(),
						// and mark the source context of the conclusion as
						// non-saturated
						new ConclusionSourceContextUnsaturationVisitor(writer));
	}
}
