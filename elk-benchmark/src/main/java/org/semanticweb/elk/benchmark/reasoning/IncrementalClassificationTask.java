package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Result;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.benchmark.util.LoaderThatRemembersAxioms;
import org.semanticweb.elk.loading.EmptyChangesLoader;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;
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

	private Reasoner reasoner_;
	private final String ontologyFile_;
	private final ReasonerConfiguration reasonerConfig_;
	private List<ElkAxiom> loadedAxioms_ = null;

	final static int REPEAT_NUMBER = 1;
	final static double CHANGE_FRACTION = 0.2;

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
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);
			LoaderThatRemembersAxioms loader = new LoaderThatRemembersAxioms(
					new Owl2FunctionalStyleParserFactory(), ontologyFile, 100000);

			reasoner_ = new ReasonerFactory().createReasoner(
					new LoggingStageExecutor(), reasonerConfig_);
			reasoner_.registerOntologyLoader(loader);
			reasoner_.registerOntologyChangesLoader(new EmptyChangesLoader());
			reasoner_.loadOntology();
			// remember the loaded axioms
			loadedAxioms_ = loader.getLoadedAxioms();
		} catch (Exception e) {
			throw new TaskException(e);
		}
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
		try {
			TestChangesLoader initialLoader = new TestChangesLoader();
			TestChangesLoader loader = new TestChangesLoader();
			Reasoner standardReasoner = new ReasonerFactory()
					.createReasoner(new LoggingStageExecutor());
			Reasoner incrementalReasoner = new ReasonerFactory()
					.createReasoner(new LoggingStageExecutor());

			standardReasoner.registerOntologyLoader(initialLoader);
			standardReasoner.registerOntologyChangesLoader(loader);
			incrementalReasoner.registerOntologyLoader(initialLoader);
			incrementalReasoner.registerOntologyChangesLoader(loader);

			incrementalReasoner.setIncrementalMode(true);
			// initial load
			add(initialLoader, loadedAxioms_);
			// initial correctness check
			correctnessCheck(standardReasoner, incrementalReasoner, -1);

			long seed =  12345; //System.currentTimeMillis();
			Random rnd = new Random(seed);

			for (int i = 0; i < REPEAT_NUMBER; i++) {
				// delete some axioms
				Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms_, rnd,
						CHANGE_FRACTION);

				for (ElkAxiom del : deleted) {
					System.err.println(OwlFunctionalStylePrinter.toString(del));
				}

				// incremental changes
				loader.clear();
				remove(loader, deleted);
				standardReasoner.registerOntologyChangesLoader(loader);
				incrementalReasoner.registerOntologyChangesLoader(loader);

				correctnessCheck(standardReasoner, incrementalReasoner, seed);
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
				reasoner_.shutdown();
			} catch (InterruptedException e) {
			}
		}

		return null;
	}

	/*
	 * can return a smaller subset than requested because one axiom can be randomly picked more than once
	 */
	private Set<ElkAxiom> getRandomSubset(List<ElkAxiom> axioms, Random rnd, double fraction) {
		int num = (int) (axioms.size() * fraction);
		Set<ElkAxiom> subset = new ArrayHashSet<ElkAxiom>(num); 
		
		if (num >= axioms.size()) {
			subset.addAll(axioms); 
		}
		else {
			for (int i = 0; i < num; i++) {
				subset.add(axioms.get(rnd.nextInt(num)));
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
		Taxonomy<ElkClass> incremental = getTaxonomy(incrementalReasoner);
		int expectedHashCode = TaxonomyHasher.hash(expected);
		int gotten = TaxonomyHasher.hash(incremental);
		
		if (expectedHashCode != gotten) {
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
