/**
 * 
 */
package org.semanticweb.elk.reasoner.stages;
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

import org.semanticweb.elk.benchmark.Metrics;
import org.semanticweb.elk.owl.exceptions.ElkException;

/**
 * A simple executor which measures the time spent on stage execution
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class TimingStageExecutor extends AbstractStageExecutor {

	private final AbstractStageExecutor executor_;
	
	protected final Metrics metrics;
	
	public static final String WALL_TIME = ".wall-time";
	
	public TimingStageExecutor(final AbstractStageExecutor executor, Metrics m) {
		executor_ = executor;
		metrics = m;
	}
	
	protected boolean measure(ReasonerStage stage) {
		return true;
	}
	
	protected void executeStage(ReasonerStage stage) throws ElkException {
		executor_.execute(stage);
	}

	@Override
	public void execute(ReasonerStage stage) throws ElkException {
		long ts = System.currentTimeMillis();

		executeStage(stage);
		ts = System.currentTimeMillis() - ts;

		if (measure(stage)) {
			metrics.updateLongMetric(stage.getName() + WALL_TIME, ts);
		}		
	}

}
