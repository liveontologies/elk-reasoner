/*
 * #%L
 * ELK OWL API Binding
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

import java.util.Objects;

import org.semanticweb.elk.owlapi.ElkReasoner;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.reasoner.Node;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 */
public class OwlEquivalentClassesTestOutput
		implements EquivalentEntitiesTestOutput<OWLClass> {

	private final Node<OWLClass> equivalent_;

	private final boolean isComplete_;

	public OwlEquivalentClassesTestOutput(final Node<OWLClass> equivalent,
			boolean isComplete) {
		this.equivalent_ = equivalent;
		this.isComplete_ = isComplete;
	}

	public OwlEquivalentClassesTestOutput(ElkReasoner reasoner,
			OWLClassExpression query) {
		// TODO: completeness
		this(reasoner.getEquivalentClasses(query), true);

	}

	@Override
	public Iterable<OWLClass> getResult() {
		return equivalent_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(OwlEquivalentClassesTestOutput.class, equivalent_,
				isComplete_);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof OwlEquivalentClassesTestOutput) {
			OwlEquivalentClassesTestOutput other = (OwlEquivalentClassesTestOutput) obj;
			return this == obj || (equivalent_.equals(other.equivalent_)
					&& isComplete_ == other.isComplete_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + equivalent_ + ", "
				+ (isComplete_ ? "" : "...") + ")";
	}

}
