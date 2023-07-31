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
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;

/**
 * A {@link RuleApplicationFactory} that works similarly to
 * {@link RuleApplicationAdditionFactory} except allowing contexts to become
 * not-saturated after additions.
 * 
 * @see Context#isSaturated()
 * 
 * @author Yevgeny Kazakov
 * @author Pavel Klinov
 */
public class RuleApplicationIncrementalAdditionSaturationFactory
		extends RuleApplicationAdditionFactory<RuleApplicationInput> {

	public RuleApplicationIncrementalAdditionSaturationFactory(
			final InterruptMonitor interrupter,
			SaturationState<? extends Context> saturationState) {
		super(interrupter, saturationState);
	}

	@Override
	protected ClassConclusion.Visitor<Boolean> getPreInsertionHook(
			Reference<Context> activeContext) {
		return null;
	}

}
