package org.semanticweb.elk.protege;

import java.util.ArrayList;
import java.util.List;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/*-
 * #%L
 * ELK Reasoner Protege Plug-in
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class ElkPreferences {

	public static final Marker MARKER = MarkerFactory
			.getMarker("ELK-preferences");

	private static final String ELK_PREFS_KEY = "ELK_PREFS",
			NUMBER_OF_WORKERS_KEY = "ELK_NUMBER_OF_WORKERS",
			INCREMENTAL_MODE_KEY = "ELK_INCREMENTAL_MODE",
			AUTO_SYNCRHONIZATION_KEY = "ELK_AUTO_SYNCHRONIZATION",
			INLINE_INFERENCES_KEY = "ELK_INLINE_INFERENCES", SIZE = "SIZE",
			SUPPRESS_ALL_WARNINGS_KEY = "ELK_SUPPRESS_ALL_WARNIGS",
			LOG_LEVEL_KEY = "ELK_LOG_LEVEL",
			LOG_CHARACTER_LIMIT_KEY = "ELK_LOG_CHARACTER_LIMIT";

	public int numberOfWorkers;
	public boolean incrementalMode, autoSynchronization, inlineInferences,
			suppressAllWarnings;
	public List<String> suppressedWarningTypes;
	public String logLevel;
	public int logCharacterLimit;

	private final int defaultNumberOfWorkers_;
	private final boolean defaultIncrementalMode_, defaultAutoSynchronization_,
			defaultInlineInferences_, defaultSuppressAllWarnings_;
	private final List<String> defaultSuppressedWarningTypes_;
	private final String defaultLogLevel_;
	private final int defaultLogCharacterLimit_;

	public ElkPreferences() {
		ReasonerConfiguration elkDefaults = ReasonerConfiguration
				.getConfiguration();
		defaultNumberOfWorkers_ = elkDefaults.getParameterAsInt(
				ReasonerConfiguration.NUM_OF_WORKING_THREADS);
		defaultIncrementalMode_ = elkDefaults.getParameterAsBoolean(
				ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED);
		defaultAutoSynchronization_ = false;
		defaultInlineInferences_ = true;
		defaultSuppressedWarningTypes_ = new ArrayList<String>();
		defaultSuppressAllWarnings_ = false;
		defaultLogLevel_ = "WARN";
		defaultLogCharacterLimit_ = 80000;
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(ELK_PREFS_KEY,
				ElkPreferences.class);
	}

	public ElkPreferences load() {
		Preferences prefs = getPrefs();
		numberOfWorkers = prefs.getInt(NUMBER_OF_WORKERS_KEY,
				defaultNumberOfWorkers_);
		incrementalMode = prefs.getBoolean(INCREMENTAL_MODE_KEY,
				defaultIncrementalMode_);
		autoSynchronization = prefs.getBoolean(AUTO_SYNCRHONIZATION_KEY,
				defaultAutoSynchronization_);
		inlineInferences = prefs.getBoolean(INLINE_INFERENCES_KEY,
				defaultInlineInferences_);
		suppressedWarningTypes = getStringList(prefs,
				defaultSuppressedWarningTypes_);
		suppressAllWarnings = prefs.getBoolean(SUPPRESS_ALL_WARNINGS_KEY,
				defaultSuppressAllWarnings_);
		logLevel = prefs.getString(LOG_LEVEL_KEY, defaultLogLevel_);
		logCharacterLimit = prefs.getInt(LOG_CHARACTER_LIMIT_KEY,
				defaultLogCharacterLimit_);
		return this;
	}

	public ElkPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putInt(NUMBER_OF_WORKERS_KEY, numberOfWorkers);
		prefs.putBoolean(INCREMENTAL_MODE_KEY, incrementalMode);
		prefs.putBoolean(AUTO_SYNCRHONIZATION_KEY, autoSynchronization);
		prefs.putBoolean(INLINE_INFERENCES_KEY, inlineInferences);
		putStringList(prefs, suppressedWarningTypes);
		prefs.putBoolean(SUPPRESS_ALL_WARNINGS_KEY, suppressAllWarnings);
		suppressedWarningTypes = defaultSuppressedWarningTypes_;
		suppressAllWarnings = defaultSuppressAllWarnings_;
		prefs.putString(LOG_LEVEL_KEY, logLevel);
		prefs.putInt(LOG_CHARACTER_LIMIT_KEY, logCharacterLimit);
		return this;
	}

	public ElkPreferences reset() {
		numberOfWorkers = defaultNumberOfWorkers_;
		incrementalMode = defaultIncrementalMode_;
		autoSynchronization = defaultAutoSynchronization_;
		inlineInferences = defaultInlineInferences_;
		logLevel = defaultLogLevel_;
		logCharacterLimit = defaultLogCharacterLimit_;
		return this;
	}

	/**
	 * @return the {@link ReasonerConfiguration} with the settings from this ELK
	 *         preferences
	 */
	public static ReasonerConfiguration getElkConfig() {
		ReasonerConfiguration elkConfig = ReasonerConfiguration
				.getConfiguration();
		ElkPreferences elkPrefs = new ElkPreferences().load();
		elkConfig.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				String.valueOf(elkPrefs.numberOfWorkers));
		elkConfig.setParameter(ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED,
				String.valueOf(elkPrefs.incrementalMode));
		return elkConfig;
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
