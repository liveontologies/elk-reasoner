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
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkObjectFactory;
import org.semanticweb.elk.owl.visitors.DummyElkAxiomVisitor;
import org.semanticweb.elk.proofs.inferences.ElkInference;
import org.semanticweb.elk.proofs.inferences.ElkInferencePremiseVisitor;
import org.semanticweb.elk.proofs.inferences.ElkInferenceSet;
import org.semanticweb.elk.proofs.inferences.ModifiableElkInferenceSet;
import org.semanticweb.elk.proofs.inferences.ModifiableElkInferenceSetImpl;
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
			ElkClassExpression sub, ElkClassExpression sup)
			throws ElkException {

		ModifiableElkInferenceSet elkInferences = new ModifiableElkInferenceSetImpl(
				factory);
		ClassConclusion conclusion = reasoner.getConclusion(sub, sup);
		Matcher matcher = new Matcher(reasoner.explainConclusion(conclusion),
				factory, elkInferences);
		if (conclusion instanceof SubClassInclusionComposed) {
			matcher.trace((SubClassInclusionComposed) conclusion);
		}

		provabilityTest(elkInferences, ontology,
				factory.getSubClassOfAxiom(sub, sup));

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
		System.out.println(ontology);
		toDo.add(goal);

		ElkInference.Visitor<Void> premiseVisitor = new ElkInferencePremiseVisitor<Void>(
				new ElkObjectFactoryImpl(), new DummyElkAxiomVisitor<Void>() {
					@Override
					protected Void defaultLogicalVisit(ElkAxiom axiom) {
						LOGGER_.info("{}: todo", axiom);
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

			LOGGER_.info("{}: next lemma", next);

			if (done.add(next)) {
				boolean proved = false;
				for (ElkInference inf : inferences.get(next)) {
					LOGGER_.info("{}: unfolding", inf);
					inf.accept(premiseVisitor);
					proved |= true;
				}
				if (!proved) {
					throw new AssertionError(
							String.format("%s: no proof found", goal));
				} else {
					LOGGER_.info("{}: proved", next);
				}
			}
		}

	}
}
