package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

import org.slf4j.Logger;

/**
 * An {@link IncompletenessMonitor} consisting of a combination of several other
 * {@link IncompletenessMonitor}s. That is, this monitor detects incompleteness
 * if and only if at least one of the monitors in the combination detects
 * incompleteness.
 * 
 * @author Yevgeny Kazakov
 */
public class CombinedIncompletenessMonitor implements IncompletenessMonitor {

	/**
	 * The monitors that are combined
	 */
	private final IncompletenessMonitor[] monitors_;

	CombinedIncompletenessMonitor(IncompletenessMonitor... monitors) {
		this.monitors_ = monitors;
	}

	@Override
	public boolean isIncompletenessDetected() {
		for (IncompletenessMonitor monitor : monitors_) {
			if (monitor.isIncompletenessDetected()) {
				return true;
			}
		}
		// else
		return false;
	}

	@Override
	public boolean hasNewExplanation() {
		for (IncompletenessMonitor monitor : monitors_) {
			if (monitor.hasNewExplanation()) {
				return true;
			}
		}
		// else
		return false;
	}

	@Override
	public void explainIncompleteness(Logger logger) {
		for (IncompletenessMonitor monitor : monitors_) {
			monitor.explainIncompleteness(logger);
		}
	}

}
