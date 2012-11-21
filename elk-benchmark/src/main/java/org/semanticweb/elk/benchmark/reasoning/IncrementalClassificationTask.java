package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
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
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassAxiom;
import org.semanticweb.elk.owl.iris.ElkPrefix;
import org.semanticweb.elk.owl.parsing.Owl2ParseException;
import org.semanticweb.elk.owl.parsing.Owl2Parser;
import org.semanticweb.elk.owl.parsing.Owl2ParserAxiomProcessor;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.config.ReasonerConfiguration;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.stages.LoggingStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.PredefinedTaxonomy;
import org.semanticweb.elk.reasoner.taxonomy.TaxonomyPrinter;
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

	final static int REPEAT_NUMBER = 10;
	final static double CHANGE_FRACTION = 0.02;

	public IncrementalClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		reasonerConfig_ = getConfig(args);
	}

	@Override
	public String getName() {
		return "Incremental classification ["
				+ ontologyFile_.substring(ontologyFile_.lastIndexOf('/')) + "]";
	}

	@Override
	public void prepare() throws TaskException {
		InputStream stream = null;
		
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);

			stream = new FileInputStream(ontologyFile);
			// remember the loaded axioms
			loadedAxioms_ = loadAxioms(stream);
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

		if (args.length > 1) {
			config.setParameter(ReasonerConfiguration.NUM_OF_WORKING_THREADS,
					args[1]);
		}

		return config;
	}

	@Override
	public Result run() throws TaskException {
		Reasoner standardReasoner = null;
		Reasoner incrementalReasoner = null;
		
		try {
			TestChangesLoader initialLoader = new TestChangesLoader();
			TestChangesLoader loader = new TestChangesLoader();
			
			standardReasoner = new ReasonerFactory().createReasoner(
					new LoggingStageExecutor(), reasonerConfig_);
			incrementalReasoner = new ReasonerFactory().createReasoner(
					new LoggingStageExecutor(), reasonerConfig_);

			standardReasoner.registerOntologyLoader(initialLoader);
			standardReasoner.registerOntologyChangesLoader(loader);
			incrementalReasoner.registerOntologyLoader(initialLoader);
			incrementalReasoner.registerOntologyChangesLoader(loader);

			incrementalReasoner.setIncrementalMode(true);
			// initial load
			add(initialLoader, loadedAxioms_);
			// initial correctness check
			correctnessCheck(standardReasoner, incrementalReasoner, -1);

			long seed = 1353526500142L;//System.currentTimeMillis();
			Random rnd = new Random(seed);

			for (int i = 0; i < REPEAT_NUMBER; i++) {
				// delete some axioms
				Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms_, rnd,
						CHANGE_FRACTION);

				System.out.println("===========DELETING==============");
				
				/*for (ElkAxiom del : deleted) {
					System.err.println(OwlFunctionalStylePrinter.toString(del));
				}*/

				// incremental changes
				loader.clear();
				remove(loader, deleted);
				standardReasoner.registerOntologyChangesLoader(loader);
				incrementalReasoner.registerOntologyChangesLoader(loader);

				correctnessCheck(standardReasoner, incrementalReasoner, seed);
				
				System.out.println("===========ADDING==============");
				
				// add the axioms back
				loader.clear();
				add(loader, deleted);
				standardReasoner.registerOntologyChangesLoader(loader);
				incrementalReasoner.registerOntologyChangesLoader(loader);

				correctnessCheck(standardReasoner, incrementalReasoner, seed);
			}

		} catch (ElkException e) {
			throw new TaskException(e);
		} finally {
			try {
				standardReasoner.shutdown();
				incrementalReasoner.shutdown();
			} catch (InterruptedException e) {
			}
		}

		return null;
	}

	/*
	 * can return a smaller subset than requested because one axiom can be randomly picked more than once
	 */
	private Set<ElkAxiom> getRandomSubset(List<ElkAxiom> axioms, Random rnd, double fraction) {
		int num = 75;//(int) (axioms.size() * fraction);
		Set<ElkAxiom> subset = new ArrayHashSet<ElkAxiom>(num); 
		
		if (num >= axioms.size()) {
			subset.addAll(axioms); 
		}
		else {
			int filteredCnt = 0;
			final int STOP = 100;
			
			for (int i = 0; i < num && filteredCnt < STOP;) {
				ElkAxiom axiom = axioms.get(rnd.nextInt(num)); 
				
				if (passAxiom(axiom)) {
					subset.add(axiom);
					i++;
					filteredCnt = 0;
				}
				else {
					filteredCnt++;
				}
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
		
		System.out.println("===========INCREMENTAL==============");
		
		Taxonomy<ElkClass> incremental = getTaxonomy(incrementalReasoner);
		
		int expectedHashCode = TaxonomyHasher.hash(expected);
		int gottenHashCode = TaxonomyHasher.hash(incremental);
		
		if (expectedHashCode != gottenHashCode) {
			
			try {
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
			}

			
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
