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
import org.semanticweb.elk.reasoner.saturation.SaturationStateImpl;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeapplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;

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

	public RuleDeapplicationFactory(final SaturationStateImpl saturationState,
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
			
			writer_ = saturationState.getWriter(
					getEngineContextModificationListener(listener,
							localStatistics.getContextStatistics()),
					getEngineConclusionVisitor(localStatistics
							.getConclusionStatistics()));
		}

		@Override
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				BasicSaturationStateWriter saturationStateWriter,
				SaturationStatistics localStatistics) {
			
			return new CombinedConclusionVisitor(
					new CombinedConclusionVisitor(
							new ConclusionOccurranceCheckingVisitor(),
							filterRuleConclusionProcessor(
									new ConclusionDeapplicationVisitor(
											saturationStateWriter,
											getEngineCompositionRuleApplicationVisitor(localStatistics
													.getRuleStatistics()),
											getEngineDecompositionRuleApplicationVisitor(
													getDecompositionRuleApplicationVisitor(),
													localStatistics
															.getRuleStatistics())),
									localStatistics)),
					new ConclusionDeletionVisitor());
		}

		@Override
		public void submit(IndexedClassExpression job) {
		}

		@Override
		protected BasicSaturationStateWriter getSaturationStateWriter() {
			return writer_;
		}

		@Override
		protected DecompositionRuleApplicationVisitor getDecompositionRuleApplicationVisitor() {
			//this decomposition visitor takes the basic writer which cannot create new contexts
			return new BackwardDecompositionRuleApplicationVisitor(getSaturationStateWriter());
		}
	}

}
