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
 * {@link IncompletenessMonitor}s. and reports incompleteness if some of these
 * monitors report incompleteness. The status messages include information about
 * a bounded number of these monitors.
 * 
 * @author Yevgeny Kazakov
 */
public class SomeOfIncompletenessMonitor implements IncompletenessMonitor {

	/**
	 * the default maximal number of monitors for which to print the
	 * explanations
	 */
	private final static int DEFAULT_EXPLANATION_LIMIT_ = 5;

	/**
	 * the maximal number of monitors for which to print the explanations
	 */
	private final int explanationLimit_;

	/**
	 * The monitors that are combined
	 */
	private final IncompletenessMonitor[] monitors_;

	/**
	 * The number of monitors in which incompleteness was detected
	 */
	private int countIncompletenessProblems_ = 0;

	SomeOfIncompletenessMonitor(int explanationLimit,
			IncompletenessMonitor... monitors) {
		this.explanationLimit_ = explanationLimit;
		this.monitors_ = monitors;
	}

	SomeOfIncompletenessMonitor(int explanationLimit,
			Collection<IncompletenessMonitor> monitors) {
		this(explanationLimit, monitors.toArray(new IncompletenessMonitor[0]));
	}

	SomeOfIncompletenessMonitor(IncompletenessMonitor... monitors) {
		this(DEFAULT_EXPLANATION_LIMIT_, monitors);
	}

	SomeOfIncompletenessMonitor(Collection<IncompletenessMonitor> monitors) {
		this(DEFAULT_EXPLANATION_LIMIT_, monitors);
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
	public boolean isStatusChanged(Logger logger) {
		if (!logger.isInfoEnabled()) {
			return false;
		}
		int countIncompletenessProblems = 0;
		for (IncompletenessMonitor monitor : monitors_) {
			if (!monitor.isIncompletenessDetected()
					|| countIncompletenessProblems++ < explanationLimit_) {
				if (monitor.isStatusChanged(logger)) {
					return true;
				}
			}
		}
		return countIncompletenessProblems != countIncompletenessProblems_;
	}

	@Override
	public void logStatus(Logger logger) {
		if (!logger.isInfoEnabled()) {
			return;
		}
		int countIncompletenessProblems = 0;
		for (IncompletenessMonitor monitor : monitors_) {
			if (!monitor.isIncompletenessDetected()
					|| countIncompletenessProblems++ < explanationLimit_) {
				monitor.logStatus(logger);
			}
		}
		if (countIncompletenessProblems == countIncompletenessProblems_) {
			return;
		}
		countIncompletenessProblems_ = countIncompletenessProblems;
		String different = countIncompletenessProblems == 1 ? "" : " different";
		String types = countIncompletenessProblems == 1 ? "type" : "types";
		logger.info("... {}{} incompleteness problem {} detected",
				countIncompletenessProblems, different, types);
	}

}
