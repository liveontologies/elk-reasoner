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

import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.util.hashing.HashGenerator;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 */
public class OwlApiRelatedEntitiesTestOutput
		implements RelatedEntitiesTestOutput<OWLClass> {

	private final NodeSet<OWLClass> related_;

	public OwlApiRelatedEntitiesTestOutput(final NodeSet<OWLClass> related) {
		this.related_ = related;
	}

	@Override
	public Iterable<? extends Iterable<OWLClass>> getSubEntities() {
		return related_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(getClass(), related_);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return related_.equals(((OwlApiRelatedEntitiesTestOutput) obj).related_);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + related_ + ")";
	}

}
