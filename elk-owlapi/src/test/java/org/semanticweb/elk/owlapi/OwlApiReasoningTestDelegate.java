/*
 * #%L
 * ELK OWL API Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owlapi;

import java.io.InputStream;
import java.util.Random;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.reasoner.RandomReasonerInterrupter;
import org.semanticweb.elk.reasoner.ReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public abstract class OwlApiReasoningTestDelegate<AO extends TestOutput>
		implements ReasoningTestWithInterruptsDelegate<AO> {

	public static final double INTERRUPTION_CHANCE = 0.3;

	protected final TestManifest<? extends UrlTestInput> manifest_;

	protected ElkReasoner reasoner_;

	public OwlApiReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this.manifest_ = manifest;
	}

	@Override
	public void initWithOutput() throws Exception {
		final InputStream input = manifest_.getInput().getUrl().openStream();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

		reasoner_ = OWLAPITestUtils.createReasoner(ontology);
	}

	@Override
	public void initWithInterrupts() throws Exception {
		final InputStream input = manifest_.getInput().getUrl().openStream();
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(input);

		final Random random = new Random(RandomSeedProvider.VALUE);
		reasoner_ = OWLAPITestUtils.createReasoner(ontology, false,
				new RandomReasonerInterrupter(random, INTERRUPTION_CHANCE),
				new SimpleStageExecutor());
	}

	@Override
	public void before() throws Exception {
		// Empty.
	}

	@Override
	public void after() {
		// Empty.
	}

}
