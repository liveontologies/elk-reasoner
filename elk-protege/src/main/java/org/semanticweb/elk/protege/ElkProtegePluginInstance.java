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

import org.protege.editor.core.editorkit.plugin.EditorKitHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;

/**
 * Carries out some initialization, e.g. Logback, which we don't want to put into
 * the reasoner factory which could be used outside Protege (for example, in
 * Snow Owl)
 * 
 * 
 * @author Pavel Klinov
 * @author Peter Skocovsky
 * 
 */
public class ElkProtegePluginInstance extends EditorKitHook {

	private static final String ELK_PACKAGE_ = "org.semanticweb.elk";

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public void initialise() throws Exception {
		Logger logger = LoggerFactory.getLogger(ELK_PACKAGE_);
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger logbackLogger = (ch.qos.logback.classic.Logger) logger;
			ProtegeMessageAppender appender = ProtegeMessageAppender.getInstance();
			LoggerContext context = logbackLogger.getLoggerContext();
			appender.setContext(context);
			logbackLogger.addAppender(appender);
			ThresholdFilter filter = new ThresholdFilter();
			filter.setLevel(Level.WARN.levelStr);
			filter.start();
			appender.addFilter(filter);
			appender.start();
		}
	}
}
