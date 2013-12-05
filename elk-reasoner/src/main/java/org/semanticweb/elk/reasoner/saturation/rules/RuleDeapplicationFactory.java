/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.rules;

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
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeapplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

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
	public DeapplicationEngine getDefaultEngine(
			ContextCreationListener listener,
			ContextModificationListener modListener) {
		return new DeapplicationEngine(modListener);
	}

	/**
	 * 
	 */
	public class DeapplicationEngine extends RuleApplicationFactory.BaseEngine {

		private final BasicSaturationStateWriter writer_;

		protected DeapplicationEngine(ContextModificationListener listener) {
			super(new SaturationStatistics());

			writer_ = saturationState.getWriter(SaturationUtils
					.addStatsToContextModificationListener(listener,
							localStatistics.getContextStatistics()),
					SaturationUtils.addStatsToConclusionVisitor(localStatistics
							.getConclusionStatistics()));
		}

		@Override
		protected ConclusionVisitor<Boolean, Context> getBaseConclusionProcessor() {
			BasicSaturationStateWriter saturationStateWriter = getSaturationStateWriter();

			return new CombinedConclusionVisitor<Context>(
					new CombinedConclusionVisitor<Context>(
							new ConclusionOccurranceCheckingVisitor(),
							getUsedConclusionsCountingVisitor(new ConclusionDeapplicationVisitor(
									saturationStateWriter,
									getStatsAwareCompositionRuleAppVisitor(localStatistics),
									getStatsAwareDecompositionRuleAppVisitor(getDecompositionRuleApplicationVisitor(), localStatistics)))),
					new ConclusionDeletionVisitor());
		}
		
		@Override
		public void submit(IndexedClassExpression job) {
		}

		@Override
		protected BasicSaturationStateWriter getSaturationStateWriter() {
			return writer_;
		}

		protected DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor() {
			// this decomposition visitor takes the basic writer which cannot
			// create new contexts
			return new BackwardDecompositionRuleApplicationVisitor(getSaturationStateWriter());
		}
	}

}
