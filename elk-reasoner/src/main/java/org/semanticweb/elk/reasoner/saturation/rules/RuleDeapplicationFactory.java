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

import org.semanticweb.elk.reasoner.saturation.ContextCreationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeapplicationVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionDeletionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionOccurranceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.CombinedConclusionVisitor;

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

	public RuleDeapplicationFactory(final SaturationState saturationState) {
		super(saturationState);
	}

	public RuleDeapplicationFactory(final SaturationState saturationState,
			boolean trackModifiedContexts) {
		super(saturationState, trackModifiedContexts);
	}

	@Override
	public Engine getEngine() {
		return new Engine();
	}

	@Override
	public Engine getEngine(ContextCreationListener listener) {
		return new Engine(listener);
	}

	/**
	 * 
	 */
	public class Engine extends RuleApplicationFactory.Engine {

		protected Engine() {
			super();
		}

		protected Engine(ContextCreationListener listener) {
			super(listener);
		}

		protected Engine(SaturationState.Writer saturationStateWriter,
				ThisStatistics factoryStats) {
			super(saturationStateWriter, factoryStats);
		}

		protected Engine(SaturationState.Writer saturationStateWriter) {
			super(saturationStateWriter);
		}

		@Override
		protected ConclusionVisitor<Boolean> getBaseConclusionProcessor(
				SaturationState.Writer saturationStateWriter,
				ThisStatistics localStatistics) {
			return new CombinedConclusionVisitor(
					new CombinedConclusionVisitor(
							new ConclusionOccurranceCheckingVisitor(),
							filterRuleConclusionProcessor(
									new ConclusionDeapplicationVisitor(
											saturationStateWriter),
									localStatistics)),
					new ConclusionDeletionVisitor());
		}
	}

}
