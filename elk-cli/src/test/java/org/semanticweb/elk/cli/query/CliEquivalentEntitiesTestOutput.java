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
package org.semanticweb.elk.cli.query;

import java.util.Collection;
import java.util.List;

import org.semanticweb.elk.cli.CliTestUtil;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.reasoner.query.EquivalentEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.ElkClassKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 */
public class CliEquivalentEntitiesTestOutput
		implements EquivalentEntitiesTestOutput<ElkClass> {

	private final List<ElkClass> equivalent_;

	public CliEquivalentEntitiesTestOutput(
			final Collection<ElkClass> equivalent) {
		this.equivalent_ = CliTestUtil.entities2Equalable(equivalent,
				ElkClassKeyProvider.INSTANCE.getComparator());
	}

	public CliEquivalentEntitiesTestOutput(final Node<ElkClass> equivalent) {
		this.equivalent_ = CliTestUtil.entities2Equalable(equivalent,
				ElkClassKeyProvider.INSTANCE.getComparator());
	}

	@Override
	public Iterable<ElkClass> getEquivalent() {
		return equivalent_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(getClass(), equivalent_);
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

		return equivalent_
				.equals(((CliEquivalentEntitiesTestOutput) obj).equivalent_);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + equivalent_ + ")";
	}

}
