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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.liveontologies.puli.BaseProof;
import org.liveontologies.puli.Inference;
import org.liveontologies.puli.Inferences;
import org.liveontologies.puli.ModifiableProof;
import org.liveontologies.puli.Proof;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.entailments.model.Entailment;
import org.semanticweb.elk.reasoner.entailments.model.EntailmentInference;
import org.semanticweb.elk.reasoner.entailments.model.HasReason;
import org.semanticweb.elk.reasoner.query.VerifiableQueryResult;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.TracingInference;
import org.semanticweb.elk.util.collections.ArrayHashSet;

/**
 * Combines {@link org.semanticweb.elk.reasoner.tracing.TracingProof
 * TracingProof} and a {@link Proof} over {@link EntailmentInference}s into a
 * {@link Proof} over common conclusions and justifies the
 * {@link TracingInference}s with their axioms.
 * 
 * @author Peter Skocovsky
 */
public class InternalProof implements Proof<Inference<Object>> {

	private final Reasoner reasoner_;
	private final ElkAxiom goal_;

	private final ModifiableProof<Inference<Object>> proof_ = new BaseProof<Inference<Object>>();

	public InternalProof(final Reasoner reasoner, final ElkAxiom goal)
			throws ElkException {
		this.reasoner_ = reasoner;
		this.goal_ = goal;
		VerifiableQueryResult result = reasoner.isEntailed(goal);
		
		try {
			final Entailment entailment = result.getEntailment();
			proof_.produce(Inferences.create("Goal inference", goal_,
					Arrays.asList(entailment)));
			processEntailment(entailment, result.getEvidence(false));
		} finally {
			result.unlock();
		}
	}

	private void processEntailment(final Entailment goal,
			final Proof<EntailmentInference> evidence) throws ElkException {

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

				final Conclusion reason = getReason(inf);

				final List<? extends Entailment> premises = inf.getPremises();
				final List<Object> newPremises = new ArrayList<Object>(
						premises.size() + (reason == null ? 0 : 1));
				newPremises.addAll(premises);

				if (reason != null) {
					if (tracingDone.add(reason)) {
						tracingToDo.add(reason);
					}
					newPremises.add(reason);
				}

				proof_.produce(Inferences.create(inf.getName(),
						inf.getConclusion(), newPremises));

				for (final Entailment premise : inf.getPremises()) {
					if (entailmentDone.add(premise)) {
						entailmentToDo.add(premise);
					}
				}

			}
		}

		final Proof<TracingInference> tracingProof = reasoner_.getProof();

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

	@Override
	public Collection<? extends Inference<Object>> getInferences(
			final Object conclusion) {
		return proof_.getInferences(conclusion);
	}

}
