package org.semanticweb.elk.reasoner.query;

/*-
 * #%L
 * ELK Reasoner Core
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

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.owl.interfaces.ElkNamedIndividual;
import org.semanticweb.elk.reasoner.Reasoner;
import org.semanticweb.elk.reasoner.taxonomy.ElkIndividualKeyProvider;
import org.semanticweb.elk.reasoner.taxonomy.model.Node;

public class ElkInstancesTestOutput
		extends ElkRelatedEntitiesTestOutput<ElkNamedIndividual> {

	public ElkInstancesTestOutput(
			final Collection<? extends Collection<ElkNamedIndividual>> instances,
			boolean isComplete) {
		super(instances, ElkIndividualKeyProvider.INSTANCE, isComplete);
	}

	public ElkInstancesTestOutput(
			final Set<? extends Node<ElkNamedIndividual>> instances,
			boolean isComplete) {
		super(instances, ElkIndividualKeyProvider.INSTANCE, isComplete);
	}

	public ElkInstancesTestOutput(Reasoner reasoner, ElkClassExpression query)
			throws ElkException {
		// TODO: completeness
		this(reasoner.getInstancesQuietly(query, true), true);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(ElkInstancesTestOutput.class, getResult(),
				isComplete());
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ElkInstancesTestOutput) {
			ElkInstancesTestOutput other = (ElkInstancesTestOutput) obj;
			return this == obj || (getResult().equals(other.getResult())
					&& isComplete() == other.isComplete());
		}
		// else
		return false;
	}

}
