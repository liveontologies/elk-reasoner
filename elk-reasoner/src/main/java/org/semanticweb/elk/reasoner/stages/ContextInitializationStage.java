/*
 * #%L
 * ELK Reasoner
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
package org.semanticweb.elk.reasoner.stages;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;

// TODO: add progress monitor, make concurrent if possible

/**
 * A {@link ReasonerStage} which purpose is to ensure that no context is
 * assigned to {@link IndexedClassExpression}s of the current ontology
 * 
 * @author "Yevgeny Kazakov"
 * 
 */
class ContextInitializationStage extends AbstractReasonerStage {

	// logger for this class
	private static final Logger LOGGER_ = Logger
			.getLogger(ContextInitializationStage.class);

	/**
	 * The counter for deleted contexts
	 */
	private int deletedContexts_;
	/**
	 * The number of contexts
	 */
	private int maxContexts_;

	/**
	 * The state of the iterator of the input to be processed
	 */
	private Iterator<Context> todo_ = null;

	public ContextInitializationStage(AbstractReasonerState reasoner,
			AbstractReasonerStage... preStages) {
		super(reasoner, preStages);
	}

	@Override
	public String getName() {
		return "Context Initialization";
	}

	@Override
	public boolean preExecute() {
		if (!super.preExecute())
			return false;
		todo_ = reasoner.saturationState.getContexts().iterator();
		// upper limit
		maxContexts_ = reasoner.ontologyIndex.getIndexedClassExpressions()
				.size();
		deletedContexts_ = 0;
		return true;
	}

	@Override
	public void executeStage() throws ElkInterruptedException {
		for (;;) {
			checkInterrupt();
			if (!todo_.hasNext())
				return;
			Context context = todo_.next();
			context.getRoot().resetContext();
			deletedContexts_++;
			progressMonitor.report(deletedContexts_, maxContexts_);
		}
	}

	@Override
	public boolean postExecute() {
		if (!super.postExecute())
			return false;
		// reasoner.saturationState.resetFirstContext();
		reasoner.saturationState.getWriter(ConclusionVisitor.DUMMY)
				.resetContexts();
		todo_ = null;
		return true;
	}

	@Override
	public void printInfo() {
		if (deletedContexts_ > 0 && LOGGER_.isDebugEnabled())
			LOGGER_.debug("Contexts deleted:" + deletedContexts_);
	}

}