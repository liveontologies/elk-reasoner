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
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.reasoner.NodeSet;

public class OwlInstancesTestOutput
		extends OwlApiRelatedEntitiesTestOutput<OWLNamedIndividual> {

	public OwlInstancesTestOutput(final NodeSet<OWLNamedIndividual> related,
			boolean isComplete) {
		super(related, isComplete);
	}

	public OwlInstancesTestOutput(ElkReasoner reasoner,
			OWLClassExpression query) {
		// TODO: completeness
		this(reasoner.getInstances(query, true), true);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(OwlInstancesTestOutput.class, getResult(),
				isComplete());
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof OwlInstancesTestOutput) {
			OwlInstancesTestOutput other = (OwlInstancesTestOutput) obj;
			return this == obj || (getResult().equals(other.getResult())
					&& isComplete() == other.isComplete());
		}
		// else
		return false;
	}

}
