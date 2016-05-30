package org.semanticweb.elk.matching.subsumers;

import org.semanticweb.elk.matching.root.IndexedContextRootMatch;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.interfaces.ElkObjectPropertyExpression;

/*
 * #%L
 * ELK Proofs Package
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

public interface IndexedObjectSomeValuesFromMatch
		extends SubsumerElkObjectMatch {

	ElkObjectPropertyExpression getPropertyMatch();

	ElkObject getFillerMatch();
	
	IndexedContextRootMatch getFillerRootMatch(
			IndexedContextRootMatch.Factory factory);

	IndexedContextRootMatch getRangeRootMatch(
			IndexedContextRootMatch.Factory factory);

	<O> O accept(Visitor<O> visitor);

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	public interface Visitor<O>
			extends SubsumerObjectSomeValuesFromMatch.Visitor<O>,
			SubsumerObjectHasValueMatch.Visitor<O> {

		// combined interface
	}

}
