/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.incremental;

import java.util.ArrayList;

import org.semanticweb.elk.reasoner.incremental.ContextInitializationFactory.ContextProcessor;
import org.semanticweb.elk.reasoner.incremental.ContextInitializationFactory.TimedContextProcessor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InterruptMonitor;
import org.semanticweb.elk.util.logging.CachedTimeThread;

/**
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
class TimedContextCollectionProcessor extends
		BaseInputProcessor<ArrayList<Context>> {

	private final ContextProcessor contextProcessor_;

	private final IncrementalProcessingStatistics stageStats_;

	private final IncrementalProcessingStatistics localStats_ = new IncrementalProcessingStatistics();

	private final InterruptMonitor interrupter_;
	
	private int procNumber_ = 0;
		

	TimedContextCollectionProcessor(ContextProcessor baseProcessor,
			IncrementalProcessingStatistics stageStats,
			InterruptMonitor interrupter) {
		contextProcessor_ = new TimedContextProcessor(baseProcessor,
				localStats_);
		stageStats_ = stageStats;
		interrupter_ = interrupter;
		localStats_.startMeasurements();		
	}

	@Override
	protected void process(ArrayList<Context> contexts) {
		long ts = CachedTimeThread.getCurrentTimeMillis();
		int contextCount = 0;
		int subsumerCount = 0;

		procNumber_++;

		for (Context context : contexts) {
			contextProcessor_.process(context);
			contextCount++;
			subsumerCount += context.getComposedSubsumers().size();
		}

		localStats_.changeInitContextCollectionProcessingTime += (CachedTimeThread
				.getCurrentTimeMillis() - ts);
		localStats_.countContexts += contextCount;

		if (contextCount > 0) {
			localStats_.countContextSubsumers += (subsumerCount / contextCount);
		}
	}

	@Override
	public void finish() {
		super.finish();
		contextProcessor_.finish();

		if (procNumber_ > 0) {
			localStats_.countContextSubsumers /= procNumber_;
		}

		stageStats_.add(localStats_);
	}

	@Override
	protected boolean isInterrupted() {
		return interrupter_.isInterrupted();
	}
	
	
}
