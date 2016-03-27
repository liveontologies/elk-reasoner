/**
 * 
 */
package org.semanticweb.elk.proofs.utils;
/*
 * #%L
 * ELK Proofs Package
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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.semanticweb.elk.matching.Matcher;
import org.semanticweb.elk.owl.exceptions.ElkException;
import org.semanticweb.elk.owl.implementation.ElkObjectFactoryImpl;
import org.semanticweb.elk.owl.inferences.ElkInference;
import org.semanticweb.elk.owl.inferences.ElkInferencePremiseVisitor;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.inferences.ModifiableElkInferenceSet;
import org.semanticweb.elk.owl.inferences.ModifiableElkInferenceSetImpl;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.interfaces.ElkSubClassOfAxiom;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.ClassConclusion;
import org.semanticweb.elk.reasoner.saturation.conclusions.model.SubClassInclusionComposed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for testing proofs
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 * @author Yevgeny Kazakov
 */
public class TestUtils {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(TestUtils.class);

	public static void provabilityOfSubsumptionTest(Reasoner reasoner,
			Set<? extends ElkAxiom> ontology, ElkObjectFactory factory,
			ElkSubClassOfAxiom subsumption) throws ElkException {

		ModifiableElkInferenceSet elkInferences = new ModifiableElkInferenceSetImpl(
				factory);
		ClassConclusion conclusion = reasoner.getConclusion(subsumption);
		if (conclusion == null) {
			throw new AssertionError(String.format(
					"%s: subsumption contains expressions not occurring in the ontology",
					subsumption));
		}
		Matcher matcher = new Matcher(reasoner.explainConclusion(conclusion),
				factory, elkInferences);
		if (conclusion instanceof SubClassInclusionComposed) {
			matcher.trace((SubClassInclusionComposed) conclusion);
		}

		provabilityTest(elkInferences, ontology, subsumption);

	}

	public static void provabilityOfInconsistencyTest(Reasoner reasoner,
			Set<? extends ElkAxiom> ontology) throws ElkException {
		// TODO
	}

	public static void provabilityTest(ElkInferenceSet inferences,
			Set<? extends ElkAxiom> ontology, ElkAxiom goal)
			throws ElkException {
		final Set<ElkAxiom> done = new HashSet<ElkAxiom>();
		final Queue<ElkAxiom> toDo = new LinkedList<ElkAxiom>();
		toDo.add(goal);

		ElkInference.Visitor<Void> premiseVisitor = new ElkInferencePremiseVisitor<Void>(
				new ElkObjectFactoryImpl(), new DummyElkAxiomVisitor<Void>() {
					@Override
					protected Void defaultLogicalVisit(ElkAxiom axiom) {
						LOGGER_.trace("{}: todo", axiom);
						toDo.add(axiom);
						return null;
					}
				});

		for (;;) {
			ElkAxiom next = toDo.poll();

			if (next == null) {
				break;
			}

			if (ontology.contains(next))
				continue;

			if (done.add(next)) {
				LOGGER_.trace("{}: new lemma", next);
				boolean inferred = false;
				for (ElkInference inf : inferences.get(next)) {
					LOGGER_.trace("{}: expanding", inf);
					inf.accept(premiseVisitor);
					inferred |= true;
				}
				if (!inferred) {
					throw new AssertionError(String
							.format("%s: cannot expand all proofs", goal));
				} else {
					LOGGER_.trace("{}: inferred", next);
				}
			}
		}

	}
}
