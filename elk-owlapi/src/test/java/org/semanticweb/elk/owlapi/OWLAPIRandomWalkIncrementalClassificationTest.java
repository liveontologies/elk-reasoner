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
package org.semanticweb.elk.owlapi;

import static org.junit.Assume.assumeTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasoningTestManifest;
import org.semanticweb.elk.reasoner.TaxonomyDiffManifest;
import org.semanticweb.elk.reasoner.TaxonomyTestOutput;
import org.semanticweb.elk.reasoner.incremental.IncrementalChange;
import org.semanticweb.elk.reasoner.incremental.IncrementalClassificationCorrectnessTest;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;
import org.semanticweb.elk.reasoner.incremental.RandomWalkIncrementalClassificationRunner;
import org.semanticweb.elk.reasoner.incremental.RandomWalkRunnerIO;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.testing.ConfigurationUtils;
import org.semanticweb.elk.testing.ConfigurationUtils.TestManifestCreator;
import org.semanticweb.elk.testing.PolySuite;
import org.semanticweb.elk.testing.PolySuite.Config;
import org.semanticweb.elk.testing.PolySuite.Configuration;
import org.semanticweb.elk.testing.TestInput;
import org.semanticweb.elk.testing.TestManifestWithOutput;
import org.semanticweb.elk.testing.UrlTestInput;
import org.semanticweb.elk.util.logging.LogLevel;
import org.semanticweb.elk.util.logging.LoggerWrap;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.parameters.OntologyCopy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
@RunWith(PolySuite.class)
public class OWLAPIRandomWalkIncrementalClassificationTest {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(OWLAPIRandomWalkIncrementalClassificationTest.class);

	final static String INPUT_DATA_LOCATION = "classification_test_input";

	/**
	 * the maximum number of rounds used
	 */
	static int MAX_ROUNDS = 5;
	/**
	 * how many changes are generated in every round
	 */
	static int ITERATIONS = 5;
	
	private final static AxiomFilter axiomFilter_ = new DummyAxiomFilter();//new PropertyAxiomFilter();

	protected final ReasoningTestManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> manifest;
	
	private final OWLOntologyManager manager_ = OWLManager.createOWLOntologyManager();
	
	public OWLAPIRandomWalkIncrementalClassificationTest(
			ReasoningTestManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> testManifest) {
		manifest = testManifest;
	}

	@Before
	public void before() throws IOException, Owl2ParseException {
		assumeTrue(!ignore(manifest.getInput()));

	}

	@SuppressWarnings("static-method")
	protected boolean ignore(TestInput input) {
		return false;
	}

	@Test
	public void randomWalk() throws Exception {
		// axioms that can change
		OnOffVector<OWLAxiom> changingAxioms = new OnOffVector<OWLAxiom>(128);
		// other axioms that do not change
		List<OWLAxiom> staticAxioms = new ArrayList<OWLAxiom>();
		InputStream stream = null;
		ElkReasoner incrementalReasoner = null;
		long seed = RandomSeedProvider.VALUE;

		LOGGER_.trace("Initial load of test axioms");

		OWLOntology ontology = null;
		
		try {
			stream = manifest.getInput().getUrl().openStream();
			
			try {
				ontology = manager_.loadOntologyFromOntologyDocument(stream);
				axiomFilter_.filter(ontology, staticAxioms, changingAxioms);
				
			} catch (OWLOntologyCreationException e) {
				throw new Owl2ParseException(e);
			}

			incrementalReasoner = OWLAPITestUtils.createReasoner(ontology, true,
					new PostProcessingStageExecutor());
			
			// let the runner run..
			RandomWalkRunnerIO<OWLAxiom> io = new OWLAPIBasedIO(ontology, incrementalReasoner);
			
			new RandomWalkIncrementalClassificationRunner<OWLAxiom>(MAX_ROUNDS, ITERATIONS, io).run(
					incrementalReasoner.getInternalReasoner(), changingAxioms, staticAxioms, seed);

		} catch (Exception e) {
			throw new ElkRuntimeException("Seed " + seed, e);
		} finally {
			if (incrementalReasoner != null) {
				incrementalReasoner.dispose();
			}
			if (ontology != null) {
				manager_.removeOntology(ontology);
			}
			IOUtils.closeQuietly(stream);
		}
	}

