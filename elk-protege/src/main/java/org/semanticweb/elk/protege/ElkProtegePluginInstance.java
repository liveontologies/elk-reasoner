/*
 * #%L
 * ELK Reasoner Protege Plug-in
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
/**
 * 
 */
package org.semanticweb.elk.protege;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.protege.editor.core.ui.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

/**
 * Carries out some initialization, e.g. Logback, which we don't want to put
 * into the reasoner factory which could be used outside Protege (for example,
 * in Snow Owl)
 * 
 * 
 * @author Pavel Klinov
 * @author Peter Skocovsky
 * 
 */
public class ElkProtegePluginInstance extends EditorKitHook {

	public static final String ELK_PACKAGE_ = "org.semanticweb.elk";

	public static final LogController ELK_LOG_CONTROLLER = new LogController();

	@Override
	public void dispose() throws Exception {
		// Empty.
	}

	@Override
	public void initialise() throws Exception {
		Logger logger = LoggerFactory.getLogger(ELK_PACKAGE_);
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
			final ElkPreferences prefs = new ElkPreferences().load();
			logbackLogger.setLevel(Level.toLevel(prefs.logLevel));
			ELK_LOG_CONTROLLER.setLogger(logbackLogger);
			ELK_LOG_CONTROLLER.setOnAppendWhenLogNotVisible(showLog_);
			ELK_LOG_CONTROLLER.setCharacterLimit(prefs.logCharacterLimit);
		}
	}

	private final Runnable showLog_ = new Runnable() {

		@Override
		public void run() {
			final EditorKit editorKit = getEditorKit();
			final Workspace workspace = editorKit.getWorkspace();
			workspace.showResultsView("org.semanticweb.elk.elk.logview", true,
					Workspace.BOTTOM_RESULTS_VIEW);
		}

	};

}
