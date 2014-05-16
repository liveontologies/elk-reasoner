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

import java.util.List;

import org.semanticweb.elk.RandomSeedProvider;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.loading.AxiomLoader;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.incremental.ClassAndIndividualAxiomTrackingLoader;
import org.semanticweb.elk.reasoner.incremental.OnOffVector;
import org.semanticweb.elk.reasoner.incremental.RandomWalkIncrementalRealizationRunner;
import org.semanticweb.elk.reasoner.incremental.RandomWalkRunnerIO;

/**
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class RandomWalkIncrementalClassificationWithABoxTask extends
		RandomWalkIncrementalClassificationTask {

	public RandomWalkIncrementalClassificationWithABoxTask(String[] args) {
		super(args);
	}

	@Override
	public void run() throws TaskException {
		long seed = RandomSeedProvider.VALUE;

		try {
			new RandomWalkIncrementalRealizationRunner<ElkAxiom>(ROUNDS,
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
	protected AxiomLoader getAxiomTrackingLoader(AxiomLoader fileLoader,
			OnOffVector<ElkAxiom> changingAxioms, List<ElkAxiom> staticAxioms) {
		return new ClassAndIndividualAxiomTrackingLoader(fileLoader,
				changingAxioms, staticAxioms);
	}

}
