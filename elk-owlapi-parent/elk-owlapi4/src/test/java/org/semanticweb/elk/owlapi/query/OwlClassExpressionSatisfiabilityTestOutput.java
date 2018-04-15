package org.semanticweb.elk.owlapi.query;

/*-
 * #%L
 * ELK OWL API v.4 Binding
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2018 Department of Computer Science, University of Oxford
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

import java.util.Objects;

import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.reasoner.query.BaseSatisfiabilityTestOutput;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class OwlClassExpressionSatisfiabilityTestOutput
		extends BaseSatisfiabilityTestOutput {

	public OwlClassExpressionSatisfiabilityTestOutput(boolean isSatisfiable,
			boolean isComplete) {
		super(isSatisfiable, isComplete);
	}

	public OwlClassExpressionSatisfiabilityTestOutput(ElkReasoner reasoner,
			OWLClassExpression query) {
		// TODO: completeness
		this(reasoner.isSatisfiable(query), true);
	}

	@Override
	public int hashCode() {
		return Objects.hash(OwlClassExpressionSatisfiabilityTestOutput.class,
				getResult(), isComplete());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof BaseSatisfiabilityTestOutput) {
			BaseSatisfiabilityTestOutput other = (BaseSatisfiabilityTestOutput) obj;
			return this == obj || (getResult() == other.getResult()
					&& isComplete() == other.isComplete());
		}
		// else
		return false;
	}

}
