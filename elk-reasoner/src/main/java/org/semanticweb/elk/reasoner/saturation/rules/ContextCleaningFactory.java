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

/**
 * Creates an engine which works as the de-application engine except that it
 * doesn't modify saturated contexts. The engine is used to "clean" contexts
 * after de-application but if the context is saturated, then cleaning is
 * unnecessary because it's not going to get any extra super-classes after
 * re-application.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ContextCleaningFactory extends RuleDeapplicationFactory {

	public ContextCleaningFactory(final SaturationState saturationState,
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
	 * A {@link RuleDeapplicationFactory} that uses a
	 * {@link SaturationState.Writer} that does not produce conclusions if their
	 * source is marked as saturated.
	 */
	public class Engine extends RuleDeapplicationFactory.Engine {

		protected Engine() {
			super(saturationState.getSaturationCheckingWriter());
		}

		protected Engine(final ContextCreationListener listener) {
			this(listener, new ThisStatistics());
		}

		protected Engine(final ContextCreationListener listener,
				final ThisStatistics factoryStats) {
			super(saturationState
					.getSaturationCheckingWriter(getEngineListener(listener,
							factoryStats)), factoryStats);
		}

	}
}
