/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing.factories;

/*-
 * #%L
 * ELK Reasoner Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2016 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.Reference;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.indexing.model.IndexedObjectProperty;
import org.semanticweb.elk.reasoner.saturation.ExtendedContext;
import org.semanticweb.elk.reasoner.saturation.MainContextFactory;
import org.semanticweb.elk.reasoner.saturation.MapSaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationState;
import org.semanticweb.elk.reasoner.saturation.SaturationStateWriter;
import org.semanticweb.elk.reasoner.saturation.SaturationStatistics;
import org.semanticweb.elk.reasoner.saturation.SaturationUtils;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.ClassConclusionInsertionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.DummyClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.TracingRuleApplicationClassConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ContextInitialization;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInference.Visitor;
import org.semanticweb.elk.reasoner.saturation.inferences.ClassInferenceConclusionVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.DummyClassInferenceVisitor;
import org.semanticweb.elk.reasoner.saturation.inferences.SubContextInitializationNoPremises;
import org.semanticweb.elk.reasoner.saturation.rules.AllInferencesRuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.RuleStatistics;
import org.semanticweb.elk.reasoner.saturation.rules.RuleVisitor;
import org.semanticweb.elk.reasoner.saturation.rules.factories.AbstractRuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationFactory;
import org.semanticweb.elk.reasoner.saturation.rules.factories.RuleApplicationInput;
import org.semanticweb.elk.reasoner.tracing.TracingInferenceProducer;

/**
 * A {@link RuleApplicationFactory} that applies inference rules to
 * {@link ClassConclusion}s currently stored in the {@link Context}s of the main
 * {@link SaturationState}. This {@link SaturationState} is not modified. The
 * inferences producing the {@link ClassConclusion}s in this
 * {@link SaturationState} are produced using the supplied
 * {@link TracingInferenceProducer}.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * 
 * @author "Yevgeny Kazakov"
 */
public class ContextTracingRuleApplicationFactory
		extends
			AbstractRuleApplicationFactory<ExtendedContext, RuleApplicationInput> {

	private final SaturationState<?> mainSaturationState_;

	private final TracingInferenceProducer<ClassInference> inferenceProducer_;

	public ContextTracingRuleApplicationFactory(
			SaturationState<?> mainSaturationState,
			TracingInferenceProducer<ClassInference> inferenceProducer) {
		super(new MapSaturationState<ExtendedContext>(
				mainSaturationState.getOntologyIndex(),
				new MainContextFactory()));
		mainSaturationState_ = mainSaturationState;
		inferenceProducer_ = inferenceProducer;
	}

	@Override
	protected RuleVisitor<?> getRuleVisitor(RuleStatistics statistics) {
		// a rule visitor that also produces redundant inferences
		// to make sure that all proofs are found
		return new AllInferencesRuleVisitor();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Visitor<Boolean> getInferenceProcessor(
			Reference<Context> activeContext, RuleVisitor<?> ruleVisitor,
			final SaturationStateWriter<? extends ExtendedContext> localWriter,
			SaturationStatistics localStatistics) {

		return SaturationUtils.compose(
				// save inference
				new InferenceProducingVisitor(),
				// process conclusions of inferences:
				new ClassInferenceConclusionVisitor<Boolean>(
						SaturationUtils.compose(
								// insert the conclusion into the local context
								// copies
								new ClassConclusionInsertionVisitor(
										activeContext, localWriter),
								// if the conclusion is new, apply local rules
								// and produce conclusions to the active (local)
								// saturation state
								new TracingRuleApplicationClassConclusionVisitor(
										mainSaturationState_, activeContext,
										ruleVisitor, localWriter),
								// initializing all sub-contexts when contexts are initialized
								// this is needed to ensure that all sub-contexts are fully traced
								new DummyClassConclusionVisitor<Boolean>() {
									@Override
									protected Boolean defaultVisit(ClassConclusion conclusion) {
										return true;
									}									
									@Override
									public Boolean visit(ContextInitialization conclusion) {
										IndexedContextRoot root = conclusion.getTraceRoot();
										Context mainContext = mainSaturationState_.getContext(root);
										for (IndexedObjectProperty subRoot : mainContext
												.getSubContextPremisesByObjectProperty().keySet()) {
											localWriter.produce(
													new SubContextInitializationNoPremises(root,
															subRoot));
										}
										return true;
									}
								}								
								)));
	}

	/**
	 * First, writes the new inference for the conclusion, second, inserts that
	 * conclusion into the context.
	 * 
	 * @author Pavel Klinov
	 * 
	 *         pavel.klinov@uni-ulm.de
	 */
	private class InferenceProducingVisitor
			extends
				DummyClassInferenceVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(final ClassInference inference) {
			inferenceProducer_.produce(inference);
			return true;
		}
	}

}
