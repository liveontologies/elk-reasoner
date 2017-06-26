/*-
 * #%L
 * ELK OWL API Binding
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
package org.semanticweb.elk.owlapi.proofs;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.puli.BaseInference;
import org.liveontologies.puli.BaseProof;
import org.liveontologies.puli.Delegator;
import org.liveontologies.puli.GenericProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.liveontologies.puli.ModifiableProof;
import org.liveontologies.puli.Proof;
import org.liveontologies.puli.Proofs;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
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
import org.semanticweb.elk.util.collections.Operations;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

/**
 * Combines {@link org.semanticweb.elk.reasoner.tracing.TracingProof
 * TracingProof} and {@link EntailmentProof} into a {@link Proof} over common
 * conclusions and justifies the {@link TracingInference}s with their axioms
 * converted to {@link OWLAxiom}s.
 * 
 * @author Peter Skocovsky
 */
public class TracingProofAdapter
		implements Proof<TracingProofAdapter.ConclusionAdapter>,
		InferenceJustifier<TracingProofAdapter.ConclusionAdapter, Set<? extends OWLAxiom>> {

	private final OwlConverter owlConverter_ = OwlConverter.getInstance();
	private final ElkConverter elkConverter_ = ElkConverter.getInstance();

	private final Reasoner reasoner_;
	private GenericProof<Conclusion, TracingInference> tracingProof_;
	private final ConclusionAdapter convertedQuery_;
	private final ModifiableProof<ConclusionAdapter, Inference<ConclusionAdapter>> entailmentProof_ = new BaseProof<ConclusionAdapter, Inference<ConclusionAdapter>>();

	public TracingProofAdapter(final Reasoner reasoner, final OWLAxiom query) {
		this.reasoner_ = reasoner;
		try {

			final ElkAxiom elkAxiom = owlConverter_.convert(query);
			this.convertedQuery_ = reasoner.isEntailed(elkAxiom)
					.accept(entailmentQueryResultVisitor_);

		} catch (final ElkException e) {
			throw elkConverter_.convert(e);
		} catch (final ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	private final EntailmentQueryResult.Visitor<ConclusionAdapter, ElkException> entailmentQueryResultVisitor_ = new EntailmentQueryResult.Visitor<ConclusionAdapter, ElkException>() {

		@Override
		public ConclusionAdapter visit(
				final ProperEntailmentQueryResult properResult)
				throws ElkException {
			try {
				final Entailment entailment = properResult.getEntailment();
				convertEntailmentInferences(entailment,
						properResult.getEvidence(false));
				return new EntailmentConclusion(entailment);
			} finally {
				properResult.unlock();
			}
		}

		@Override
		public ConclusionAdapter visit(
				final UnsupportedIndexingEntailmentQueryResult unsupportedIndexing)
				throws ElkException {
			/*
			 * Incomplete result, no proof can be provided. The warning should
			 * be logged during loading.
			 */
			return new DummyConclusion();
		}

		@Override
		public ConclusionAdapter visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryType)
				throws ElkException {
			throw new UnsupportedEntailmentTypeException(
					elkConverter_.convert(unsupportedQueryType.getQuery()));
		}

	};

	private void convertEntailmentInferences(final Entailment goal,
			final EntailmentProof evidence) {

		final List<TracingProof> tracingProofs = new ArrayList<TracingProof>();

		final Set<Entailment> done = new ArrayHashSet<Entailment>();
		final Queue<Entailment> toDo = new LinkedList<Entailment>();

		if (done.add(goal)) {
			toDo.add(goal);
		}

		Entailment entailment;
		while ((entailment = toDo.poll()) != null) {
			for (final EntailmentInference inf : evidence
					.getInferences(entailment)) {

				entailmentProof_.produce(wrapEntailmentInference(inf));
				final Conclusion reason = getReason(inf);
				if (reason != null) {
					try {
						tracingProofs.add(reasoner_.explainConclusion(reason));
					} catch (final ElkException e) {
						throw new ElkRuntimeException(e);
					}
				}

				for (final Entailment premise : inf.getPremises()) {
					if (done.add(premise)) {
						toDo.add(premise);
					}
				}

			}
		}

		/*
		 * TODO: Remove after tracing caching is finished.
		 * 
		 * This is to avoid completing tracing stage each time tracing
		 * inferences are requested. After tracing caching is finished, tracing
		 * stage will not be completed if the inferences are cached, so
		 * reasoner_.explainConclusion() can be called on demand.
		 */
		this.tracingProof_ = Proofs.union(tracingProofs);

	}

	public ConclusionAdapter getConvertedQuery() {
		return convertedQuery_;
	}

	public static interface ConclusionAdapter {

		<O> O accept(Visitor<O> visitor);

		public static interface Visitor<O> {
			O visit(DummyConclusion dummyConclusion);

			O visit(InternalConclusion internalConclusion);

			O visit(EntailmentConclusion entailmentConclusion);
		}

	}

	private static class DummyConclusion implements ConclusionAdapter {

		@Override
		public <O> O accept(final Visitor<O> visitor) {
			return visitor.visit(this);
		}

	}

	private static class InternalConclusion extends Delegator<Conclusion>
			implements ConclusionAdapter {

		public InternalConclusion(final Conclusion delegate) {
			super(delegate);
		}

		@Override
		public <O> O accept(final Visitor<O> visitor) {
			return visitor.visit(this);
		}

	}

	private static class EntailmentConclusion extends Delegator<Entailment>
			implements ConclusionAdapter {

		public EntailmentConclusion(final Entailment delegate) {
			super(delegate);
		}

		@Override
		public <O> O accept(final Visitor<O> visitor) {
			return visitor.visit(this);
		}

	}

	private static class InternalInference extends Delegator<TracingInference>
			implements Inference<ConclusionAdapter> {

		public InternalInference(final TracingInference delegate) {
			super(delegate);
		}

		@Override
		public String getName() {
			return getDelegate().getName();
		}

		@Override
		public ConclusionAdapter getConclusion() {
			return new InternalConclusion(getDelegate().getConclusion());
		}

		@Override
		public List<? extends ConclusionAdapter> getPremises() {
			final List<? extends Conclusion> premises = getDelegate()
					.getPremises();
			return new AbstractList<ConclusionAdapter>() {

				@Override
				public ConclusionAdapter get(final int index) {
					return new InternalConclusion(premises.get(index));
				}

				@Override
				public int size() {
					return premises.size();
				}

			};
		}

	}

	private Inference<ConclusionAdapter> wrapEntailmentInference(
			final EntailmentInference inf) {
		final List<? extends Entailment> premises = inf.getPremises();
		final Conclusion reason = getReason(inf);
		return new BaseInference<ConclusionAdapter>(inf.getName(),
				new EntailmentConclusion(inf.getConclusion()),
				new AbstractList<ConclusionAdapter>() {

					@Override
					public ConclusionAdapter get(final int index) {
						return index == premises.size() && reason != null
								? new InternalConclusion(reason)
								: new EntailmentConclusion(premises.get(index));
					}

					@Override
					public int size() {
						final int size = premises.size();
						return reason == null ? size : size + 1;
					}

				});
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

	@Override
	public Collection<? extends Inference<ConclusionAdapter>> getInferences(
			final ConclusionAdapter conclusion) {
		return conclusion.accept(inferenceGetter_);
	}

	private final ConclusionAdapter.Visitor<Collection<? extends Inference<ConclusionAdapter>>> inferenceGetter_ = new ConclusionAdapter.Visitor<Collection<? extends Inference<ConclusionAdapter>>>() {

		@Override
		public Collection<? extends Inference<ConclusionAdapter>> visit(
				final DummyConclusion dummyConclusion) {
			return Collections.emptySet();
		}

		@Override
		public Collection<? extends Inference<ConclusionAdapter>> visit(
				final InternalConclusion internalConclusion) {
			final Conclusion conclusion = internalConclusion.getDelegate();
			final Collection<? extends TracingInference> infs = tracingProof_
					.getInferences(conclusion);
			return Operations.map(infs, transformInternalInference_);
		}

		@Override
		public Collection<? extends Inference<ConclusionAdapter>> visit(
				final EntailmentConclusion entailmentConclusion) {
			return entailmentProof_.getInferences(entailmentConclusion);
		}

	};

	private final Operations.Transformation<TracingInference, Inference<ConclusionAdapter>> transformInternalInference_ = new Operations.Transformation<TracingInference, Inference<ConclusionAdapter>>() {

		@Override
		public Inference<ConclusionAdapter> transform(
				final TracingInference inf) {
			return new InternalInference(inf);
		}

	};

	@Override
	public Set<? extends OWLAxiom> getJustification(
			final Inference<ConclusionAdapter> inference) {
		if (!(inference instanceof InternalInference)) {
			return Collections.emptySet();
		}
		// else
		final InternalInference tracingInference = (InternalInference) inference;
		final Set<OWLAxiom> result = new HashSet<OWLAxiom>();
		tracingInference.getDelegate()
				.accept(new TracingInferencePremiseVisitor<Void>(
						CONCLUSION_FACTORY_, DUMMY_CONCLUSION_VISITOR_,
						new AxiomCollector(result)));
		return result;
	}

	private static final Conclusion.Factory CONCLUSION_FACTORY_ = new ConclusionBaseFactory();

	private static final Conclusion.Visitor<Void> DUMMY_CONCLUSION_VISITOR_ = new DummyConclusionVisitor<Void>();

	private class AxiomCollector extends DummyElkAxiomVisitor<Void> {

		private final Collection<OWLAxiom> axioms_;

		public AxiomCollector(final Collection<OWLAxiom> axioms) {
			this.axioms_ = axioms;
		}

		@Override
		protected Void defaultLogicalVisit(final ElkAxiom axiom) {
			axioms_.add(elkConverter_.convert(axiom));
			return null;
		}

	}

}
