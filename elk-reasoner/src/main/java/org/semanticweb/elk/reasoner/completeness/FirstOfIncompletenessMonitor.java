package org.semanticweb.elk.reasoner.completeness;

import java.util.Collection;

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
 * An {@link IncompletenessMonitor} that monitors several other
 * {@link IncompletenessMonitor}s and reports incompleteness if some of these
 * monitors report incompleteness. The status messages include information of
 * monitors until the first monitor that reports incompleteness.
 * 
 * @author Yevgeny Kazakov
 */
public class FirstOfIncompletenessMonitor implements IncompletenessMonitor {

	/**
	 * The monitors that are combined
	 */
	private final IncompletenessMonitor[] monitors_;

	FirstOfIncompletenessMonitor(IncompletenessMonitor... monitors) {
		this.monitors_ = monitors;
	}

	FirstOfIncompletenessMonitor(Collection<IncompletenessMonitor> monitors) {
		this(monitors.toArray(new IncompletenessMonitor[0]));
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
	public boolean isStatusChanged() {
		for (IncompletenessMonitor monitor : monitors_) {
			if (monitor.isStatusChanged()) {
				return true;
			}
			if (monitor.isIncompletenessDetected()) {
				return false;
			}
		}
		return false;
	}

	@Override
	public void logStatus(Logger logger) {
		for (IncompletenessMonitor monitor : monitors_) {
			monitor.logStatus(logger);
			if (monitor.isIncompletenessDetected()) {
				return;
			}
		}
	}

}