	@Config
	public static Configuration getConfig() throws URISyntaxException,
			IOException {
		return ConfigurationUtils
				.loadFileBasedTestConfiguration(
						INPUT_DATA_LOCATION,
						IncrementalClassificationCorrectnessTest.class,
						"owl",
						"expected",
						new TestManifestCreator<UrlTestInput, TaxonomyTestOutput<?>, TaxonomyTestOutput<?>>() {
							@Override
							public TestManifestWithOutput<UrlTestInput, TaxonomyTestOutput<?>, TaxonomyTestOutput<?>> create(
									URL input, URL output) throws IOException {
								// don't need an expected output for these tests
								return new TaxonomyDiffManifest<TaxonomyTestOutput<?>, TaxonomyTestOutput<?>>(
										input, null);
							}
						});
	}

	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	public static final class OWLAPIBasedIO implements RandomWalkRunnerIO<OWLAxiom> {

		private final OWLOntology ontology_;
		private final ElkReasoner owlapiReasoner_;
		
		public OWLAPIBasedIO(OWLOntology ontology, ElkReasoner owlapiReasoner) {
			ontology_ = ontology;
			owlapiReasoner_ = owlapiReasoner;
		}

		@Override
		public Reasoner createReasoner(Iterable<OWLAxiom> axioms) {					
			try {
				return OWLAPITestUtils.createReasoner(
						OWLManager.createOWLOntologyManager()
								.copyOntology(ontology_, OntologyCopy.SHALLOW),
						false, new SimpleStageExecutor()).getInternalReasoner();
			} catch (OWLOntologyCreationIOException e) {
				throw new RuntimeException(e);
			} catch (OWLOntologyCreationException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void loadChanges(final Reasoner reasoner,
				final IncrementalChange<OWLAxiom> change) {
			OWLOntologyManager manager = ontology_.getOWLOntologyManager();
			
			for (OWLAxiom axiom : change.getDeletions()) {
				LOGGER_.trace("removing: {}", axiom);

				manager.removeAxiom(ontology_, axiom);
			}
			
			for (OWLAxiom axiom : change.getAdditions()) {
				LOGGER_.trace("adding: {}", axiom);

				manager.addAxiom(ontology_, axiom);
			}
			
			owlapiReasoner_.flush();
		}

		@Override
		public void printAxiom(OWLAxiom axiom, Logger logger, LogLevel level) {
			LoggerWrap.log(logger, level, "Current axiom: " + axiom);
		}

		@Override
		public void revertChanges(Reasoner reasoner,
				IncrementalChange<OWLAxiom> change) {
			OWLOntologyManager manager = ontology_.getOWLOntologyManager();
			
			for (OWLAxiom axiom : change.getDeletions()) {
				LOGGER_.trace("adding: {}", axiom);
				manager.addAxiom(ontology_, axiom);
			}
			
			for (OWLAxiom axiom : change.getAdditions()) {
				LOGGER_.trace("deleting: {}", axiom);
				manager.removeAxiom(ontology_, axiom);
			}

			owlapiReasoner_.flush();
		}
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	private static interface AxiomFilter {
		
		void filter(OWLOntology ontology, Collection<OWLAxiom> staticAxioms, Collection<OWLAxiom> dynamicAxioms);
	}
	
	/**
	 * Treats property axioms and static, all others as changing
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	@SuppressWarnings("unused")
	private static class PropertyAxiomFilter implements AxiomFilter {

		final Set<AxiomType<?>> STATIC_AXIOM_TYPES = new HashSet<AxiomType<?>>(
				Arrays.asList(AxiomType.TRANSITIVE_OBJECT_PROPERTY,
						AxiomType.SUB_PROPERTY_CHAIN_OF,
						AxiomType.SUB_OBJECT_PROPERTY,
						AxiomType.REFLEXIVE_OBJECT_PROPERTY));
		
		@Override
		public void filter(OWLOntology ontology,
				Collection<OWLAxiom> staticAxioms,
				Collection<OWLAxiom> dynamicAxioms) {
			
			for (OWLAxiom axiom : ontology.getAxioms()) {
				if (STATIC_AXIOM_TYPES.contains(axiom.getAxiomType())) {
					staticAxioms.add(axiom);
				}
				else {
					dynamicAxioms.add(axiom);
				}
			}
		}
		
	}
	
	/**
	 * Treats all axioms as changing so the reasoner will be forced to
	 * re-classify in case of non-incremental changes
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private static class DummyAxiomFilter implements AxiomFilter {

		@Override
		public void filter(OWLOntology ontology,
				Collection<OWLAxiom> staticAxioms,
				Collection<OWLAxiom> dynamicAxioms) {
			dynamicAxioms.addAll(ontology.getAxioms());
		}
		
	}
}
