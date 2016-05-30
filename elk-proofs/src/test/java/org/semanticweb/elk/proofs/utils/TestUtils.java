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

import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.inferences.ElkInferenceSet;
import org.semanticweb.elk.owl.inferences.ReasonerProofProvider;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.reasoner.Reasoner;
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

	public static void provabilityTest(Reasoner reasoner,
			Set<? extends ElkAxiom> ontology, ElkObject.Factory factory,
			ElkAxiom goal) throws ElkException {

		LOGGER_.debug("Provability test: {}", goal);
		ElkInferenceSet inferences = new ReasonerProofProvider(reasoner,
				factory).getInferences(goal);
		ProvabilityTester tester = new ProvabilityTester(inferences, ontology);
		if (!tester.isProvable(goal)) {
			throw new AssertionError(String.format("%s: not provable", goal));
		}
		Set<? extends ElkAxiom> unproved = tester.getUnprovedLemmas();
		if (!unproved.isEmpty()) {
			throw new AssertionError(
					String.format("%s: unproved lemmas", unproved));

		}
	}

}
