/**
 * 
 */
package org.semanticweb.elk.reasoner.incremental;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
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

import java.util.Collection;
import java.util.Map;

import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.IncrementalContextRuleChain;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ContextRules;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * Goes through the input class expressions and puts
 * each context's superclass for which there're changes into
 * the ToDo queue
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IncrementalChangesInitialization
		extends
		ReasonerComputation<IndexedClassExpression, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<IndexedClassExpression> inputs,
			Map<IndexedClassExpression, IncrementalContextRuleChain> changes,
			SaturationState state,
			ComputationExecutor executor,
			int maxWorkers,
			ProgressMonitor progressMonitor,
			ContextRules changedGlobalRules) {
		super(inputs, new ContextInitializationFactory(state, changes, changedGlobalRules), executor, maxWorkers, progressMonitor);
	}
}


class ContextInitializationFactory implements InputProcessorFactory<IndexedClassExpression, InputProcessor<IndexedClassExpression>> {

	//private static final Logger LOGGER_ = Logger.getLogger(ContextInitializationFactory.class);	
	
	private final SaturationState saturationState_;
	private final Map<IndexedClassExpression, IncrementalContextRuleChain> indexChanges_;
	private final ContextRules changedGlobalRules_;
	
	public ContextInitializationFactory(SaturationState state,
			Map<IndexedClassExpression,
			IncrementalContextRuleChain> indexChanges,
			ContextRules changedGlobalRules) {
		saturationState_ = state;
		indexChanges_ = indexChanges;
		changedGlobalRules_ = changedGlobalRules;
	}

	@Override
	public InputProcessor<IndexedClassExpression> getEngine() {

		return new BaseInputProcessor<IndexedClassExpression>() {
			
			@Override
			protected void process(IndexedClassExpression ice) {
				Context context = ice.getContext();
				
				if (context != null) {
					
					if (!context.isSaturated()) {
						//FIXME replace by a stage which will complete all context not saturated due to an interruption 
						saturationState_.markAsModified(context);
					}
					
					if (changedGlobalRules_ != null) {
						//apply changes in the global context rules
						saturationState_.produce(context, new IncrementalContextRuleChain(changedGlobalRules_));
					}
					
					for (IndexedClassExpression changedICE : new LazySetIntersection<IndexedClassExpression>(
							indexChanges_.keySet(),
							context.getSuperClassExpressions())) {
						IncrementalContextRuleChain change = indexChanges_.get(changedICE);
						// place the accumulated changes into the queue
						saturationState_.produce(context, change);
					}
				}
			}
		};
	}

	@Override
	public void finish() {}
	
}