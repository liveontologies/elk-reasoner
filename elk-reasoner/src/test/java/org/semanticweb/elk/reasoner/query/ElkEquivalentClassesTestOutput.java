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
package org.semanticweb.elk.reasoner.query;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 * @author Yevgeny Kazakov
 */
public class ElkEquivalentClassesTestOutput
		implements EquivalentEntitiesTestOutput<ElkClass> {

	private final List<ElkClass> equivalent_;

	private final boolean isComplete_;

	public ElkEquivalentClassesTestOutput(final Collection<ElkClass> equivalent,
			boolean isComplete) {
		this.equivalent_ = QueryTestUtils.entities2Equalable(equivalent,
				ElkClassKeyProvider.INSTANCE.getComparator());
		this.isComplete_ = isComplete;
	}

	public ElkEquivalentClassesTestOutput(Reasoner reasoner,
			ElkClassExpression query) throws ElkException {
		// TODO: get completeness
		this(reasoner.getEquivalentClassesQuietly(query), true);
	}

	public ElkEquivalentClassesTestOutput(final Node<ElkClass> equivalent,
			boolean isComplete) {
		this.equivalent_ = QueryTestUtils.entities2Equalable(equivalent,
				ElkClassKeyProvider.INSTANCE.getComparator());
		this.isComplete_ = isComplete;
	}

	@Override
	public Iterable<ElkClass> getResult() {
		return equivalent_;
	}

	@Override
	public boolean isComplete() {
		return isComplete_;
	}

	@Override
	public final int hashCode() {
		return Objects.hash(ElkEquivalentClassesTestOutput.class, equivalent_,
				isComplete_);
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ElkEquivalentClassesTestOutput) {
			ElkEquivalentClassesTestOutput other = (ElkEquivalentClassesTestOutput) obj;
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
