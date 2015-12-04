/**
 * 
 */
package org.semanticweb.elk.reasoner.tracing;

/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2015 Department of Computer Science, University of Oxford
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

/**
 * A skeleton implementation of {@link Conclusion}
 * 
 * @author Yevgeny Kazakov
 *
 */
public abstract class AbstractConclusion implements Conclusion {

	/**
	 * hash code, computed on demand
	 */
	private int hashCode_ = 0;

	@Override
	public int hashCode() {
		if (hashCode_ == 0) {
			hashCode_ = ConclusionHash.hashCode(this);
		}
		// else
		return hashCode_;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o || hashCode() == o.hashCode()) {
			return true;
		}	
		// else
		return ConclusionEquality.equals(this, o);
	}

	@Override
	public String toString() {
		return ConclusionPrinter.toString(this);
	}

}
