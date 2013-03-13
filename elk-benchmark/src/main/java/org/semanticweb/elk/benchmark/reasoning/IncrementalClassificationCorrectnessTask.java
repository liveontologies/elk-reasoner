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
import java.util.Random;
import java.util.Set;

import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.owl.exceptions.ElkException;
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
		standardReasoner_.setAllowIncrementalMode(false);
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

			Set<ElkAxiom> deleted = getRandomSubset(loadedAxioms, rnd);

			// incremental changes
			remove(changeLoader1, deleted);
			remove(changeLoader2, deleted);
			
			correctnessCheck(standardReasoner_, incrementalReasoner, seed);

			// add the axioms back
			add(changeLoader1, deleted);
			add(changeLoader2, deleted);

			correctnessCheck(standardReasoner_, incrementalReasoner, seed);
		}
	}
	
	
	protected void correctnessCheck(Reasoner standardReasoner, Reasoner incrementalReasoner, long seed) throws TaskException {
		try {
			Taxonomy<ElkClass> expected = standardReasoner.getTaxonomyQuietly();
			Taxonomy<ElkClass> incremental = incrementalReasoner.getTaxonomyQuietly();
			
			int expectedHashCode = TaxonomyHasher.hash(expected);
			int gottenHashCode = TaxonomyHasher.hash(incremental);
			
			if (expectedHashCode != gottenHashCode) {
				
				throw new TaskException("Comparison failed for seed " + seed);
			}
		} catch (ElkException e) {
			throw new TaskException(e);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		shutdown(standardReasoner_);
	}	
	
}
