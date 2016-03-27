package org.semanticweb.elk.matching.conclusions;

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

import org.semanticweb.elk.owl.interfaces.ElkObjectSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.model.IndexedRangeFiller;

public class IndexedRangeFillerMatch extends IndexedContextRootMatch {

	private final ElkObjectSomeValuesFrom existentialMatch_;

	public IndexedRangeFillerMatch(ElkObjectSomeValuesFrom existentialMatch) {
		this.existentialMatch_ = existentialMatch;
	}

	/**
	 * @return the {@link ElkObjectSomeValuesFrom} whose property and filler
	 *         match respectively the property and the filler of the
	 *         {@link IndexedRangeFiller}
	 */
	public ElkObjectSomeValuesFrom getValue() {
		return existentialMatch_;
	}

	@Override
	public <O> O accept(IndexedContextRootMatch.Visitor<O> visitor) {
		return visitor.visit(this);
	}

	/**
	 * The visitor pattern for instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 * @param <O>
	 *            the type of the output
	 */
	interface Visitor<O> {

		O visit(IndexedRangeFillerMatch match);

	}

	/**
	 * A factory for creating instances
	 * 
	 * @author Yevgeny Kazakov
	 *
	 */
	public interface Factory {

		IndexedRangeFillerMatch getIndexedRangeFillerMatch(
				ElkObjectSomeValuesFrom existentialMatch);

	}

}
