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
package org.semanticweb.elk.reasoner.saturation;

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedClassExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;

/**
 * A specialization of {@link AbstractContextSaturationFactory} for saturating
 * class expressions. It guarantees that after each job finishes, all subsumers
 * of the context's root will be computed.
 * 
 * @author Frantisek Simancik
 * @author "Yevgeny Kazakov"
 * 
 * @param <J>
 *            the type of the saturation jobs that can be processed by this
 *            saturation engine
 * @see ClassExpressionSaturationListener
 * 
 */
public class ClassExpressionSaturationFactory<J extends SaturationJob<? extends IndexedClassExpression>>
		extends AbstractContextSaturationFactory<J, RuleApplicationInput> {

	/**
	 * Creates a new {@link ClassExpressionSaturationFactory} using the given
	 * {@link RuleApplicationFactory}for applying the rules, the maximal number
	 * of workers that can apply the rules concurrently, and
	 * {@link ClassExpressionSaturationListener} for reporting finished
	 * saturation jobs.
	 * 
	 * saturation state, listener for callback functions, and threshold for the
	 * number of unprocessed contexts.
	 * 
	 * @param ruleAppFactory
	 *            specifies how the rules are applied to new {@link Conclusion}s
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 * @param listener
	 *            the listener object implementing callback functions
	 */
	public ClassExpressionSaturationFactory(
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory, int maxWorkers,
			ClassExpressionSaturationListener<J> listener) {
		super(ruleAppFactory, maxWorkers, listener);
	}

	/**
	 * Creates a new {@link ClassExpressionSaturationFactory} using the given
	 * {@link RuleApplicationFactory}for applying the rules and the maximal
	 * number of workers that can apply the rules concurrently.
	 * 
	 * @param ruleAppFactory
	 * @param maxWorkers
	 *            the maximum number of workers that can use this factory
	 */
	public ClassExpressionSaturationFactory(
			RuleApplicationFactory<?, RuleApplicationInput> ruleAppFactory, int maxWorkers) {
		/* we use a dummy listener */
		this(ruleAppFactory, maxWorkers,
				new ClassExpressionSaturationListener<J>() {

					@Override
					public void notifyFinished(J job)
							throws InterruptedException {
						// dummy listener does not do anything
					}
				});
	}
	
	
	@Override
	protected RuleApplicationInput createRuleApplicationInput(J saturationJob) {
		return new RuleApplicationInput(saturationJob.getInput());
	}

	@Override
	protected boolean isJobFinished(J saturationJob, Context jobContext) {
		return jobContext != null && jobContext.isInitialized() && jobContext.isSaturated();
	}

}
