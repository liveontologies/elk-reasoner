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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.reasoner.RandomReasonerInterrupter;
import org.semanticweb.elk.reasoner.incremental.IncrementalChangeType;
import org.semanticweb.elk.reasoner.incremental.IncrementalReasoningTestWithInterruptsDelegate;
import org.semanticweb.elk.testing.TestManifest;
import org.semanticweb.elk.testing.TestOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.slf4j.Logger;

public abstract class OwlApiIncrementalReasoningTestDelegate<EO extends TestOutput, AO extends TestOutput>
		implements IncrementalReasoningTestWithInterruptsDelegate<OWLAxiom, EO, AO> {

	public static final double DEFAULT_INTERRUPTION_CHANCE = 0.1;

	@SuppressWarnings("unchecked")
	private static final Set<AxiomType<?>> DYNAMIC_AXIOM_TYPES = new HashSet<AxiomType<?>>(
			Arrays.asList(AxiomType.SUBCLASS_OF, AxiomType.EQUIVALENT_CLASSES,
					AxiomType.DISJOINT_CLASSES));

	private final TestManifest<? extends UrlTestInput> manifest_;
	private final double interruptionChance_;
	
	private OWLOntology testOntology_;
	private ElkReasoner standardReasoner_;
	private ElkReasoner incrementalReasoner_;

	public OwlApiIncrementalReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest,
			final double interruptionChance) {
		this.manifest_ = manifest;
		this.interruptionChance_ = interruptionChance;
	}

	public OwlApiIncrementalReasoningTestDelegate(
			final TestManifest<? extends UrlTestInput> manifest) {
		this(manifest, DEFAULT_INTERRUPTION_CHANCE);
	}
	
	public TestManifest<? extends UrlTestInput> getManifest() {
		return manifest_;
	}
	
	public ElkReasoner getStandardReasoner() {
		return standardReasoner_;
	}
	
	public ElkReasoner getIncrementalReasoner() {
		return incrementalReasoner_;
	}

	@Override
	public Collection<OWLAxiom> load() throws Exception {

		final ArrayList<OWLAxiom> changingAxioms = new ArrayList<OWLAxiom>();

		final OWLOntologyManager manager = TestOWLManager
				.createOWLOntologyManager();

		final InputStream stream = manifest_.getInput().getUrl().openStream();
		testOntology_ = manager.loadOntologyFromOntologyDocument(stream);

		for (OWLAxiom axiom : testOntology_.getLogicalAxioms()) {
			if (DYNAMIC_AXIOM_TYPES.contains(axiom.getAxiomType())) {
				changingAxioms.add(axiom);
			}
		}

		return changingAxioms;
	}

	@Override
	public void initIncremental() throws Exception {

		// important to use the buffering mode here
		// otherwise we'd need to issue a query to the ElkReasoner
		// before we could use the internal reasoner directly in the tests
		standardReasoner_ = OWLAPITestUtils.createReasoner(testOntology_, true);
		standardReasoner_.getInternalReasoner().setAllowIncrementalMode(false);
		incrementalReasoner_ = OWLAPITestUtils.createReasoner(testOntology_,
				true);
		incrementalReasoner_.getInternalReasoner()
				.setAllowIncrementalMode(true);

	}

	@Override
	public void initWithInterrupts() throws Exception {

		/*
		 * important to use the buffering mode here otherwise we'd need to issue
		 * a query to the ElkReasoner before we could use the internal reasoner
		 * directly in the tests
		 * 
		 */
		standardReasoner_ = OWLAPITestUtils.createReasoner(testOntology_, true);
		standardReasoner_.getInternalReasoner().setAllowIncrementalMode(false);
		final Random random = new Random(RandomSeedProvider.VALUE);
		incrementalReasoner_ = OWLAPITestUtils.createReasoner(testOntology_,
				false,
				new RandomReasonerInterrupter(random, interruptionChance_));
		incrementalReasoner_.getInternalReasoner()
				.setAllowIncrementalMode(true);

	}

	@Override
	public void applyChanges(final Iterable<OWLAxiom> changes,
			final IncrementalChangeType type) {
		// the changes are applied indirectly by modifying the ontology
		final OWLOntologyManager manager = testOntology_
				.getOWLOntologyManager();

		for (OWLAxiom axiom : changes) {
			switch (type) {
			case ADD:
				manager.addAxiom(testOntology_, axiom);
				break;
			case DELETE:
				manager.removeAxiom(testOntology_, axiom);
				break;
			}
		}
		
		standardReasoner_.flush();
		incrementalReasoner_.flush();
	}

	@Override
	public void dumpChangeToLog(final OWLAxiom change, final Logger logger,
			final LogLevel level) {
		LoggerWrap.log(logger, level, change.toString());
	}

	@Override
	public void before() throws Exception {
		// Empty.
	}

	@Override
	public void after() {
		standardReasoner_.dispose();
		incrementalReasoner_.dispose();
	}

}
