package org.semanticweb.elk.protege.preferences;

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

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

public class ElkWarningPreferences {

	private static final String ELK_WARNING_PREFS_KEY = "ELK_WARNING_PREFS",
			SIZE = "SIZE",
			SUPPRESS_ALL_WARNINGS_KEY = "ELK_SUPPRESS_ALL_WARNIGS";

	public List<String> suppressedWarningTypes;
	public boolean suppressAllWarnings;

	private final List<String> defaultSuppressedWarningTypes_;
	private final boolean defaultIgnoreAllWarnings_;

	public ElkWarningPreferences() {
		defaultSuppressedWarningTypes_ = new ArrayList<String>();
		defaultIgnoreAllWarnings_ = false;
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(ELK_WARNING_PREFS_KEY,
				ElkWarningPreferences.class);
	}

	public ElkWarningPreferences load() {
		Preferences prefs = getPrefs();
		suppressedWarningTypes = getStringList(prefs,
				defaultSuppressedWarningTypes_);
		suppressAllWarnings = prefs.getBoolean(SUPPRESS_ALL_WARNINGS_KEY,
				defaultIgnoreAllWarnings_);
		return this;
	}

	public ElkWarningPreferences save() {
		Preferences prefs = getPrefs();
		putStringList(prefs, suppressedWarningTypes);
		prefs.putBoolean(SUPPRESS_ALL_WARNINGS_KEY, suppressAllWarnings);
		return this;
	}

	public ElkWarningPreferences reset() {
		suppressedWarningTypes = defaultSuppressedWarningTypes_;
		suppressAllWarnings = defaultIgnoreAllWarnings_;
		return this;
	}

	/**
	 * A replacement for {@link Preferences#getStringList(String, List)}, which
	 * does not work in combination with {@link Preferences#clear()}
	 * 
	 * @param prefs
	 * @param key
	 * @param defaultList
	 * @return
	 */
	private List<String> getStringList(Preferences prefs,
			List<String> defaultList) {
		int size = prefs.getInt(SIZE, -1);
		if (size < 0)
			return defaultList;
		// else
		List<String> result = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			result.add(prefs.getString(Integer.toString(i), ""));
		}
		return result;
	}

	/**
	 * A replacement for {@link Preferences#putStringList(String, List)}, which
	 * does not work in combination with {@link Preferences#clear()}
	 * 
	 * @param prefs
	 * @param key
	 * @param list
	 */
	private void putStringList(Preferences prefs, List<String> list) {
		int size = list.size();
		prefs.putInt(SIZE, size);
		int i = 0;
		for (String value : list) {
			prefs.putString(Integer.toString(i++), value);
		}
	}

}
