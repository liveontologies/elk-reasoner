/**
 * 
 */
package org.semanticweb.elk.reasoner;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.concurrent.Executors;

import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.ReasonerStageExecutor;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TestReasonerUtils {

	public static Reasoner createTestReasoner(AxiomLoader axiomLoader,
			ReasonerStageExecutor stageExecutor, ReasonerConfiguration config) {
		return new ReasonerFactory().createReasoner(axiomLoader, stageExecutor,
				Executors.newSingleThreadExecutor(), config);
	}

	public static Reasoner createTestReasoner(AxiomLoader axiomLoader,
			ReasonerStageExecutor stageExecutor) {
		return createTestReasoner(axiomLoader, stageExecutor,
				ReasonerConfiguration.getConfiguration());
	}

	public static Reasoner createTestReasoner(AxiomLoader axiomLoader,
			ReasonerStageExecutor stageExecutor, int maxWorkers) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
				String.valueOf(maxWorkers));

		return createTestReasoner(axiomLoader, stageExecutor, config);
	}
}
