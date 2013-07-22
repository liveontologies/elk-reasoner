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

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.util.logging.CachedTimeThread;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	// private static final Logger logger_ =
	// Logger.getLogger(MainActivity.class);

	int ontologyId = 0;
	OntologyWrap[] ontologies;

	Reasoner reasoner;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		Field[] rawOntologies = R.raw.class.getFields();
		ontologies = new OntologyWrap[rawOntologies.length];
		for (int i = 0; i < rawOntologies.length; i++) {
			Field rawOntology = rawOntologies[i];
			if (rawOntology.getName().startsWith("ont"))
				ontologies[i] = new OntologyWrap(rawOntology);
		}

		// load default preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		// creating ontology list
		Spinner spinner = (Spinner) findViewById(R.id.ontology_spinner);
		ArrayAdapter<OntologyWrap> adapter = new ArrayAdapter<OntologyWrap>(
				this, android.R.layout.simple_spinner_item, ontologies);

		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SpinnerActivity());

		// creating log screen
		TextView tv = (TextView) findViewById(R.id.logText);
		tv.setMovementMethod(new ScrollingMovementMethod());
		Logger.getRootLogger().addAppender(new TextViewAppender(tv));

		// set default log level from preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Logger.getRootLogger().setLevel(
				SettingsActivity.getLogLevel(this, prefs));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivityForResult(i, 0);
			break;
		}

		return true;
	}

	/** Called when the user clicks the run button */
	public void run(View view) {
		deactivateOntologySelection();
		if (reasoner == null)
			initReasoner();

		final Thread reasonerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					reasoner.getTaxonomyQuietly();
				} catch (ElkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						reasoner.shutdown();
						reasoner = null;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Spinner spinner = (Spinner) findViewById(R.id.ontology_spinner);
					spinner.post(new Runnable() {
						@Override
						public void run() {
							activateOntologySelection();
						}
					});
				}
			}
		});

		reasonerThread.start();

	}

	/** Called when the user clicks the stop button */
	public void stop(View view) {
		if (reasoner != null)
			reasoner.interrupt();
	}

	private void initReasoner() {
		// create the reasoner
		ReasonerFactory reasoningFactory = new ReasonerFactory();
		ReasonerConfiguration configuration = ReasonerConfiguration
				.getConfiguration();
		Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
		AxiomLoader loader = new Owl2StreamLoader(parserFactory, this
				.getResources().openRawResource(ontologyId));
		reasoner = reasoningFactory.createReasoner(loader,
				new LoggingStageExecutor(), configuration);
		reasoner.setProgressMonitor(new ProgressIndicator());
		// set the parameters from preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int workers = Integer.valueOf(prefs.getString(
				SettingsActivity.KEY_PREF_WORKERS, "1"));
		reasoner.setNumberOfWorkers(workers);
	}

	private void deactivateOntologySelection() {
		Spinner spinner = (Spinner) findViewById(R.id.ontology_spinner);
		spinner.setEnabled(false);
		Button run = (Button) findViewById(R.id.run);
		Button stop = (Button) findViewById(R.id.stop);
		run.setVisibility(View.GONE);
		stop.setVisibility(View.VISIBLE);
	}

	private void activateOntologySelection() {
		Spinner spinner = (Spinner) findViewById(R.id.ontology_spinner);
		spinner.setEnabled(true);
		Button run = (Button) findViewById(R.id.run);
		Button stop = (Button) findViewById(R.id.stop);
		stop.setVisibility(View.GONE);
		run.setVisibility(View.VISIBLE);
	}

	public static class OntologyWrap {
		private final Field ontology_;

		public OntologyWrap(Field ontology) {
			this.ontology_ = ontology;
		}

		@Override
		public String toString() {
			String fullName = ontology_.getName();
			return fullName.substring(fullName.indexOf("_") + 1) + ".owl";
		}

		public int getResourceId() {
			try {
				return ontology_.getInt(ontology_);
			} catch (IllegalArgumentException e) {
				return 0;
			} catch (IllegalAccessException e) {
				return 0;
			}
		}
	}

	public class SpinnerActivity extends Activity implements
			OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			ontologyId = ontologies[pos].getResourceId();
			if (reasoner != null) {
				try {
					reasoner.shutdown();
				} catch (InterruptedException e) {
				}
				reasoner = null;
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// nothing changes
		}
	}

	public class ProgressIndicator implements ProgressMonitor {
		private final ProgressBar mProgress_ = (ProgressBar) MainActivity.this
				.findViewById(R.id.reasonerProgress);
		private static final int UPDATE_INTERVAL = 1; // in ms
		private long lastUpdate_ = 0;
		private int currentMaxState_ = 0;

		@Override
		public void finish() {
			mProgress_.setProgress(currentMaxState_);
		}

		@Override
		public void report(final int state, final int maxState) {
			long time = CachedTimeThread.currentTimeMillis;
			if (time < lastUpdate_ + UPDATE_INTERVAL)
				return;
			lastUpdate_ = time;
			if (maxState != currentMaxState_) {
				currentMaxState_ = maxState;
				mProgress_.setMax(maxState);
			}
			mProgress_.setProgress(state);
		}

		@Override
		public void start(String message) {
			mProgress_.setProgress(0);

		}
	}

}
