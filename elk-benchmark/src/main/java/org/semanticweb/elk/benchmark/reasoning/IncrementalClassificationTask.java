package org.semanticweb.elk.benchmark.reasoning;

/*
 * #%L
 * ELK Benchmarking Package
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2ParserLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.visitors.ElkAxiomProcessor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Used to evaluate effectiveness of incremental classification
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationTask implements Task {

	protected final String ontologyFileName;
	protected final ReasonerConfiguration reasonerConfig;
	protected List<ElkAxiom> loadedAxioms = null;
	protected final int axiomsToChange;

	protected Reasoner incrementalReasoner = null;

	final static int REPEAT_NUMBER = 5;

	public IncrementalClassificationTask(String[] args) {
		ontologyFileName = args[0];
		axiomsToChange = args.length > 1 ? Integer.valueOf(args[1]) : -1;
		reasonerConfig = ReasonerConfiguration.getConfiguration();
	}

	@Override
	public String getName() {
		return "Incremental classification ["
				+ ontologyFileName.substring(ontologyFileName.lastIndexOf('/'))
				+ "]";
	}

	@Override
	public void prepare() throws TaskException {
		File ontologyFile = BenchmarkUtils.getFile(ontologyFileName);

		loadedAxioms = new ArrayList<ElkAxiom>();
		incrementalReasoner = prepareReasoner(ontologyFile, true);
		incrementalReasoner.setAllowIncrementalMode(true);
	}

	protected Reasoner prepareReasoner(final File ontologyFile,
			final boolean saveAxioms) throws TaskException {
		InputStream stream = null;

		try {
			stream = new FileInputStream(ontologyFile);

			final Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
					.getParser(stream);
			final OntologyLoader loader = new OntologyLoader() {

				@Override
				public Loader getLoader(final ElkAxiomProcessor axiomLoader) {

					if (saveAxioms) {
						return new Owl2ParserLoader(parser,
								new ElkAxiomProcessor() {

									@Override
									public void visit(ElkAxiom elkAxiom) {
										axiomLoader.visit(elkAxiom);

										if (passAxiom(elkAxiom)) {
											loadedAxioms.add(elkAxiom);
										}
									}
								});
					} else {
						return new Owl2ParserLoader(parser, axiomLoader);
					}
				}
			};

			Reasoner reasoner = new ReasonerFactory().createReasoner(loader,
					new LoggingStageExecutor(), reasonerConfig);

			reasoner.loadOntology();

			return reasoner;
		} catch (Exception e) {
			throw new TaskException(e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}

	protected List<ElkAxiom> loadAxioms(InputStream stream) throws IOException,
			Owl2ParseException {
		Owl2Parser parser = new Owl2FunctionalStyleParserFactory()
				.getParser(stream);
		final List<ElkAxiom> axioms = new ArrayList<ElkAxiom>();

		parser.accept(new Owl2ParserAxiomProcessor() {

			@Override
			public void visit(ElkPrefix elkPrefix) throws Owl2ParseException {
			}

			@Override
			public void visit(ElkAxiom elkAxiom) throws Owl2ParseException {
				axioms.add(elkAxiom);
			}
		});

		return axioms;
	}

	protected boolean passAxiom(ElkAxiom axiom) {
		return axiom instanceof ElkClassAxiom;
	}

	@Override
	public void run() throws TaskException {
		long seed = System.currentTimeMillis();
		Random rnd = new Random(seed);
		TestChangesLoader changeLoader = new TestChangesLoader();

		incrementalReasoner.registerOntologyChangesLoader(changeLoader);

		for (int i = 0; i < REPEAT_NUMBER; i++) {
			// delete some axioms
			Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms, rnd);

			/*
			 * for (ElkAxiom del : deleted) {
			 * System.out.println(OwlFunctionalStylePrinter.toString(del)); }
			 */

			// incremental changes
			changeLoader.clear();

			remove(changeLoader, deleted);
			incrementalReasoner.getTaxonomyQuietly();
			// add the axioms back

			changeLoader.clear();
			add(changeLoader, deleted);

			incrementalReasoner.getTaxonomyQuietly();
		}
	}

	/*
	 * can return a smaller subset than requested because one axiom can be
	 * randomly picked more than once
	 */
	protected Set<ElkAxiom> getRandomSubset(List<ElkAxiom> axioms, Random rnd) {
		int size = axiomsToChange > 0 ? axiomsToChange : Math.max(1,
				axioms.size() / 100);
		Set<ElkAxiom> subset = new ArrayHashSet<ElkAxiom>(size);

		if (size >= axioms.size()) {
			subset.addAll(axioms);
		} else {
			for (int i = 0; i < size; i++) {
				ElkAxiom axiom = axioms.get(rnd.nextInt(size));

				subset.add(axiom);
			}
		}

		return subset;
	}

	protected void add(TestChangesLoader loader, Collection<ElkAxiom> axiomList) {
		for (ElkAxiom axiom : axiomList) {
			loader.add(axiom);
		}
	}

	protected void remove(TestChangesLoader loader,
			Collection<ElkAxiom> axiomList) {
		for (ElkAxiom axiom : axiomList) {
			loader.remove(axiom);
		}
	}

	@Override
	public void dispose() {
		shutdown(incrementalReasoner);
	}

	protected void shutdown(Reasoner reasoner) {
		if (reasoner != null) {
			try {
				reasoner.shutdown();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public Metrics getMetrics() {
		// TODO Auto-generated method stub
		return null;
	}
}
