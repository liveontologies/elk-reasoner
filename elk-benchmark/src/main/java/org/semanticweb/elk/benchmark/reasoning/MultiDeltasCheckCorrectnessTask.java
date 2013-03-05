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

import org.junit.Assert;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.stages.SimpleStageExecutor;
import org.semanticweb.elk.reasoner.taxonomy.hashing.TaxonomyHasher;
import org.semanticweb.elk.reasoner.taxonomy.model.Taxonomy;

/**
 * Adds correctness checks to the superclass
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class MultiDeltasCheckCorrectnessTask extends
		IncrementalClassificationMultiDeltas {

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
	 *         pavel.klinov@uni-ulm.de
	 */
	class ClassifyFirstTime extends
			IncrementalClassificationMultiDeltas.ClassifyFirstTime {

		ClassifyFirstTime(File file) {
			super(file);
		}

		@Override
		public void prepare() throws TaskException {
			super.prepare();
			stageExecutor = new SimpleStageExecutor();
			createReasoner();
		}

		@Override
		public void run() throws TaskException {
			try {
				Taxonomy<ElkClass> incrementalTaxonomy = reasoner
						.getTaxonomyQuietly();
				Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
						.getTaxonomyQuietly();

				Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
						TaxonomyHasher.hash(standardTaxonomy));
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

	}

	/**
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	class ClassifyIncrementallyWithCheck extends ClassifyIncrementally {

		ClassifyIncrementallyWithCheck(File dir) {
			super(dir);
		}

		@Override
		public void prepare() throws TaskException {
			super.prepare();

			standardReasoner_.setAllowIncrementalMode(false);
			loadChanges(standardReasoner_);
		}

		@Override
		public void run() throws TaskException {
			try {
				Taxonomy<ElkClass> incrementalTaxonomy = reasoner
						.getTaxonomyQuietly();
				Taxonomy<ElkClass> standardTaxonomy = standardReasoner_
						.getTaxonomyQuietly();

				Assert.assertEquals(TaxonomyHasher.hash(incrementalTaxonomy),
						TaxonomyHasher.hash(standardTaxonomy));
			} catch (ElkException e) {
				throw new TaskException(e);
			}
		}

	}
}
