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

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

public class ElkGeneralPreferences {

	private static final String ELK_GENERAL_PREFS_KEY = "ELK_GENERAL_PREFS",
			NUMBER_OF_WORKERS_KEY = "ELK_NUMBER_OF_WORKERS",
			INCREMENTAL_MODE_KEY = "ELK_INCREMENTAL_MODE",
			AUTO_SYNCRHONIZATION_KEY = "ELK_AUTO_SYNCHRONIZATION";

	public int numberOfWorkers;
	public boolean incrementalMode, autoSynchronization;

	private final int defaultNumberOfWorkers_;
	private final boolean defaultIncrementalMode_, defaultAutoSynchronization_;

	public ElkGeneralPreferences() {
		ReasonerConfiguration elkDefaults = ReasonerConfiguration
				.getConfiguration();
		defaultNumberOfWorkers_ = elkDefaults.getParameterAsInt(
				ReasonerConfiguration.NUM_OF_WORKING_THREADS);
		defaultIncrementalMode_ = elkDefaults.getParameterAsBoolean(
				ReasonerConfiguration.INCREMENTAL_MODE_ALLOWED);
		defaultAutoSynchronization_ = false;
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(ELK_GENERAL_PREFS_KEY,
				ElkGeneralPreferences.class);
	}

	public ElkGeneralPreferences load() {
		Preferences prefs = getPrefs();
		numberOfWorkers = prefs.getInt(NUMBER_OF_WORKERS_KEY,
				defaultNumberOfWorkers_);
		incrementalMode = prefs.getBoolean(INCREMENTAL_MODE_KEY,
				defaultIncrementalMode_);
		autoSynchronization = prefs.getBoolean(AUTO_SYNCRHONIZATION_KEY,
				defaultAutoSynchronization_);
		return this;
	}

	public ElkGeneralPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putInt(NUMBER_OF_WORKERS_KEY, numberOfWorkers);
		prefs.putBoolean(INCREMENTAL_MODE_KEY, incrementalMode);
		prefs.putBoolean(AUTO_SYNCRHONIZATION_KEY, autoSynchronization);
		return this;
	}

	public ElkGeneralPreferences reset() {
		numberOfWorkers = defaultNumberOfWorkers_;
		incrementalMode = defaultIncrementalMode_;
		autoSynchronization = defaultAutoSynchronization_;
		return this;
	}

}
