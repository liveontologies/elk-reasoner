package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;

import org.junit.Assert;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.ReasonerFactory;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Adds correctness checks to the superclass
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class MultiDeltasCheckCorrectnessTask extends
		IncrementalClassificationMultiDeltasTask {

	
	private Reasoner standardReasoner_;
	
	public MultiDeltasCheckCorrectnessTask(String[] args) {
		super(args);
	}

	
	@Override
	public void dispose() {
		try {
			if (standardReasoner_ != null) {
				standardReasoner_.shutdown();
				standardReasoner_ = null;
			}			
		} catch (InterruptedException e) {
		}
	}


	@Override
	protected Task getFirstTimeClassificationTask(File source) {
		return new MultiDeltasCheckCorrectnessTask.ClassifyFirstTime(source);
	}

	@Override
	protected Task getIncrementalClassificationTask(File source) {
		return new ClassifyIncrementallyWithCheck(source);
	}




	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	class ClassifyFirstTime extends IncrementalClassificationMultiDeltasTask.ClassifyFirstTime {

		ClassifyFirstTime(File file) {
			super(file);
		}
		
		@Override
		public void prepare() throws TaskException {
			super.prepare();
			
			standardReasoner_ = new ReasonerFactory().createReasoner(new SimpleStageExecutor(), config_);
			load(standardReasoner_);
		}

		@Override
		public void run() throws TaskException {
			Taxonomy<ElkClass> incrementalTaxonomy = reasoner_
					.getTaxonomyQuietly();
			Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
					.getTaxonomyQuietly();

			Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
					TaxonomyHasher.hash(standardTaxonomy));
		}
		
	}
	
	/**
	 * 
	 * @author Pavel Klinov
	 *
	 * pavel.klinov@uni-ulm.de
	 */
	class ClassifyIncrementallyWithCheck extends ClassifyIncrementally {

		ClassifyIncrementallyWithCheck(File dir) {
			super(dir);
		}
		
		@Override
		public void prepare() throws TaskException {
			super.prepare();
			
			standardReasoner_.setIncrementalMode(false);
			loadChanges(standardReasoner_);
		}

		
		@Override
		public void run() throws TaskException {
			Taxonomy<ElkClass> incrementalTaxonomy = reasoner_
					.getTaxonomyQuietly();
			Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
					.getTaxonomyQuietly();

			Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
					TaxonomyHasher.hash(standardTaxonomy));
		}
		
	}
}
