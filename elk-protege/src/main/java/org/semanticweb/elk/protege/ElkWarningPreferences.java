package org.semanticweb.elk.protege;

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

	private static final String SUPPRESSED_WARNING_TYPES_KEY = "ELK_SUPPRESSED_WARNING_TYPES",
			SIZE = "SIZE";

	public List<String> suppressedWarningTypes;

	private List<String> defaultSuppressedWarningTypes_;

	public ElkWarningPreferences() {
		defaultSuppressedWarningTypes_ = new ArrayList<String>();
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(SUPPRESSED_WARNING_TYPES_KEY,
				ElkWarningPreferences.class);
	}

	public ElkWarningPreferences load() {
		Preferences prefs = getPrefs();
		suppressedWarningTypes = getStringList(prefs,
				defaultSuppressedWarningTypes_);
		return this;
	}

	public ElkWarningPreferences save() {
		Preferences prefs = getPrefs();
		putStringList(prefs, suppressedWarningTypes);
		return this;
	}

	public ElkWarningPreferences reset() {
		getPrefs().clear();
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
