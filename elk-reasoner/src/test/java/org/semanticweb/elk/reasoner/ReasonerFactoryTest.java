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
package org.semanticweb.elk.reasoner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.semanticweb.elk.config.ConfigurationException;
import org.semanticweb.elk.config.ConfigurationFactory;
import org.semanticweb.elk.loading.EmptyAxiomLoader;
import org.semanticweb.elk.loading.TestAxiomLoaderFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class ReasonerFactoryTest {

	@SuppressWarnings("static-method")
	@Test
	public void createReasonerDefaultConfig() {
		Reasoner reasoner = new ReasonerFactory().createReasoner(
				new TestAxiomLoaderFactory(new EmptyAxiomLoader()),
				new SimpleStageExecutor());

		assertEquals(Runtime.getRuntime().availableProcessors(),
				reasoner.getNumberOfWorkers());
	}

	@Test
	public void createReasonerCustomConfig() throws ConfigurationException,
			IOException {
		Reasoner reasoner = new ReasonerFactory().createReasoner(
				new TestAxiomLoaderFactory(new EmptyAxiomLoader()),
				FailingReasonerInterrupter.INSTANCE, new SimpleStageExecutor(),
				(ReasonerConfiguration) new ConfigurationFactory()
						.getConfiguration(getClass().getClassLoader()
								.getResourceAsStream("elk_test.properties"),
								ReasonerConfiguration.REASONER_CONFIG_PREFIX,
								ReasonerConfiguration.class));

		assertNotNull(reasoner);
		assertEquals(10, reasoner.getNumberOfWorkers());
		assertFalse(reasoner.isIncrementalMode());
	}
}
