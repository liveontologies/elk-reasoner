package org.semanticweb.elk.owl.predefined;

/*
 * #%L
 * ELK OWL Object Interfaces
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

import org.semanticweb.elk.owl.comparison.ElkObjectEquality;
import org.semanticweb.elk.owl.comparison.ElkObjectHash;
import org.semanticweb.elk.owl.interfaces.ElkObject;
import org.semanticweb.elk.owl.printers.OwlFunctionalStylePrinter;

/**
 * A skeleton implementation of {@link ElkObject} defining equality, hash, and
 * similar functions. All other implementation should extend or delegate to this
 * class.
 * 
 * @author Yevgeny Kazakov
 */
public abstract class AbstractElkObject implements ElkObject {

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	@Override
	public final int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = accept(ElkObjectHash.getInstance());
		}
		// else
		return hashCode_;
	}

	@Override
	public final boolean equals(Object o) {
		return this == o || (o != null && hashCode() == o.hashCode()
				&& accept(new ElkObjectEquality(o)) != null);
	}

	@Override
	public String toString() {
		return OwlFunctionalStylePrinter.toString(this);
	}

}
