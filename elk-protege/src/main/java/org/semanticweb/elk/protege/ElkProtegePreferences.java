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
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

public class ElkProtegePreferences {

	public static final String PREFERENCES_SET_KEY = "ELK_PREFS_SET";

	private static final String NUMBER_OF_WORKERS_KEY = "ELK_NUMBER_OF_WORKERS",
			INCREMENTAL_MODE_KEY = "ELK_INCREMENTAL_MODE",
			AUTO_SYNCRHONIZATION_KEY = "ELK_AUTO_SYNCHRONIZATION",
			SUPPRESSED_WARNING_TYPES_KEY = "ELK_SUPPRESSED_WARNING_TYPES";

	public int numberOfWorkers;
	public boolean incrementalMode, autoSynchronization;
	public List<String> suppressedWarningTypes;

	private int defaultNumberOfWorkers_;
	private boolean defaultIncrementalMode_, defaultAutoSynchronization_;
	private List<String> defaultSuppressedWarningTypes_;

	public ElkProtegePreferences() {
		ReasonerConfiguration elkDefaults = ReasonerConfiguration
				.getConfiguration();
		defaultNumberOfWorkers_ = elkDefaults
				.getParameterAsInt(ReasonerConfiguration.NUM_OF_WORKING_THREADS);
		defaultIncrementalMode_ = elkDefaults
				.getParameterAsBoolean(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED);
		defaultAutoSynchronization_ = false;
		defaultSuppressedWarningTypes_ = new ArrayList<String>();
	}

	private static Preferences getPreferences() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(PREFERENCES_SET_KEY,
				ElkProtegePreferences.class);
	}

	public ElkProtegePreferences load() {
		Preferences prefs = getPreferences();
		numberOfWorkers = prefs.getInt(NUMBER_OF_WORKERS_KEY,
				defaultNumberOfWorkers_);
		incrementalMode = prefs.getBoolean(INCREMENTAL_MODE_KEY,
				defaultIncrementalMode_);
		autoSynchronization = prefs.getBoolean(AUTO_SYNCRHONIZATION_KEY,
				defaultAutoSynchronization_);
		suppressedWarningTypes = getStringList(prefs,
				SUPPRESSED_WARNING_TYPES_KEY, defaultSuppressedWarningTypes_);
		return this;
	}

	public ElkProtegePreferences save() {
		Preferences prefs = getPreferences();
		prefs.putInt(NUMBER_OF_WORKERS_KEY, numberOfWorkers);
		prefs.putBoolean(INCREMENTAL_MODE_KEY, incrementalMode);
		prefs.putBoolean(AUTO_SYNCRHONIZATION_KEY, autoSynchronization);
		putStringList(prefs, SUPPRESSED_WARNING_TYPES_KEY,
				suppressedWarningTypes);
		return this;
	}

	public ElkProtegePreferences reset() {
		getPreferences().clear();
		return load();
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
	private List<String> getStringList(Preferences prefs, String key,
			List<String> defaultList) {
		int size = prefs.getInt(key, -1);
		if (size < 0)
			return defaultList;
		// else
		List<String> result = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			result.add(prefs.getString(key + "#" + i, ""));
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
	private void putStringList(Preferences prefs, String key, List<String> list) {
		int size = list.size();
		prefs.putInt(key, size);
		int i = 0;
		for (String value : list) {
			prefs.putString(key + "#" + i++, value);
		}
	}

	/**
	 * @return the {@link ReasonerConfiguration} with the settings from this
	 *         {@link ElkProtegePreferences}
	 */
	public ReasonerConfiguration getElkConfig() {
		ReasonerConfiguration elkConfig = ReasonerConfiguration
				.getConfiguration();
		elkConfig.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				String.valueOf(numberOfWorkers));
		elkConfig.setParameter(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED,
				String.valueOf(incrementalMode));
		return elkConfig;
	}

}
