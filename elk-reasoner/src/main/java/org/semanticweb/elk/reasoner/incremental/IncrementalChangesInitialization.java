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
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.util.collections.LazySetIntersection;
import org.semanticweb.elk.util.concurrent.computation.BaseInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.ComputationExecutor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessorFactory;

/**
 * Goes through the input class expressions and puts each context's superclass
 * for which there're changes into the ToDo queue
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class IncrementalChangesInitialization
		extends
		ReasonerComputation<IndexedClassExpression, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<IndexedClassExpression> inputs,
			ChainableRule<Context> changedGlobalRules,
			Map<IndexedClassExpression, ChainableRule<Context>> changes,
			SaturationState state, boolean expectAllContextsSaturated,
			ComputationExecutor executor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ContextInitializationFactory(state, changes,
				changedGlobalRules, expectAllContextsSaturated), executor,
				maxWorkers, progressMonitor);
	}
}

class ContextInitializationFactory
		implements
		InputProcessorFactory<IndexedClassExpression, InputProcessor<IndexedClassExpression>> {

	// private static final Logger LOGGER_ =
	// Logger.getLogger(ContextInitializationFactory.class);

	private final SaturationState.Writer saturationStateWriter_;
	private final Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges_;
	private final LinkRule<Context> changedGlobalRuleHead_;
	private final boolean expectAllContextsSaturated_;

	public ContextInitializationFactory(
			SaturationState state,
			Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges,
			LinkRule<Context> changedGlobalRuleHead,
			boolean expectAllContextsSaturated) {
		saturationStateWriter_ = state.getWriter();
		indexChanges_ = indexChanges;
		changedGlobalRuleHead_ = changedGlobalRuleHead;
		expectAllContextsSaturated_ = expectAllContextsSaturated;
	}

	@Override
	public InputProcessor<IndexedClassExpression> getEngine() {

		return new BaseInputProcessor<IndexedClassExpression>() {

			@Override
			protected void process(IndexedClassExpression ice) {
				Context context = ice.getContext();

				if (context != null) {

					if (expectAllContextsSaturated_ && !context.isSaturated()) {
						/*
						 * If we expect that all contexts are saturated at the
						 * beginning of this phase (e.g. we ran the completion
						 * phase before) but the flag isn't set for some
						 * context, then we set it
						 */
						context.setSaturated(true);
					}

					LinkRule<Context> nextGlobalRule = changedGlobalRuleHead_;
					while (nextGlobalRule != null) {
						// apply all changed global context rules
						nextGlobalRule.apply(saturationStateWriter_, context);
						nextGlobalRule = nextGlobalRule.next();
					}

					for (IndexedClassExpression changedICE : new LazySetIntersection<IndexedClassExpression>(
							indexChanges_.keySet(),
							context.getSuperClassExpressions())) {
						// applying the changed rules for this class expression
						LinkRule<Context> nextLocalRule = indexChanges_
								.get(changedICE);
						while (nextLocalRule != null) {
							nextLocalRule
									.apply(saturationStateWriter_, context);
							nextLocalRule = nextLocalRule.next();
						}
					}
				}
			}
		};
	}

	@Override
	public void finish() {
	}

}