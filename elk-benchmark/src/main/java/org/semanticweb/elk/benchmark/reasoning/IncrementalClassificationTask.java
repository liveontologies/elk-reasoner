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
import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.io.IOUtils;
import org.semanticweb.elk.loading.Loader;
import org.semanticweb.elk.loading.OntologyLoader;
import org.semanticweb.elk.loading.Owl2ParserLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
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
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Used to evaluate effectiveness of incremental classification
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationTask implements Task {

	private final String ontologyFile_;
	private final ReasonerConfiguration reasonerConfig_;
	private List<ElkAxiom> loadedAxioms_ = null;
	private final int axiomsToChange_;
	
	private Reasoner standardReasoner_ = null;
	private Reasoner incrementalReasoner_ = null;

	final static int REPEAT_NUMBER = 5;

	public IncrementalClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		axiomsToChange_ = Integer.valueOf(args[1]);
		reasonerConfig_ = getConfig(args);
	}

	@Override
	public String getName() {
		return "Incremental classification ["
				+ ontologyFile_.substring(ontologyFile_.lastIndexOf('/')) + "]";
	}

	@Override
	public void prepare() throws TaskException {
		File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);
		
		loadedAxioms_ = new ArrayList<ElkAxiom>();
		standardReasoner_ = prepareReasoner(ontologyFile, true);
		incrementalReasoner_ = prepareReasoner(ontologyFile, true);
		incrementalReasoner_.setIncrementalMode(true);
	}
	
	private Reasoner prepareReasoner(final File ontologyFile, final boolean saveAxioms) throws TaskException {
		InputStream stream = null;
		
		try {
			stream = new FileInputStream(ontologyFile);
			
			final Owl2Parser parser = new Owl2FunctionalStyleParserFactory().getParser(stream);
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
											loadedAxioms_.add(elkAxiom);
										}
									}
								});
					} else {
						return new Owl2ParserLoader(parser, axiomLoader);
					}
				}
			};

			Reasoner reasoner = new ReasonerFactory().createReasoner(
					new LoggingStageExecutor(), reasonerConfig_);

			reasoner.registerOntologyLoader(loader);
			reasoner.loadOntology();
			
			return reasoner;
		} catch (Exception e) {
			throw new TaskException(e);
		}
		finally {
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
	
	private ReasonerConfiguration getConfig(String[] args) {
		ReasonerConfiguration config = ReasonerConfiguration.getConfiguration();

		if (args.length > 2) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
					args[2]);
		}

		return config;
	}

	@Override
	public Result run() throws TaskException {
		
		try {
			TestChangesLoader changeLoader1 = new TestChangesLoader();
			TestChangesLoader changeLoader2 = new TestChangesLoader();
			
			standardReasoner_.registerOntologyChangesLoader(changeLoader1);
			incrementalReasoner_.registerOntologyChangesLoader(changeLoader2);

			// initial correctness check
			correctnessCheck(standardReasoner_, incrementalReasoner_, -1);

			long seed = System.currentTimeMillis();
			Random rnd = new Random(seed);

			for (int i = 0; i < REPEAT_NUMBER; i++) {
				// delete some axioms
				standardReasoner_.setIncrementalMode(false);
				
				Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms_, rnd);

				//System.out.println("===========DELETING " + axiomsToChange_ + " AXIOMS=============");
				
				/*for (ElkAxiom del : deleted) {
					System.out.println(OwlFunctionalStylePrinter.toString(del));
				}*/

				// incremental changes
				changeLoader1.clear();
				changeLoader2.clear();
				remove(changeLoader1, deleted);
				remove(changeLoader2, deleted);

				correctnessCheck(standardReasoner_, incrementalReasoner_, seed);
				
				standardReasoner_.setIncrementalMode(false);
				
				//System.out.println("===========ADDING BACK=============");
				
				// add the axioms back
				changeLoader1.clear();
				changeLoader2.clear();
				add(changeLoader1, deleted);
				add(changeLoader2, deleted);

				correctnessCheck(standardReasoner_, incrementalReasoner_, seed);
			}

		} catch (ElkException e) {
			throw new TaskException(e);
		} finally {
			try {
				standardReasoner_.shutdown();
				incrementalReasoner_.shutdown();
			} catch (InterruptedException e) {
			}
		}

		return null;
	}

	/*
	 * can return a smaller subset than requested because one axiom can be randomly picked more than once
	 */
	private Set<ElkAxiom> getRandomSubset(List<ElkAxiom> axioms, Random rnd) {
		Set<ElkAxiom> subset = new ArrayHashSet<ElkAxiom>(axiomsToChange_); 
		
		if (axiomsToChange_ >= axioms.size()) {
			subset.addAll(axioms); 
		}
		else {
			for (int i = 0; i < axiomsToChange_; i++) {
				ElkAxiom axiom = axioms.get(rnd.nextInt(axiomsToChange_)); 
				
				subset.add(axiom);
			}
		}
		
		return subset;
	}

	private void add(TestChangesLoader loader, Collection<ElkAxiom> axiomList) {
		for (ElkAxiom axiom : axiomList) {
			loader.add(axiom);
		}
	}

	private void remove(TestChangesLoader loader, Collection<ElkAxiom> axiomList) {
		for (ElkAxiom axiom : axiomList) {
			loader.remove(axiom);
		}
	}
	
	protected void correctnessCheck(Reasoner standardReasoner, Reasoner incrementalReasoner, long seed) throws ElkException {
		Taxonomy<ElkClass> expected = getTaxonomy(standardReasoner);
		
//		System.out.println("===========INCREMENTAL==============");
		
		Taxonomy<ElkClass> incremental = getTaxonomy(incrementalReasoner);
		
		int expectedHashCode = TaxonomyHasher.hash(expected);
		int gottenHashCode = TaxonomyHasher.hash(incremental);
		
		if (expectedHashCode != gottenHashCode) {
			
/*			try {
				Writer writer1 = new OutputStreamWriter(new FileOutputStream(new File("/home/pavel/tmp/expected.owl")));
				Writer writer2 = new OutputStreamWriter(new FileOutputStream(new File("/home/pavel/tmp/gotten.owl")));				
				TaxonomyPrinter.dumpClassTaxomomy(expected, writer1, false);
				TaxonomyPrinter.dumpClassTaxomomy(incremental, writer2, false);
				writer1.flush();
				writer2.flush();
				writer1.close();
				writer2.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			
			throw new RuntimeException("Comparison failed for seed " + seed);
		}
	}
	
	private Taxonomy<ElkClass> getTaxonomy(Reasoner reasoner) {
		Taxonomy<ElkClass> result = null;
		
		try {
			result = reasoner.getTaxonomy();
		} catch (ElkException e) {
			result = PredefinedTaxonomy.INCONSISTENT_CLASS_TAXONOMY;
		}
		
		return result;
	}	
}
