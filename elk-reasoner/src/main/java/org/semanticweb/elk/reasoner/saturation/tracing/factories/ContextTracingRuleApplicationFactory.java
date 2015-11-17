/**
 * 
 */
package org.semanticweb.elk.reasoner.saturation.tracing.factories;

import org.semanticweb.elk.ModifiableReference;
import org.semanticweb.elk.Reference;

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

import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.MainContextFactory;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.AbstractClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionOccurrenceCheckingVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.LocalRuleApplicationClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.RelativizedContextReference;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubContextInitialization;
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
 * A {@link RuleApplicationFactory} that applies inference rules to
 * {@link ClassConclusion}s currently stored in the {@link Context}s of the main
 * {@link SaturationState}. This {@link SaturationState} is not modified. The
 * inferences producing the {@link ClassConclusion}s in this {@link SaturationState}
 * are produced using the supplied {@link ClassInferenceProducer}.
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
	
	private final SubContextInitialization.Factory factory_;

	public ContextTracingRuleApplicationFactory(
			SaturationState<?> mainSaturationState,
			ClassInferenceProducer inferenceProducer) {
		super(new MapSaturationState<ExtendedContext>(
				mainSaturationState.getOntologyIndex(),
				new MainContextFactory()));
		mainSaturationState_ = mainSaturationState;
		inferenceProducer_ = inferenceProducer;
		factory_ = new ConclusionBaseFactory();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected ClassConclusion.Visitor<Boolean> getConclusionProcessor(
			Reference<Context> activeContext,
			RuleVisitor<?> ruleVisitor,
			// this writer will block cyclic inferences
			SaturationStateWriter<? extends ExtendedContext> localWriter,
			SaturationStatistics localStatistics) {

		return SaturationUtils.compose(
		// Checking the conclusion against the main saturation
		// state
				new ClassConclusionOccurrenceCheckingVisitor(
						new RelativizedContextReference(activeContext,
								mainSaturationState_)),
				// if the conclusion was indeed derived, save its inference
				new InferenceProducingVisitor(),
				// insert the conclusion into the local context copies
				new ClassConclusionInsertionVisitor(activeContext, localWriter),
				// if the conclusion is new, apply local rules and produce
				// conclusions to the active (local) saturation state
				new LocalRuleApplicationClassConclusionVisitor(mainSaturationState_,
						activeContext, ruleVisitor, localWriter)
				);
	}

	@Override
	protected InputProcessor<RuleApplicationInput> getEngine(
			ModifiableReference<Context> activeContext,
			ClassConclusion.Visitor<Boolean> conclusionProcessor,
			final SaturationStateWriter<? extends ExtendedContext> saturationStateWriter,
			WorkerLocalTodo localTodo, SaturationStatistics localStatistics) {
		final InputProcessor<RuleApplicationInput> defaultEngine = super
				.getEngine(activeContext, conclusionProcessor, saturationStateWriter,
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
							.produce(factory_.getSubContextInitialization(root,
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
			AbstractClassConclusionVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(final ClassConclusion conclusion) {
			if (conclusion instanceof ClassInference) {
				inferenceProducer_.produce((ClassInference) conclusion);
			}
			return true;
		}
	}

}
