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

import ch.qos.logback.classic.Level;

public class ElkLogPreferences {

	private static final String ELK_LOG_PREFS_KEY = "ELK_LOG_PREFS",
			ELK_LOG_LEVEL_KEY = "ELK_LOG_LEVEL",
			ELK_LOG_BUFFER_SIZE_KEY = "ELK_LOG_BUFFER_SIZE";

	public int logBufferSize;

	private int logLevel_;

	private final int defaultLogLevel_, defaultLogBufferSize_;

	public ElkLogPreferences() {
		defaultLogLevel_ = Level.DEBUG_INT;
		defaultLogBufferSize_ = 256;
	}

	public Level getLogLevel() {
		return Level.toLevel(logLevel_);
	}

	public void setLogLevel(Level logLevel) {
		this.logLevel_ = logLevel.toInt();
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(ELK_LOG_PREFS_KEY,
				ElkLogPreferences.class);
	}

	public ElkLogPreferences load() {
		Preferences prefs = getPrefs();
		logLevel_ = prefs.getInt(ELK_LOG_LEVEL_KEY, defaultLogLevel_);
		logBufferSize = prefs.getInt(ELK_LOG_BUFFER_SIZE_KEY,
				defaultLogBufferSize_);
		return this;
	}

	public ElkLogPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putInt(ELK_LOG_LEVEL_KEY, logLevel_);
		prefs.putInt(ELK_LOG_BUFFER_SIZE_KEY, logBufferSize);
		return this;
	}

	public ElkLogPreferences reset() {
		logLevel_ = defaultLogLevel_;
		logBufferSize = defaultLogBufferSize_;
		return this;
	}

}
