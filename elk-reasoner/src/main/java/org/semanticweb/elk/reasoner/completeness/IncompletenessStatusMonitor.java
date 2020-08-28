package org.semanticweb.elk.reasoner.completeness;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2020 Department of Computer Science, University of Oxford
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
 * An {@link IncompletenessMonitor} that keeps track of changes in
 * incompleteness status for a given reasoning task. It only reports changes to
 * the completeness of the reasoning task as warnings, i.e., if the reasoning
 * task was complete but becomes incomplete, or if the reasoning task was
 * incomplete and then becomes complete.
 * 
 * @author Yevgeny Kazakov
 *
 */
public class IncompletenessStatusMonitor
		extends DelegatingIncompletenessMonitor {

	private final String messageIncomple_, messageComplete_;
	boolean isIncompletenessDetected_ = false;

	public IncompletenessStatusMonitor(IncompletenessMonitor delegate,
			String messageIncomple, String messageComplete) {
		super(delegate);
		this.messageIncomple_ = messageIncomple;
		this.messageComplete_ = messageComplete;
	}

	@Override
	public boolean hasNewExplanation() {
		return isIncompletenessDetected_ != isIncompletenessDetected();
	}

	@Override
	public void explainIncompleteness(Logger logger) {
		if (!hasNewExplanation()) {
			return;
		}
		isIncompletenessDetected_ = isIncompletenessDetected();
		if (isIncompletenessDetected_ && messageIncomple_ != null) {
			logger.warn(messageIncomple_);
		} else {
			logger.warn(messageComplete_);
		}
		super.explainIncompleteness(logger);
	}

}
