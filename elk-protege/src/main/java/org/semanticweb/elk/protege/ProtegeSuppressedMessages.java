/*
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.protege;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages ELK warning messages that are suppressed, i.e., should not be
 * presented to the user
 * 
 * @author "Yevgeny Kazakov"
 */
public class ProtegeSuppressedMessages {

	private static final ProtegeSuppressedMessages INSTANCE_ = new ProtegeSuppressedMessages();

	private final Map<String, Integer> suppressedWarningCounts_;
	private boolean suppressAllWarnings_;

	public ProtegeSuppressedMessages() {
		suppressedWarningCounts_ = new HashMap<String, Integer>();
	}

	public static ProtegeSuppressedMessages getInstance() {
		return INSTANCE_;
	}

	/**
	 * Loads the suppressed message types from the preferences, keeping the
	 * counters if there were any
	 * 
	 * @return the loaded messages
	 */
	public ProtegeSuppressedMessages reload() {
		ElkPreferences prefs = new ElkPreferences().load();
		suppressedWarningCounts_.keySet().retainAll(
				prefs.suppressedWarningTypes);
		for (String warningType : prefs.suppressedWarningTypes) {
			if (suppressedWarningCounts_.get(warningType) == null)
				suppressedWarningCounts_.put(warningType, 0);
		}
		suppressAllWarnings_ = prefs.suppressAllWarnings;
		return this;
	}

	public void addWarningType(String newWarningType) {
		suppressedWarningCounts_.put(newWarningType, 0);
		ElkPreferences prefs = new ElkPreferences().load();
		prefs.suppressedWarningTypes.add(newWarningType);
		prefs.save();
	}

	/**
	 * 
	 * @param warningType
	 * @return {@code true} if the warningType is suppressed and increments the
	 *         counter for this warningType, or returns {@code false} otherwise
	 */
	public boolean checkSuppressed(String warningType) {
		Integer count = suppressedWarningCounts_.get(warningType);
		if (count == null) {
			if (suppressAllWarnings_) {
				count = 0;
				addWarningType(warningType);
			} else
				return false;
		}
		suppressedWarningCounts_.put(warningType, count + 1);
		return true;
	}

	/**
	 * @param warningType
	 * @return the current value of the counter for the given warningType
	 */
	public int getCount(String warningType) {
		return suppressedWarningCounts_.get(warningType);
	}

}
