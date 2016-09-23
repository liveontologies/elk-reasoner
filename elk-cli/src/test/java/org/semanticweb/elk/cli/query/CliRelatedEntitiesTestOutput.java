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
import java.util.Set;

import org.semanticweb.elk.cli.CliTestUtil;
import org.semanticweb.elk.owl.interfaces.ElkEntity;
import org.semanticweb.elk.reasoner.query.RelatedEntitiesTestOutput;
import org.semanticweb.elk.reasoner.taxonomy.model.ComparatorKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * ensures that the test results can be compared with {@link #equals(Object)}
 * 
 * @author Peter Skocovsky
 */
public class CliRelatedEntitiesTestOutput<E extends ElkEntity>
		implements RelatedEntitiesTestOutput<E> {

	private final Iterable<? extends Iterable<E>> related_;

	public CliRelatedEntitiesTestOutput(
			final Collection<? extends Collection<E>> related,
			final ComparatorKeyProvider<? super E> keyProvider) {
		this.related_ = CliTestUtil.related2Equalable(related,
				keyProvider.getComparator());
	}

	public CliRelatedEntitiesTestOutput(final Set<? extends Node<E>> related,
			final ComparatorKeyProvider<? super E> keyProvider) {
		this.related_ = CliTestUtil.relatedNodes2Equalable(related,
				keyProvider.getComparator());
	}

	@Override
	public Iterable<? extends Iterable<E>> getSubEntities() {
		return related_;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(getClass(), related_);
	}

	@SuppressWarnings("rawtypes")
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

		return related_.equals(((CliRelatedEntitiesTestOutput) obj).related_);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + related_ + ")";
	}

}
