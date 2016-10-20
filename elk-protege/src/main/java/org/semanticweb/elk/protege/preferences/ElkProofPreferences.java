package org.semanticweb.elk.protege.preferences;

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

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;

public class ElkProofPreferences {

	private static final String ELK_PROOF_PREFS_KEY = "ELK_PROOF_PREFS",
			FLATTEN_INFERENCES_KEY = "ELK_FLATTEN_INFERENCES";

	public boolean flattenInferences;

	private final boolean defaultFlattenInferences_;

	public ElkProofPreferences() {
		ReasonerConfiguration elkDefaults = ReasonerConfiguration
				.getConfiguration();
		defaultFlattenInferences_ = elkDefaults.getParameterAsBoolean(
				ReasonerConfiguration.FLATTEN_INFERENCES);
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(ELK_PROOF_PREFS_KEY,
				ElkProofPreferences.class);
	}

	public ElkProofPreferences load() {
		Preferences prefs = getPrefs();
		flattenInferences = prefs.getBoolean(FLATTEN_INFERENCES_KEY,
				defaultFlattenInferences_);
		return this;
	}

	public ElkProofPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putBoolean(FLATTEN_INFERENCES_KEY, flattenInferences);
		return this;
	}

	public ElkProofPreferences reset() {
		flattenInferences = defaultFlattenInferences_;
		return this;
	}

}
