/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.MainContextFactory;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.implementation.SubContextInitializationImpl;
import org.semanticweb.elk.reasoner.saturation.conclusions.interfaces.Conclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.AbstractConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.ConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.HybridLocalRuleApplicationConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.visitors.LocalizedConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.reasoner.saturation.rules.factories.WorkerLocalTodo;
import org.semanticweb.elk.reasoner.saturation.tracing.ClassInferenceProducer;
import org.semanticweb.elk.util.concurrent.computation.DelegatingInputProcessor;
import org.semanticweb.elk.util.concurrent.computation.InputProcessor;

/**
 * A {@link RuleApplicationFactory} that applies redundant and non-redundant
 * rules to {@link Conclusion}s currently stored in the {@link Context}s of the
 * main {@link SaturationState}. This {@link SaturationState} is not modified.
 * The inferences producing the {@link Conclusion}s in this
 * {@link SaturationState} are produced using the supplied
 * {@link ClassInferenceProducer}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextTracingRuleApplicationFactory extends
		AbstractRuleApplicationFactory<ExtendedContext, RuleApplicationInput> {

	private final SaturationState<?> mainSaturationState_;

	private final ClassInferenceProducer inferenceProducer_;

	public ContextTracingRuleApplicationFactory(
			SaturationState<?> mainSaturationState,
			ClassInferenceProducer inferenceProducer) {
		super(new MapSaturationState<ExtendedContext>(
				mainSaturationState.getOntologyIndex(),
				new MainContextFactory()));
		mainSaturationState_ = mainSaturationState;
		inferenceProducer_ = inferenceProducer;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ConclusionVisitor<? super Context, Boolean> getConclusionProcessor(
			RuleVisitor ruleVisitor,
			// this writer will block cyclic inferences
			SaturationStateWriter<? extends ExtendedContext> localWriter,
			SaturationStatistics localStatistics) {

		return SaturationUtils.compose(
		// Checking the conclusion against the main saturation
		// state
				new LocalizedConclusionVisitor(
						new ConclusionOccurrenceCheckingVisitor(),
						mainSaturationState_),
				// if the conclusion was indeed derived, save its inference
				new InferenceProducingVisitor(),
				// insert the conclusion into the local context copies
				// new ConclusionInitializingInsertionVisitor(localWriter),
				new ConclusionInsertionVisitor(localWriter),
				// if the conclusion is new, apply local (non-redundant +
				// redundnat) rules and produce conclusions to the active
				// (local) saturation state
				new HybridLocalRuleApplicationConclusionVisitor(
						mainSaturationState_, ruleVisitor, ruleVisitor,
						localWriter, localWriter));
	}

	@Override
	protected InputProcessor<RuleApplicationInput> getEngine(
			ConclusionVisitor<? super Context, Boolean> conclusionProcessor,
			final SaturationStateWriter<? extends ExtendedContext> saturationStateWriter,
			WorkerLocalTodo localTodo, SaturationStatistics localStatistics) {
		final InputProcessor<RuleApplicationInput> defaultEngine = super
				.getEngine(conclusionProcessor, saturationStateWriter,
						localTodo, localStatistics);
		return new DelegatingInputProcessor<RuleApplicationInput>(defaultEngine) {
			@Override
			public void submit(RuleApplicationInput job) {
				defaultEngine.submit(job);
				// additionally initialize all sub-contexts present in the main
				// saturation state
				IndexedContextRoot root = job.getRoot();
				Context mainContext = mainSaturationState_.getContext(root);
				for (IndexedObjectProperty subRoot : mainContext
						.getSubContextPremisesByObjectProperty().keySet()) {
					saturationStateWriter
							.produce(new SubContextInitializationImpl(root,
									subRoot));
				}
			}
		};
	}

	/**
	 * First, writes the new inference for the conclusion, second, inserts that
	 * conclusion into the context.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class InferenceProducingVisitor extends
			AbstractConclusionVisitor<Context, Boolean> {

		@Override
		protected Boolean defaultVisit(final Conclusion conclusion,
				final Context cxt) {
			if (conclusion instanceof ClassInference) {
				inferenceProducer_.produce((ClassInference) conclusion);
			}
			return true;
		}
	}

}
