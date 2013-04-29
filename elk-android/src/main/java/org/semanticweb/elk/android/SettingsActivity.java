package org.semanticweb.elk.android;

/*
 * #%L
 * ELK Android App
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsActivity extends Activity {

	public static final String KEY_PREF_LOG_LEVEL = "pref_logLevel";

	public static final String KEY_PREF_WORKERS = "pref_workers";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	/**
	 * @param prefs
	 * @return the log level set in shared preferences
	 */
	public static Level getLogLevel(Context c, SharedPreferences prefs) {
		String logLevel = prefs.getString(SettingsActivity.KEY_PREF_LOG_LEVEL,
				"");
		if (logLevel.equals(c.getString(R.string.pref_OFF)))
			return Level.OFF;
		if (logLevel.equals(c.getString(R.string.pref_FATAL)))
			return Level.FATAL;
		if (logLevel.equals(c.getString(R.string.pref_ERROR)))
			return Level.ERROR;
		if (logLevel.equals(c.getString(R.string.pref_WARN)))
			return Level.WARN;
		if (logLevel.equals(c.getString(R.string.pref_INFO)))
			return Level.INFO;
		if (logLevel.equals(c.getString(R.string.pref_DEBUG)))
			return Level.DEBUG;
		if (logLevel.equals(c.getString(R.string.pref_TRACE)))
			return Level.TRACE;
		if (logLevel.equals(c.getString(R.string.pref_ALL)))
			return Level.ALL;
		// default value
		return Level.DEBUG;
	}

	public static class SettingsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preferences);
			SharedPreferences prefs = getPreferenceScreen()
					.getSharedPreferences();
			// register a listener that updates the summary
			prefs.registerOnSharedPreferenceChangeListener(this);
			// update summary of ListPreference
			updateSummaryListPrefs(prefs, KEY_PREF_LOG_LEVEL);
			updateSummaryListPrefs(prefs, KEY_PREF_WORKERS);
		}

		@Override
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			if (key.equals(KEY_PREF_LOG_LEVEL)) {
				// Set summary to be the user-description for the selected value
				updateSummaryListPrefs(sharedPreferences, KEY_PREF_LOG_LEVEL);
				// change log level
				Logger.getRootLogger().setLevel(
						getLogLevel(getActivity(), sharedPreferences));
			} else if (key.equals(KEY_PREF_WORKERS)) {
				// Set summary to be the user-description for the selected value
				updateSummaryListPrefs(sharedPreferences, KEY_PREF_WORKERS);
			}
		}

		void updateSummaryListPrefs(SharedPreferences sharedPreferences,
				String key) {
			ListPreference listPref = (ListPreference) findPreference(key);
			String level = sharedPreferences.getString(key, "");
			listPref.setSummary(level);
		}

	}

}
