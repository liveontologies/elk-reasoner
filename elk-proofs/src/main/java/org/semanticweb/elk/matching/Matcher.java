/*
 * #%L
 * ELK Proofs Package
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
package org.semanticweb.elk.matching;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferenceOptimizedProducingFactory;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkIndividual;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkSubObjectPropertyExpression;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassInconsistency;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubPropertyChain;
import org.semanticweb.elk.reasoner.tracing.TracingProof;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Matcher {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(Matcher.class);

	private final Queue<InferenceMatch> toDoInferences_;

	private final Queue<ConclusionMatch> toDoConclusions_;

	private final ConclusionMatch.Visitor<Void> conclusionMatcher_;

	private final InferenceMatch.Visitor<Void> inferenceMatcher_;

	private final ConclusionMatchExpressionFactory conclusionMatchFactory_;

	public Matcher(TracingProof inferences, ElkObject.Factory elkObjectFactory,
			ElkInference.Factory elkInferenceFactory) {
		toDoInferences_ = new LinkedList<InferenceMatch>();
		toDoConclusions_ = new LinkedList<ConclusionMatch>();
		InferenceMatch.Factory inferenceMatchFactory = new InferenceMatchBufferringFactory(
				toDoInferences_);
		ConclusionMatchHierarchyImpl hierarchy = new ConclusionMatchHierarchyImpl();
		conclusionMatchFactory_ = new ConclusionMatchExpressionRecycleFactory(
				elkObjectFactory, toDoConclusions_, hierarchy);
		InferenceMatchMapImpl matchedInferences = new InferenceMatchMapImpl(
				inferences);
		conclusionMatcher_ = new ConclusionMatcherVisitor(inferenceMatchFactory,
				matchedInferences);
		inferenceMatcher_ = new InferenceMatchVisitor(matchedInferences,
				hierarchy, conclusionMatchFactory_, inferenceMatchFactory,
				elkInferenceFactory);
	}

	public Matcher(TracingProof inferences, ElkObject.Factory elkObjectFactory,
			ElkInferenceProducer elkInferenceProducer) {
		this(inferences, elkObjectFactory,
				new ElkInferenceOptimizedProducingFactory(elkInferenceProducer,
						elkObjectFactory));
	}

	public void trace(SubClassInclusionComposed conclusion,
			ElkClassExpression subClassMatch,
			ElkClassExpression superClassMatch) {
		conclusionMatchFactory_.getSubClassInclusionComposedMatch1(conclusion,
				conclusionMatchFactory_
						.getIndexedContextRootClassExpressionMatch(
								subClassMatch),
				superClassMatch);
		process();
	}

	public void trace(ClassInconsistency conclusion,
			ElkClassExpression inconsistent) {
		conclusionMatchFactory_.getClassInconsistencyMatch1(conclusion,
				conclusionMatchFactory_
						.getIndexedContextRootClassExpressionMatch(
								inconsistent));
		process();
	}

	public void trace(ClassInconsistency conclusion,
			ElkIndividual inconsistent) {
		conclusionMatchFactory_.getClassInconsistencyMatch1(conclusion,
				conclusionMatchFactory_
						.getIndexedContextRootClassExpressionMatch(
								conclusionMatchFactory_
										.getObjectOneOf(Collections
												.singletonList(inconsistent))));
		process();
	}

	public void trace(final SubPropertyChain conclusion,
			final ElkSubObjectPropertyExpression subChainMatch,
			final ElkSubObjectPropertyExpression superPropertyMatch) {
		conclusionMatchFactory_
				.getSubPropertyChainMatch2(
						conclusionMatchFactory_.getSubPropertyChainMatch1(
								conclusion, superPropertyMatch, 0),
						subChainMatch, 0);
		process();
	}

	private void process() {
		for (;;) {
			ConclusionMatch conclusion = toDoConclusions_.poll();
			if (conclusion != null) {
				LOGGER_.trace("{}: process", conclusion);
				conclusion.accept(conclusionMatcher_);
				continue;
			}
			InferenceMatch inference = toDoInferences_.poll();
			if (inference != null) {
				LOGGER_.trace("{}: process", inference);
				inference.accept(inferenceMatcher_);
				continue;
			}
			return;
		}

	}

}
