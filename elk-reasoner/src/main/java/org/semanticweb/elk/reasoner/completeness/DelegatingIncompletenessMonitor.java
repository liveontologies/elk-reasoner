/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.completeness;

import java.util.Arrays;

/**
 * Delegates the incompleteness check to the provided sub-monitors. Calls all
 * the sub-monitors and reports incompleteness if at least one of them reports
 * incompleteness.
 * 
 * @author Peter Skocovsky
 */
class DelegatingIncompletenessMonitor implements IncompletenessMonitor {

	private final Iterable<? extends IncompletenessMonitor> incompletenessMonitors_;

	public DelegatingIncompletenessMonitor(
			final Iterable<? extends IncompletenessMonitor> incompletenessMonitors) {
		this.incompletenessMonitors_ = incompletenessMonitors;
	}

	public DelegatingIncompletenessMonitor(
			final IncompletenessMonitor... incompletenessMonitors) {
		this(Arrays.asList(incompletenessMonitors));
	}

	@Override
	public boolean isIncomplete() {
		boolean result = false;
		for (final IncompletenessMonitor incompletenessMonitor : incompletenessMonitors_) {
			result = incompletenessMonitor.isIncomplete() || result;
		}
		return result;
	}

}
