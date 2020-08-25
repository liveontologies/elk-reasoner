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
package org.semanticweb.elk.owlapi.query;

import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.reasoner.completeness.IncompletenessMonitor;
import org.semanticweb.elk.reasoner.completeness.ReasoningResult;
import org.semanticweb.elk.reasoner.query.IncompleteEntailmentTestOutput;
import org.semanticweb.owlapi.model.OWLAxiom;

/**
 * The test output for entailment checking of {@link OWLAxiom}s
 * 
 * @author Yevgeny Kazakov
 */
public class OwlEntailmentQueryTestOutput extends
		IncompleteEntailmentTestOutput<OWLAxiom, OwlEntailmentQueryTestOutput> {

	public OwlEntailmentQueryTestOutput(OWLAxiom query,
			boolean entailmentProved, boolean entailmentDisproved) {
		if (entailmentProved) {
			addPositiveEntailment(query);
		}
		if (entailmentDisproved) {
			addNegativeEntailment(query);
		}
	}

	public OwlEntailmentQueryTestOutput(OWLAxiom query, boolean isEntailed) {
		this(query, isEntailed, !isEntailed);
	}

	public OwlEntailmentQueryTestOutput(OWLAxiom query, boolean isEntailed,
			IncompletenessMonitor monitor) {
		this(query, isEntailed,
				!isEntailed && !monitor.isIncompletenessDetected());
	}

	public OwlEntailmentQueryTestOutput(OWLAxiom query,
			ReasoningResult<Boolean> result) {
		this(query, result.getValue(), result.geIncompletenessMonitor());
	}

	public OwlEntailmentQueryTestOutput(ElkReasoner reasoner, OWLAxiom query) {
		this(query, reasoner.checkEntailment(query));
	}

}
