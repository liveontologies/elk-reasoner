/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
 * @author Yevgeny Kazakov, Jun 28, 2011
 */
package org.semanticweb.elk.protege;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.inference.AbstractProtegeOWLReasonerInfo;
import org.semanticweb.elk.owlapi.ElkReasonerConfiguration;
import org.semanticweb.elk.owlapi.ElkReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.util.logging.MessageDialogAppender;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

/**
 * Entry point for Protege reasoner plugin. As part of the initialisation, a
 * Log4J appender is registered to create message dialogs for warnings/errors
 * that would otherwise go unnoticed.
 * 
 * @author Yevgeny Kazakov
 * @author Markus Kroetzsch
 */
public class ProtegeReasonerFactory extends AbstractProtegeOWLReasonerInfo {

	protected final OWLReasonerFactory factory = new ElkReasonerFactory();
	protected final ReasonerConfiguration elkConfig = ReasonerConfiguration
			.getConfiguration();

	protected final MessageDialogAppender messageDialogAppender = new MessageDialogAppender();

	@Override
	public BufferingMode getRecommendedBuffering() {
		return BufferingMode.BUFFERING;
	}

	@Override
	public OWLReasonerFactory getReasonerFactory() {
		return factory;
	}

	@Override
	public ElkReasonerConfiguration getConfiguration(
			ReasonerProgressMonitor monitor) {
		OWLReasonerConfiguration defaultOwlConfig = ElkReasonerConfiguration
				.getDefaultOwlReasonerConfiguration(monitor);
		return new ElkReasonerConfiguration(defaultOwlConfig, elkConfig);
	}

	@Override
	public void initialise() throws Exception {
		// No matter how often this is called, Log4J will register this object
		// only once:
		Logger.getLogger("org.semanticweb.elk").addAppender(
				messageDialogAppender);
	}

	@Override
	public void dispose() throws Exception {
	}

	public ReasonerConfiguration getElkConfiguration() {
		return elkConfig;
	}
}