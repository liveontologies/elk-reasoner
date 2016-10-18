/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.managers.ElkObjectEntityRecyclingFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.ReasonerInterrupter;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main factory to instantiate {@link Reasoner}
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author Yevgeny Kazakov
 * @author Peter Skocovsky
 */
public class ReasonerFactory {

	final static Logger LOGGER_ = LoggerFactory
			.getLogger(ReasonerFactory.class);

	final static ReasonerStageExecutor DEFAULT_STAGE_EXECUTOR = new SimpleStageExecutor();

	final static ReasonerInterrupter DEFAULT_INTERRUPTER = new ReasonerInterrupter();

	public Reasoner createReasoner(AxiomLoader.Factory axiomLoaderFactory) {
		return createReasoner(new ElkObjectEntityRecyclingFactory(),
				axiomLoaderFactory, DEFAULT_INTERRUPTER, DEFAULT_STAGE_EXECUTOR,
				ReasonerConfiguration.getConfiguration());
	}

	public Reasoner createReasoner(AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerInterrupter interrupter,
			ReasonerStageExecutor stageExecutor) {
		return createReasoner(axiomLoaderFactory, interrupter, stageExecutor,
				ReasonerConfiguration.getConfiguration());
	}

	public Reasoner createReasoner(AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerInterrupter interrupter,
			ReasonerStageExecutor stageExecutor, ReasonerConfiguration config) {
		return createReasoner(new ElkObjectEntityRecyclingFactory(),
				axiomLoaderFactory, interrupter, stageExecutor, config);
	}

	@SuppressWarnings("static-method")
	public Reasoner createReasoner(ElkObject.Factory elkFactory,
			AxiomLoader.Factory axiomLoaderFactory,
			final ReasonerInterrupter interrupter,
			ReasonerStageExecutor stageExecutor, ReasonerConfiguration config) {
		Reasoner reasoner = new Reasoner(elkFactory, axiomLoaderFactory,
				interrupter, stageExecutor, config);

		return reasoner;
	}

}
