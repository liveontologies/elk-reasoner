package org.semanticweb.elk.matching;

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

import java.util.LinkedList;
import java.util.Queue;

import org.semanticweb.elk.matching.conclusions.ConclusionMatch;
import org.semanticweb.elk.matching.conclusions.ConclusionMatchExpressionFactory;
import org.semanticweb.elk.matching.conclusions.IndexedClassExpressionMatch;
import org.semanticweb.elk.matching.inferences.InferenceMatch;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducer;
import org.semanticweb.elk.owl.inferences.ElkInferenceProducingFactory;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClass;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.semanticweb.elk.reasoner.tracing.InferenceSet;
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

	public Matcher(InferenceSet inferences, ElkObject.Factory elkObjectFactory,
			ElkInferenceProducer elkInferenceProducer) {
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
				new ElkInferenceProducingFactory(elkInferenceProducer));
	}

	public void trace(SubClassInclusionComposed conclusion) {
		IndexedContextRoot subExpression = conclusion.getDestination();
		IndexedClassExpression superExpression = conclusion.getSubsumer();
		if (subExpression instanceof IndexedClass
				&& superExpression instanceof IndexedClass) {
			conclusionMatchFactory_.getSubClassInclusionComposedMatch1(
					conclusion, getMatch((IndexedClass) subExpression),
					((IndexedClass) superExpression).getElkEntity());
			process();
		}

	}

	private IndexedClassExpressionMatch getMatch(IndexedClass indexedClass) {
		return conclusionMatchFactory_
				.getIndexedClassExpressionMatch(indexedClass.getElkEntity());
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
