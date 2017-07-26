/*-
 * #%L
 * ELK Proofs Package
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2017 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.proofs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.puli.BaseProof;
import org.liveontologies.puli.Delegator;
import org.liveontologies.puli.GenericProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.Inferences;
import org.liveontologies.puli.ModifiableProof;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentProof;
import org.semanticweb.elk.reasoner.entailments.model.HasReason;
import org.semanticweb.elk.reasoner.query.EntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.ProperEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedIndexingEntailmentQueryResult;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.tracing.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.reasoner.tracing.TracingInferencePremiseVisitor;
import org.semanticweb.elk.reasoner.tracing.TracingProof;
import org.semanticweb.elk.util.collections.ArrayHashSet;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

/**
 * Combines {@link org.semanticweb.elk.reasoner.tracing.TracingProof
 * TracingProof} and {@link EntailmentProof} into a {@link Proof} over common
 * conclusions and justifies the {@link TracingInference}s with their axioms.
 * 
 * @author Peter Skocovsky
 */
public class InternalProof implements Proof<Object>,
		InferenceJustifier<Object, Set<? extends ElkAxiom>>,
		EntailmentQueryResult.Visitor<Void, ElkException> {

	private final Reasoner reasoner_;
	private final ElkAxiom goal_;

	private final ModifiableProof<Object, Inference<Object>> proof_ = new BaseProof<Object, Inference<Object>>();

	public InternalProof(final Reasoner reasoner, final ElkAxiom goal)
			throws ElkException {
		this.reasoner_ = reasoner;
		this.goal_ = goal;

		reasoner.isEntailed(goal).accept(this);

	}

	@Override
	public Void visit(final ProperEntailmentQueryResult properResult)
			throws ElkException {
		try {
			final Entailment entailment = properResult.getEntailment();
			proof_.produce(Inferences.create("Goal inference", goal_,
					Arrays.asList(entailment)));
			processEntailment(entailment, properResult.getEvidence(false));
			return null;
		} finally {
			properResult.unlock();
		}
	}

	@Override
	public Void visit(
			final UnsupportedIndexingEntailmentQueryResult unsupportedIndexing)
			throws ElkException {
		/*
		 * Incomplete result, no proof can be provided. The warning should be
		 * logged during loading.
		 */
		return null;
	}

	@Override
	public Void visit(
			final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryType)
			throws ElkException {
		throw new ElkRuntimeException(
				"Cannot check entailment: " + unsupportedQueryType.getQuery());
	}

	private void processEntailment(final Entailment goal,
			final EntailmentProof evidence) throws ElkException {

		final List<TracingProof> tracingProofs = new ArrayList<TracingProof>();

		final Set<Entailment> entailmentDone = new ArrayHashSet<Entailment>();
		final Queue<Entailment> entailmentToDo = new LinkedList<Entailment>();
		final Set<Conclusion> tracingDone = new ArrayHashSet<Conclusion>();
		final Queue<Conclusion> tracingToDo = new LinkedList<Conclusion>();

		if (entailmentDone.add(goal)) {
			entailmentToDo.add(goal);
		}

		Entailment entailment;
		while ((entailment = entailmentToDo.poll()) != null) {
			for (final EntailmentInference inf : evidence
					.getInferences(entailment)) {

				proof_.produce(Inferences.transform(inf, Functions.identity()));
				final Conclusion reason = getReason(inf);
				if (reason != null) {
					if (tracingDone.add(reason)) {
						tracingToDo.add(reason);
						tracingProofs.add(reasoner_.explainConclusion(reason));
					}
				}

				for (final Entailment premise : inf.getPremises()) {
					if (entailmentDone.add(premise)) {
						entailmentToDo.add(premise);
					}
				}

			}
		}

		/*
		 * TODO: Change tracing caching is finished.
		 * 
		 * This is to avoid completing tracing stage each time tracing
		 * inferences are requested. After tracing caching is finished, tracing
		 * stage will not be completed if the inferences are cached, so
		 * reasoner_.explainConclusion() can be called on demand.
		 */
		final GenericProof<Conclusion, TracingInference> tracingProof = Proofs
				.union(tracingProofs);

		Conclusion conclusion;
		while ((conclusion = tracingToDo.poll()) != null) {
			for (final TracingInference inf : tracingProof
					.getInferences(conclusion)) {

				proof_.produce(new TracingInferenceWrap(inf));

				for (final Conclusion premise : inf.getPremises()) {
					if (tracingDone.add(premise)) {
						tracingToDo.add(premise);
					}
				}

			}
		}

	}

	private Conclusion getReason(
			final EntailmentInference entailmentInference) {
		Conclusion reason = null;
		if (entailmentInference instanceof HasReason) {
			final Object r = ((HasReason<?>) entailmentInference).getReason();
			if (r instanceof Conclusion) {
				reason = (Conclusion) r;
			}
		}
		return reason;
	}

	private class TracingInferenceWrap extends Delegator<TracingInference>
			implements Inference<Object> {

		public TracingInferenceWrap(final TracingInference inference) {
			super(inference);
		}

		@Override
		public String getName() {
			return getDelegate().getName();
		}

		@Override
		public Object getConclusion() {
			return getDelegate().getConclusion();
		}

		@Override
		public List<? extends Object> getPremises() {
			return Lists.transform(getDelegate().getPremises(),
					Functions.identity());
		}

	}

	@Override
	public Collection<? extends Inference<Object>> getInferences(
			final Object conclusion) {
		return proof_.getInferences(conclusion);
	}

	@Override
	public Set<? extends ElkAxiom> getJustification(
			final Inference<Object> inference) {
		if (!(inference instanceof TracingInferenceWrap)) {
			return Collections.emptySet();
		}
		// else
		final TracingInferenceWrap tracingInference = (TracingInferenceWrap) inference;
		final Set<ElkAxiom> result = new HashSet<ElkAxiom>();
		tracingInference.getDelegate()
				.accept(new TracingInferencePremiseVisitor<Void>(
						CONCLUSION_FACTORY_, DUMMY_CONCLUSION_VISITOR_,
						new AxiomCollector(result)));
		return result;
	}

	private static final Conclusion.Factory CONCLUSION_FACTORY_ = new ConclusionBaseFactory();

	private static final Conclusion.Visitor<Void> DUMMY_CONCLUSION_VISITOR_ = new DummyConclusionVisitor<Void>();

	private class AxiomCollector extends DummyElkAxiomVisitor<Void> {

		private final Collection<ElkAxiom> axioms_;

		public AxiomCollector(final Collection<ElkAxiom> axioms) {
			this.axioms_ = axioms;
		}

		@Override
		protected Void defaultLogicalVisit(final ElkAxiom axiom) {
			axioms_.add(axiom);
			return null;
		}

	}

}
