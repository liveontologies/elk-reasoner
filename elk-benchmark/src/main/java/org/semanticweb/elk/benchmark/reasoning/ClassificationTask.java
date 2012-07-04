/*
 * #%L
 * ELK Bencharking Package
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
package org.semanticweb.elk.benchmark.reasoning;

import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.cli.IOReasoner;
import org.semanticweb.elk.cli.IOReasonerFactory;
import org.semanticweb.elk.reasoner.InconsistentOntologyException;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;

/**
 * A task to classify an ontology
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class ClassificationTask implements Task {

	private IOReasoner reasoner;
	
	@Override
	public String getName() {
		return "EL classification";
	}

	@Override
	public void prepare(String... args) throws TaskException {
		try {
			ReasonerConfiguration config = getConfig(args);
			
			reasoner = new IOReasonerFactory().createReasoner(new LoggingStageExecutor(), config);
			reasoner.loadOntologyFromFile(args[0]);
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	private ReasonerConfiguration getConfig(String[] args) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();
		
		if (args.length > 1) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS, args[1]);
		}
		
		return config;
	}

	@Override
	public Result run() throws TaskException {
		try {
			reasoner.getTaxonomy();
		} catch (InconsistentOntologyException e) {
			throw new TaskException(e);
		}
		
		return null;
	}

}
