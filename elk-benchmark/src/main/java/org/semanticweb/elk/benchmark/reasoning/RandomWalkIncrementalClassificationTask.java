/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

/*
 * #%L
 * ELK Benchmarking Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.loading.Owl2StreamLoader;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.managers.ElkEntityRecycler;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.TestReasonerUtils;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.ClassAxiomTrackingLoader;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;
import org.semanticweb.elk.reasoner.incremental.RandomWalkIncrementalClassificationRunner;
import org.semanticweb.elk.reasoner.incremental.RandomWalkRunnerIO;
import org.semanticweb.elk.reasoner.stages.PostProcessingStageExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO docs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalClassificationTask implements Task {

	// logger for this class
	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(RandomWalkIncrementalClassificationTask.class);

	protected Reasoner reasoner_;
	private final String ontologyFile_;
	protected final ReasonerConfiguration reasonerConfig_;
	protected OnOffVector<ElkAxiom> changingAxioms_ = null;
	// other axioms that do not change
	protected List<ElkAxiom> staticAxioms_ = null;

	/**
	 * how many test rounds is used
	 */
	static int ROUNDS = 5;
	/**
	 * how many changes are generated in every round
	 */
	static int ITERATIONS = 5;
	/**
	 * initial change size; will double with every round
	 */
	static int INITIAL_CHANGES_SIZE = 5;

	public RandomWalkIncrementalClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = BenchmarkUtils.getReasonerConfiguration(args);
	}

	@Override
	public String getName() {
		return "Random walk incremental classification";
	}

	@Override
	public void prepare() throws TaskException {
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);

			changingAxioms_ = new OnOffVector<ElkAxiom>(128);
			staticAxioms_ = new ArrayList<ElkAxiom>();
			AxiomLoader fileLoader = new Owl2StreamLoader(
					new Owl2FunctionalStyleParserFactory(
							new ElkObjectFactoryImpl(new ElkEntityRecycler())),
					ontologyFile);
			AxiomLoader trackingLoader = getAxiomTrackingLoader(fileLoader,
					changingAxioms_, staticAxioms_);
			reasoner_ = TestReasonerUtils.createTestReasoner(trackingLoader,
					new PostProcessingStageExecutor(), reasonerConfig_);
		} catch (Exception e) {
			throw new TaskException(e);
		}
	}

	@Override
	public void run() throws TaskException {
		long seed = RandomSeedProvider.VALUE;

		try {
			new RandomWalkIncrementalClassificationRunner<ElkAxiom>(ROUNDS,
					ITERATIONS, new RandomWalkRunnerIO.ElkAPIBasedIO()).run(
					reasoner_, changingAxioms_, staticAxioms_, seed);
		} catch (Exception e) {
			throw new TaskException(e);
		} finally {
			try {
				reasoner_.shutdown();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void dispose() {
		try {
			reasoner_.shutdown();
		} catch (InterruptedException e) {
		}
	}

	@Override
	public Metrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void postRun() throws TaskException {}

	protected AxiomLoader getAxiomTrackingLoader(AxiomLoader fileLoader,
			OnOffVector<ElkAxiom> changingAxioms, List<ElkAxiom> staticAxioms) {
		return new ClassAxiomTrackingLoader(fileLoader, changingAxioms,
				staticAxioms);
	}

}
