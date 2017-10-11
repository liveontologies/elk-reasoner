/*-
 * #%L
 * ELK Reasoner Protege Plug-in
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

import java.util.Arrays;
import java.util.Collection;

import org.liveontologies.puli.Inference;
import org.liveontologies.puli.Inferences;
import org.liveontologies.puli.Proof;
import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.exceptions.ElkRuntimeException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owlapi.ElkConverter;
import org.semanticweb.elk.owlapi.wrapper.OwlConverter;
import org.semanticweb.elk.proofs.InternalProof;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.query.UnsupportedQueryTypeEntailmentQueryResult;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.reasoner.UnsupportedEntailmentTypeException;

public class OwlInternalProof implements Proof<Inference<Object>> {

	private final OwlConverter owlConverter_ = OwlConverter.getInstance();
	private final ElkConverter elkConverter_ = ElkConverter.getInstance();

	private final OWLAxiom goal_;
	private final Inference<Object> goalInference_;
	private final InternalProofExtension proof_;

	public OwlInternalProof(final Reasoner reasoner, final OWLAxiom goal) {
		this.goal_ = goal;
		final ElkAxiom convertedGoal = owlConverter_.convert(goal);
		this.goalInference_ = Inferences.create("Converting inference", goal,
				Arrays.asList(convertedGoal));
		try {
			this.proof_ = new InternalProofExtension(reasoner,
					owlConverter_.convert(goal));
		} catch (final ElkException e) {
			throw elkConverter_.convert(e);
		} catch (final ElkRuntimeException e) {
			throw elkConverter_.convert(e);
		}
	}

	public Object getGoal() {
		return goal_;
	}

	@Override
	public Collection<? extends Inference<Object>> getInferences(
			final Object conclusion) {
		if (goal_.equals(conclusion)) {
			@SuppressWarnings("unchecked")
			final Collection<? extends Inference<Object>> result = Arrays
					.asList(goalInference_);
			return result;
		}
		// else
		return proof_.getInferences(conclusion);
	}

	private class InternalProofExtension extends InternalProof {

		public InternalProofExtension(final Reasoner reasoner,
				final ElkAxiom goal) throws ElkException {
			super(reasoner, goal);
		}

		@Override
		public Void visit(
				final UnsupportedQueryTypeEntailmentQueryResult unsupportedQueryType)
				throws ElkException {
			throw new UnsupportedEntailmentTypeException(
					elkConverter_.convert(unsupportedQueryType.getQuery()));
		}

	}

}