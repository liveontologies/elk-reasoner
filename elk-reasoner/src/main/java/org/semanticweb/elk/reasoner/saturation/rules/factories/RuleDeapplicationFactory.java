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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextExistenceCheckingWriter;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AllRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ComposedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionSourceContextUnsaturationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;

/**
 * Creates an engine which applies rules backwards, e.g., removes conclusions
 * from the context instead of adding them
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class RuleDeapplicationFactory extends RuleApplicationFactory {

	public RuleDeapplicationFactory(final SaturationState saturationState,
			boolean trackModifiedContexts) {
		super(saturationState, trackModifiedContexts);
	}

	@Override
	public DeapplicationEngine getEngine(
			ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new DeapplicationEngine(modListener);
	}

	/**
	 * @param ruleVisitor
	 *            A {@link RuleVisitor} used for rule application
	 * @param writer
	 *            A {@link SaturationStateWriter} to be used for rule
	 *            applications
	 * @return {@link ConclusionVisitor} that applies all rules to the
	 *         {@link Conclusion} if it occurs in the {@link Context}, and
	 *         deletes this {@link Conclusion} from the {@link Context}
	 */
	@SuppressWarnings("unchecked")
	private ConclusionVisitor<Context, Boolean> getDeletionConclusionProcessor(
			RuleVisitor ruleVisitor, SaturationStateWriter writer) {
		return new ComposedConclusionVisitor<Context>(
		// check if conclusion occurs in the context
				new ConclusionOccurrenceCheckingVisitor(),
				// if so, apply the rules, including those that are
				// redundant
				new AllRuleApplicationConclusionVisitor(ruleVisitor, writer),
				// after processing, delete the conclusion
				new ConclusionDeletionVisitor(),
				// and mark the source context as non-saturated
				new ConclusionSourceContextUnsaturationVisitor(saturationState,
						writer));
	}

	/**
	 * 
	 */
	public class DeapplicationEngine extends AbstractRuleEngineWithStatistics {

		private final SaturationStateWriter writer_;

		DeapplicationEngine(SaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			super(getDeletionConclusionProcessor(
					SaturationUtils.getStatsAwareRuleVisitor(localStatistics
							.getRuleStatistics()), saturationStateWriter),
					aggregatedStats, localStatistics);
			writer_ = saturationStateWriter;
		}

		protected DeapplicationEngine(ContextModificationListener listener,
				SaturationStatistics localStatistics) {
			this(
					// producing conclusions only in existing contexts
					new ContextExistenceCheckingWriter(
							// use writer with statistics
							SaturationUtils.getStatAwareWriter(
									saturationState
											.getWriter(
											// check which contexts are modified
											SaturationUtils
													.addStatsToContextModificationListener(
															listener,
															localStatistics
																	.getContextStatistics())),
									localStatistics), saturationState),
					localStatistics);
		}

		protected DeapplicationEngine(ContextModificationListener listener) {
			this(listener, new SaturationStatistics());
		}

		@Override
		public void submit(IndexedClassExpression job) {
			// new jobs cannot be submitted
		}

		@Override
		Context getNextActiveContext() {
			return writer_.pollForActiveContext();
		}

	}

}
