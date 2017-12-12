package org.semanticweb.elk.protege;

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
			INLINE_INFERENCES_KEY = "ELK_INLINE_INFERENCES";

	public int numberOfWorkers;
	public boolean incrementalMode, autoSynchronization, inlineInferences;

	private final int defaultNumberOfWorkers_;
	private final boolean defaultIncrementalMode_, defaultAutoSynchronization_,
			defaultInlineInferences_;

	public ElkPreferences() {
		ReasonerConfiguration elkDefaults = ReasonerConfiguration
				.getConfiguration();
		defaultNumberOfWorkers_ = elkDefaults.getParameterAsInt(
				ReasonerConfiguration.NUM_OF_WORKING_THREADS);
		defaultIncrementalMode_ = elkDefaults.getParameterAsBoolean(
				ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED);
		defaultAutoSynchronization_ = false;
		defaultInlineInferences_ = true;
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
		return this;
	}

	public ElkPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putInt(NUMBER_OF_WORKERS_KEY, numberOfWorkers);
		prefs.putBoolean(INCREMENTAL_MODE_KEY, incrementalMode);
		prefs.putBoolean(AUTO_SYNCRHONIZATION_KEY, autoSynchronization);
		prefs.putBoolean(INLINE_INFERENCES_KEY, inlineInferences);
		return this;
	}

	public ElkPreferences reset() {
		numberOfWorkers = defaultNumberOfWorkers_;
		incrementalMode = defaultIncrementalMode_;
		autoSynchronization = defaultAutoSynchronization_;
		inlineInferences = defaultInlineInferences_;
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

}
