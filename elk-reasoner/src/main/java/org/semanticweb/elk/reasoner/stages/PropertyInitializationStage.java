package org.semanticweb.elk.reasoner.stages;

/*
 * #%L
 * ELK Reasoner
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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedPropertyChain;
import org.semanticweb.elk.reasoner.saturation.properties.SaturatedPropertyChain;

//TODO: add progress monitor, make concurrent if possible

/**
 * A {@link ReasonerStage} which deletes all derived information from
 * {@link SaturatedPropertyChain} assigned to {@link IndexedPropertyChain}s
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class PropertyInitializationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(PropertyInitializationStage.class);

	/**
	 * The counter for deleted saturations
	 */
	private int clearedSaturations_;
	/**
	 * The progress counter
	 */
	private int progress_;
	/**
	 * The number of contexts
	 */
	private int maxProgress_;

	/**
	 * The state of the iterator of the input to be processed
	 */
	private Iterator<IndexedPropertyChain> todo_ = null;

	public PropertyInitializationStage(ReasonerStageManager manager) {
		super(manager);
	}

	@Override
	public String getName() {
		return "Property Saturation Initialization";
	}

	@Override
	public boolean done() {
		return reasoner.donePropertySaturationReset;
	}

	@Override
	public List<ReasonerStage> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		try {
			progressMonitor.start(getName());
			for (;;) {
				if (!todo_.hasNext())
					break;
				IndexedPropertyChain ipc = todo_.next();
				SaturatedPropertyChain saturation = ipc.getSaturated();
				if (saturation != null) {
					saturation.clear();
					clearedSaturations_++;
				}
				progressMonitor.report(++progress_, maxProgress_);
				if (interrupted())
					continue;
			}
		} finally {
			progressMonitor.finish();
		}
		reasoner.donePropertySaturationReset = true;
	}

	@Override
	boolean preExecute() {
		if (!super.preExecute())
			return false;
		todo_ = reasoner.ontologyIndex.getIndexedPropertyChains().iterator();
		maxProgress_ = reasoner.ontologyIndex.getIndexedPropertyChains().size();
		progress_ = 0;
		clearedSaturations_ = 0;
		return true;
	}

	@Override
	boolean postExecute() {
		if (!super.postExecute())
			return false;
		todo_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (clearedSaturations_ > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Saturations cleared: " + clearedSaturations_);
	}

}