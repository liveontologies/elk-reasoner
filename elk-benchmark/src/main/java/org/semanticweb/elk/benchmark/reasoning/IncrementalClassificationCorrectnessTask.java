/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.incremental.TestChangesLoader;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalClassificationCorrectnessTask extends
		IncrementalClassificationTask {

	private Reasoner standardReasoner_ = null;
	
	
	public IncrementalClassificationCorrectnessTask(String[] args) {
		super(args);
	}

	@Override
	public void prepare() throws TaskException {
		super.prepare();
		
		File ontologyFile = BenchmarkUtils.getFile(ontologyFileName);
		
		standardReasoner_ = prepareReasoner(ontologyFile, true);
	}

	@Override
	public void run() throws TaskException {
		
		TestChangesLoader changeLoader1 = new TestChangesLoader();
		TestChangesLoader changeLoader2 = new TestChangesLoader();

		standardReasoner_.registerOntologyChangesLoader(changeLoader1);
		incrementalReasoner.registerOntologyChangesLoader(changeLoader2);
		// initial correctness check
		correctnessCheck(standardReasoner_, incrementalReasoner, -1);

		long seed = System.currentTimeMillis();
		Random rnd = new Random(seed);

		for (int i = 0; i < REPEAT_NUMBER; i++) {
			// delete some axioms
			standardReasoner_.setIncrementalMode(false);

			Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms, rnd);

			// incremental changes
			changeLoader1.clear();
			changeLoader2.clear();
			remove(changeLoader1, deleted);
			remove(changeLoader2, deleted);
			
			correctnessCheck(standardReasoner_, incrementalReasoner, seed);

			standardReasoner_.setIncrementalMode(false);

			// add the axioms back
			changeLoader1.clear();
			changeLoader2.clear();
			add(changeLoader1, deleted);
			add(changeLoader2, deleted);

			correctnessCheck(standardReasoner_, incrementalReasoner, seed);
		}
	}
	
	
	protected void correctnessCheck(Reasoner standardReasoner, Reasoner incrementalReasoner, long seed) throws TaskException {
		Taxonomy<ElkClass> expected = standardReasoner.getTaxonomyQuietly();
		Taxonomy<ElkClass> incremental = incrementalReasoner.getTaxonomyQuietly();
		
		int expectedHashCode = TaxonomyHasher.hash(expected);
		int gottenHashCode = TaxonomyHasher.hash(incremental);
		
		if (expectedHashCode != gottenHashCode) {
			
			throw new TaskException("Comparison failed for seed " + seed);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		shutdown(standardReasoner_);
	}	
	
}
