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

import org.semanticweb.elk.exceptions.ElkException;
import org.semanticweb.elk.owl.interfaces.ElkClass;
import org.semanticweb.elk.owl.interfaces.ElkClassExpression;
import org.semanticweb.elk.reasoner.Reasoner;

public class ElkSuperClassesTestOutput extends ElkRelatedClassesTestOutput {

	public ElkSuperClassesTestOutput(
			final Collection<? extends Collection<ElkClass>> superClasses,
			boolean isComplete) {
		super(superClasses, isComplete);
	}

	public ElkSuperClassesTestOutput(Reasoner reasoner,
			ElkClassExpression query) throws ElkException {
		// TODO: completeness
		super(reasoner.getSuperClassesQuietly(query, true), true);
	}

	@Override
	public final int hashCode() {
		return Objects.hash(ElkSuperClassesTestOutput.class, getResult(),
				isComplete());
	}

	@Override
	public final boolean equals(final Object obj) {
		if (obj instanceof ElkSuperClassesTestOutput) {
			ElkSuperClassesTestOutput other = (ElkSuperClassesTestOutput) obj;
			return this == obj || (getResult().equals(other.getResult())
					&& isComplete() == other.isComplete());
		}
		// else
		return false;
	}

}
