package org.semanticweb.elk.proofs;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.liveontologies.puli.Inference;
import org.liveontologies.puli.InferenceJustifier;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.tracing.Conclusion;
import org.semanticweb.elk.reasoner.tracing.ConclusionBaseFactory;
import org.semanticweb.elk.reasoner.tracing.DummyConclusionVisitor;
import org.semanticweb.elk.reasoner.tracing.TracingInferencePremiseVisitor;

public class InternalJustifier
		implements InferenceJustifier<Inference<?>, Set<? extends ElkAxiom>> {

	@Override
	public Set<? extends ElkAxiom> getJustification(
			final Inference<?> inference) {
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
