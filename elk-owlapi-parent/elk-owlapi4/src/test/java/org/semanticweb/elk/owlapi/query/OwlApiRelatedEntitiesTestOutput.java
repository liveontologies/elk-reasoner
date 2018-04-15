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
import org.semanticweb.owlapi.model.OWLLogicalEntity;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * A {@link RelatedEntitiesTestOutput} that returns {@link OWLLogicalEntity}s
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 * 
 * @param <E>
 *            the type of the entities to return
 */
public abstract class OwlApiRelatedEntitiesTestOutput<E extends OWLLogicalEntity>
		implements RelatedEntitiesTestOutput<E> {

	private final NodeSet<E> related_;

	private final boolean isComplete_;

	public OwlApiRelatedEntitiesTestOutput(final NodeSet<E> related,
			boolean isComplete) {
		this.related_ = related;
		this.isComplete_ = isComplete;
	}

	@Override
	public Iterable<? extends Iterable<E>> getResult() {
		return related_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(getClass(), related_);
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof OwlApiRelatedEntitiesTestOutput<?>) {
			OwlApiRelatedEntitiesTestOutput<?> other = (OwlApiRelatedEntitiesTestOutput<?>) obj;
			return this == obj || (related_.equals(other.related_)
					&& isComplete_ == other.isComplete_);
		}
		// else
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + related_
				+ (isComplete_ ? "" : "...") + ")";
	}

}
