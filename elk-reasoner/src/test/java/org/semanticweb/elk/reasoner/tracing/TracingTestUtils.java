/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2014 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.tracing;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.liveontologies.puli.Proof;
import org.semanticweb.elk.MutableBoolean;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.HasReason;
import org.semanticweb.elk.reasoner.indexing.model.IndexedClassExpression;
import org.semanticweb.elk.reasoner.indexing.model.IndexedContextRoot;
import org.semanticweb.elk.reasoner.query.ElkQueryException;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;
import org.semanticweb.elk.reasoner.saturation.conclusions.classes.SaturationConclusionBaseFactory;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SaturationConclusion;
import org.semanticweb.elk.reasoner.saturation.context.Context;
import org.semanticweb.elk.reasoner.stages.ReasonerStateAccessor;
import org.semanticweb.elk.util.collections.ArrayHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of utility methods to test correctness of saturation tracing.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class TracingTestUtils {

	protected static final Logger LOGGER_ = LoggerFactory
			.getLogger(TracingTestUtils.class);

	private static final UntracedConclusionListener UNTRACED_LISTENER = new UntracedConclusionListener() {

		@Override
		public void notifyUntraced(Conclusion conclusion) {
			fail(conclusion + ": conclusion was not traced");
		}

	};

	private static final SaturationConclusion.Factory FACTORY_ = new SaturationConclusionBaseFactory();

	public static Collection<Conclusion> getDerivedConclusionsForSubsumption(
			final ElkClassExpression subClass,
			final ElkClassExpression superClass, final Reasoner reasoner)
			throws ElkException {

		final ElkObject.Factory elkFactory = reasoner.getElkFactory();

		final Collection<Conclusion> conclusions = new ArrayList<Conclusion>();

		final EntailmentQueryResult result = reasoner.isEntailed(
				elkFactory.getSubClassOfAxiom(subClass, superClass));
		result.accept(
				new EntailmentQueryResult.Visitor<Void, ElkQueryException>() {

					@Override
					public Void visit(final ProperEntailmentQueryResult result)
							throws ElkQueryException {
						try {
							collectReasons(result.getEntailment(),
									result.getEvidence(false), conclusions);
						} finally {
							result.unlock();
						}
						return null;
					}

					@Override
					public Void visit(
							final UnsupportedIndexingEntailmentQueryResult result) {
						throw new ElkRuntimeException(
								UnsupportedIndexingEntailmentQueryResult.class
										.getSimpleName());
					}

					@Override
					public Void visit(
							final UnsupportedQueryTypeEntailmentQueryResult result) {
						throw new ElkRuntimeException(
								UnsupportedQueryTypeEntailmentQueryResult.class
										.getSimpleName());
					}

				});

		return conclusions;
	}

	private static void collectReasons(final Entailment goal,
			final Proof<? extends EntailmentInference> evidence,
			final Collection<Conclusion> result) {

		final Set<Entailment> done = new ArrayHashSet<Entailment>();
		final Queue<Entailment> toDo = new LinkedList<Entailment>();

		if (done.add(goal)) {
			toDo.offer(goal);
		}

		Entailment entailment;
		while ((entailment = toDo.poll()) != null) {
			for (final EntailmentInference inf : evidence
					.getInferences(entailment)) {

				if (inf instanceof HasReason) {
					final Object reason = ((HasReason<?>) inf).getReason();
					if (reason instanceof Conclusion) {
						result.add((Conclusion) reason);
					}
				}

				for (final Entailment premise : inf.getPremises()) {
					if (done.add(premise)) {
						toDo.offer(premise);
					}
				}

			}
		}

	}

	static ClassConclusion getConclusionToTrace(Context context,
			IndexedClassExpression subsumer) {
		if (context != null) {
			IndexedContextRoot root = context.getRoot();
			ClassConclusion contradiction = FACTORY_.getContradiction(root);
			if (context.containsConclusion(contradiction)) {
				return contradiction;
			}

			return FACTORY_.getSubClassInclusionComposed(root, subsumer);
		}

		throw new IllegalArgumentException("Context may not be null");
	}

	public static void checkTracingCompleteness(ElkClassExpression subClass,
			ElkClassExpression superClass, final Reasoner reasoner)
			throws ElkException {
		final Collection<Conclusion> conclusions = getDerivedConclusionsForSubsumption(
				subClass, superClass, reasoner);
		if (conclusions.isEmpty()) {
			fail(subClass + " ⊑ " + superClass + " not provable!");
		}
		final TracingProof proof = reasoner.getProof();
		final AtomicInteger conclusionCount = new AtomicInteger(0);
		TracingInference.Visitor<Boolean> counter = new DummyInferenceChecker() {

			@Override
			protected Boolean defaultVisit(TracingInference inference) {
				conclusionCount.incrementAndGet();
				return true;
			}
		};

		TestTraceUnwinder explorer = new TestTraceUnwinder(proof,
				UNTRACED_LISTENER);
		for (final Conclusion conclusion : conclusions) {
			explorer.accept(conclusion, counter);
		}
	}

	/*
	 * Either the class inference visitor or the property inference visitor
	 * should return true for some used inference in the trace.
	 * 
	 * TODO Generalize this to look for multiple inferences in the trace.
	 */
	public static void checkConditionOverUsedInferences(ElkClassExpression sub,
			ElkClassExpression sup, Reasoner reasoner,
			final TracingInference.Visitor<Boolean> inferenceVisitor)
			throws ElkException {
		final IndexedClassExpression subsumee = ReasonerStateAccessor
				.transform(reasoner, sub);
		SaturationConclusion conclusion = getConclusionToTrace(
				ReasonerStateAccessor.getContext(reasoner, subsumee),
				ReasonerStateAccessor.transform(reasoner, sup));
		final MutableBoolean inferenceCondition = new MutableBoolean(false);
		final TracingProof proof = reasoner.getProof();

		new TestTraceUnwinder(proof, UNTRACED_LISTENER).accept(conclusion,
				new DummyInferenceChecker() {

					@Override
					protected Boolean defaultVisit(TracingInference inference) {
						inferenceCondition
								.or(inference.accept(inferenceVisitor));

						return true;
					}

				});

		assertTrue("The condition didn't succeed on any used inference",
				inferenceCondition.get());
	}

	public static String print(TracingProof inferences, Conclusion conclusion) {
		StringBuilder builder = new StringBuilder();
		print(inferences, conclusion, builder, new HashSet<Conclusion>(), 0);
		return builder.toString();
	}

	private static void printIndent(StringBuilder builder, int depth) {
		for (int i = 0; i < depth; i++) {
			builder.append("   ");
		}
	}

	private static void print(final TracingProof inferences,
			Conclusion conclusion, final StringBuilder builder,
			final Set<Conclusion> done, final int depth) {

		printIndent(builder, depth);

		builder.append(conclusion);

		if (done.add(conclusion)) {
			builder.append('\n');
		} else {
			builder.append("*\n");
			return;
		}

		for (TracingInference inf : inferences.getInferences(conclusion)) {
			for (int i = 0; i < depth + 1; i++) {
				builder.append("   ");
			}

			builder.append(inf.getClass().getSimpleName()).append('\n');

			inf.accept(new TracingInferencePremiseVisitor<Void>(
					new ConclusionBaseFactory(),
					new DummyConclusionVisitor<Void>() {
						@Override
						protected Void defaultVisit(Conclusion premise) {
							print(inferences, premise, builder, done,
									depth + 2);
							return null;
						}
					}, new DummyElkAxiomVisitor<Void>() {
						@Override
						protected Void defaultVisit(ElkAxiom axiom) {
							printIndent(builder, depth + 2);
							builder.append(": ");
							builder.append(axiom);
							builder.append('\n');
							return null;
						}
					}));
		}
	}

	// //////////////////////////////////////////////////////////////////////
	// some dummy utility visitors
	// //////////////////////////////////////////////////////////////////////
	static class DummyInferenceChecker
			extends TracingInferenceDummyVisitor<Boolean> {

		@Override
		protected Boolean defaultVisit(TracingInference inference) {
			return true;
		}

	}

	static final TracingInference.Visitor<Boolean> DUMMY_INFERENCE_CHECKER = new DummyInferenceChecker();

}
