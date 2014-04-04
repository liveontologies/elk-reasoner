/*
 * #%L
 * ELK Bencharking Package
 * 
 * $Id$
 * $HeadURL$
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
/**
 * 
 */
package org.semanticweb.elk.benchmark.reasoning;

import java.io.File;

import org.semanticweb.elk.alc.loading.AxiomLoader;
import org.semanticweb.elk.alc.loading.Owl2StreamLoader;
import org.semanticweb.elk.alc.reasoner.Reasoner;
import org.semanticweb.elk.alc.saturation.SaturationStatistics;
import org.semanticweb.elk.benchmark.BenchmarkUtils;
import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.benchmark.Task;
import org.semanticweb.elk.benchmark.TaskException;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.parsing.Owl2ParserFactory;
import org.semanticweb.elk.owl.parsing.javacc.Owl2FunctionalStyleParserFactory;

/**
 * A task to classify an ontology using the ALC reasoner
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class ALCClassificationTask implements Task {

	private final String SAT_PREFIX = "sat.";
	private final String CLASSIFY_PREFIX = "classify.";
	private final String REDUCE_PREFIX = "reduce.";
	private final String TIME = "time";
	private final String ADDED_CONCLUSIONS = "conclusions.added";
	private final String REMOVED_CONCLUSIONS = "conclusions.removed";
	private final String INCONSISTENT_ROOTS = "roots.inconsistent";
	
	private Reasoner reasoner_;
	private final String ontologyFile_;
	private final Metrics metrics_ = new Metrics();
	private boolean classificationOptimization_ = false;

	public ALCClassificationTask(String[] args) {
		ontologyFile_ = args[0];
		classificationOptimization_ = Boolean.valueOf(args[1]);
	}

	@Override
	public String getName() {
		return "EL classification ["
				+ ontologyFile_.substring(ontologyFile_.lastIndexOf('/')) + "]";
	}

	@Override
	public void prepare() throws TaskException {
		AxiomLoader loader = null;
		
		try {
			File ontologyFile = BenchmarkUtils.getFile(ontologyFile_);
			Owl2ParserFactory parserFactory = new Owl2FunctionalStyleParserFactory();
			
			loader = new Owl2StreamLoader(parserFactory, ontologyFile);
			reasoner_ = new Reasoner(loader);
			// loading in prepare
			reasoner_.forceLoading();
		} catch (Exception e) {
			throw new TaskException(e);
		}
		finally {
			if (loader != null) {
				loader.dispose();
			}
		}
	}

	@Override
	public void run() throws TaskException {
		try {
			long ts = System.currentTimeMillis();
			
			reasoner_.checkSatisfiability();
			
			recordStatistics(SAT_PREFIX, System.currentTimeMillis() - ts); 
			ts = System.currentTimeMillis();
			
			if (classificationOptimization_) {
				reasoner_.classifyOptimized();
			}
			else {
				reasoner_.classify();
			}
			recordStatistics(CLASSIFY_PREFIX, System.currentTimeMillis() - ts); 
			ts = System.currentTimeMillis();
			
			reasoner_.reduce();
			recordStatistics(REDUCE_PREFIX, System.currentTimeMillis() - ts); 
			ts = System.currentTimeMillis();

		} catch (ElkException e) {
			throw new TaskException(e);
		}
		
		metrics_.incrementRunCount();
	}

	private void recordStatistics(String prefix, long time) {
		SaturationStatistics stats = reasoner_.getStatistics();
		
		metrics_.updateLongMetric(prefix + ADDED_CONCLUSIONS, stats.addedConclusions);
		metrics_.updateLongMetric(prefix + REMOVED_CONCLUSIONS, stats.removedConclusions);
		metrics_.updateLongMetric(prefix + INCONSISTENT_ROOTS, stats.inconsistentRoots);
		metrics_.updateLongMetric(prefix + TIME, time);
	}

	@Override
	public void dispose() {
	}

	@Override
	public Metrics getMetrics() {
		return metrics_;
	}
	
	@Override
	public void postRun() throws TaskException {}

}
