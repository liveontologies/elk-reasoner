/*-
 * #%L
 * ELK Reasoner Core
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
package org.semanticweb.elk.owl.inferences;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkAxiom;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.query.IncompleteEntailmentTestOutput;

/**
 * The test output for derivability checking of {@link ElkAxiom}s
 * 
 * @author Yevgeny Kazakov
 */
public class ElkQueryDerivabilityTestOutput
		extends IncompleteEntailmentTestOutput<ElkAxiom, ElkQueryDerivabilityTestOutput> {

	public ElkQueryDerivabilityTestOutput(ElkAxiom query, boolean isDerived,
			boolean notEntailed) {
		if (isDerived) {
			addPositiveEntailment(query);
		}
		if (notEntailed) {
			addNegativeEntailment(query);
		}
	}

	public ElkQueryDerivabilityTestOutput(ElkAxiom query, boolean isEntailed) {
		this(query, isEntailed, !isEntailed);
	}

	public ElkQueryDerivabilityTestOutput(Reasoner reasoner, ElkAxiom query)
			throws ElkException {
		// TODO: completeness
		this(query, TestUtils.getNonDerivable(reasoner, null,
				reasoner.getElkFactory(), query) != null);
	}

}
