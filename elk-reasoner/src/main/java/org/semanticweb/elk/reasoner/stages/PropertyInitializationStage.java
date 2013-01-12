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
 * A {@link ReasonerStage} which purpose is to ensure that no
 * {@link SaturatedPropertyChain} is assigned to {@link IndexedPropertyChain}s
 * of the current ontology
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
	private int deletedSaturations_;
	/**
	 * The number of contexts
	 */
	private int maxSaturations_;

	/**
	 * The state of the iterator of the input to be processed
	 */
	private Iterator<IndexedPropertyChain> todo = null;

	public PropertyInitializationStage(AbstractReasonerState reasoner) {
		super(reasoner);
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
	public void execute() throws ElkInterruptedException {
		if (todo == null)
			initComputation();
		try {
			progressMonitor.start(getName());
			for (;;) {
				if (!todo.hasNext())
					break;
				IndexedPropertyChain ipc = todo.next();
				ipc.resetSaturated();
				deletedSaturations_++;
				progressMonitor.report(deletedSaturations_, maxSaturations_);
				if (interrupted())
					continue;
			}
		} finally {
			progressMonitor.finish();
		}
		reasoner.donePropertySaturationReset = true;
	}

	@Override
	void initComputation() {
		super.initComputation();
		todo = reasoner.ontologyIndex.getIndexedPropertyChains().iterator();
		maxSaturations_ = reasoner.ontologyIndex.getIndexedPropertyChains()
				.size();
		deletedSaturations_ = 0;
	}

	@Override
	public void printInfo() {
		if (deletedSaturations_ > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Saturations deleted:" + deletedSaturations_);
	}

}