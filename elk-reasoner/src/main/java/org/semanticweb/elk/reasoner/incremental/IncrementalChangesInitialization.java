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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.semanticweb.elk.reasoner.ProgressMonitor;
import org.semanticweb.elk.reasoner.ReasonerComputation;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.BasicSaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.ContextModificationListener;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.conclusions.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.ChainableRule;
import org.semanticweb.elk.reasoner.saturation.rules.LinkRule;
import org.semanticweb.elk.reasoner.saturation.rules.RuleApplicationVisitor;
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
public class IncrementalChangesInitialization extends
		ReasonerComputation<ArrayList<Context>, ContextInitializationFactory> {

	public IncrementalChangesInitialization(
			Collection<ArrayList<Context>> inputs,
			ChainableRule<Context> changedGlobalRules,
			Map<IndexedClassExpression, ChainableRule<Context>> changes,
			SaturationState state, ComputationExecutor executor,
			RuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?> conclusionVisitor, int maxWorkers,
			ProgressMonitor progressMonitor) {
		super(inputs, new ContextInitializationFactory(state, changes,
				changedGlobalRules, ruleAppVisitor, conclusionVisitor),
				executor, maxWorkers, progressMonitor);
	}
}

class ContextInitializationFactory
		implements
		InputProcessorFactory<ArrayList<Context>, InputProcessor<ArrayList<Context>>> {

	private static final Logger LOGGER_ = Logger
			.getLogger(ContextInitializationFactory.class);

	private final BasicSaturationStateWriter saturationStateWriter_;
	private final Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges_;
	private final IndexedClassExpression[] indexChangesKeys_;
	private final LinkRule<Context> changedGlobalRuleHead_;
	private final RuleApplicationVisitor ruleAppVisitor_;
	private AtomicInteger ruleHits = new AtomicInteger(0);

	public ContextInitializationFactory(
			SaturationState state,
			Map<IndexedClassExpression, ? extends LinkRule<Context>> indexChanges,
			LinkRule<Context> changedGlobalRuleHead,
			RuleApplicationVisitor ruleAppVisitor,
			ConclusionVisitor<?> conclusionVisitor) {
		saturationStateWriter_ = state.getWriter(
				ContextModificationListener.DUMMY, conclusionVisitor);
		indexChanges_ = indexChanges;
		indexChangesKeys_ = new IndexedClassExpression[indexChanges.keySet()
				.size()];
		indexChanges.keySet().toArray(indexChangesKeys_);
		changedGlobalRuleHead_ = changedGlobalRuleHead;
		ruleAppVisitor_ = ruleAppVisitor;
	}

	@Override
	public InputProcessor<ArrayList<Context>> getEngine() {

		return new BaseInputProcessor<ArrayList<Context>>() {

			int localRuleHits = 0;

			@Override
			protected void process(ArrayList<Context> input) {
				for (int i = 0; i < input.size(); i++) {
					Context context = input.get(i);
					// apply all changed global context rules
					LinkRule<Context> nextGlobalRule = changedGlobalRuleHead_;
					while (nextGlobalRule != null) {
						if (LOGGER_.isTraceEnabled())
							LOGGER_.trace(context + ": applying rule "
									+ nextGlobalRule.getName());
						nextGlobalRule.accept(ruleAppVisitor_,
								saturationStateWriter_, context);
						nextGlobalRule = nextGlobalRule.next();
					}

					// apply all changed rules for indexed class expressions
					Set<IndexedClassExpression> subsumers = context
							.getSubsumers();
					if (subsumers.size() > indexChangesKeys_.length >> 2) {
						// iterate over changes, check subsumers
						for (int j = 0; j < indexChangesKeys_.length; j++) {
							IndexedClassExpression changedICE = indexChangesKeys_[j];
							if (subsumers.contains(changedICE)) {
								applyLocalRules(context, changedICE);
							}
						}
					} else {
						// iterate over subsumers, check changes
						for (IndexedClassExpression changedICE : subsumers) {
							applyLocalRules(context, changedICE);
						}
					}

				}
			}

			private void applyLocalRules(Context context,
					IndexedClassExpression changedICE) {
				LinkRule<Context> nextLocalRule = indexChanges_.get(changedICE);
				if (nextLocalRule != null) {
					localRuleHits++;
					if (LOGGER_.isTraceEnabled())
						LOGGER_.trace(context + ": applying rules for "
								+ changedICE);
				}
				while (nextLocalRule != null) {
					nextLocalRule.accept(ruleAppVisitor_,
							saturationStateWriter_, context);
					nextLocalRule = nextLocalRule.next();
				}
			}

			@Override
			public void finish() {
				ruleHits.addAndGet(localRuleHits);

			}

		};
	}

	@Override
	public void finish() {
		// aggregatedStats_.add(localStatistics);
		if (LOGGER_.isDebugEnabled())
			LOGGER_.debug("Rule hits: " + ruleHits.get());
	}

}
