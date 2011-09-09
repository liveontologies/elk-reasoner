/*
 * #%L
 * elk-reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Oxford University Computing Laboratory
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
package org.semanticweb.elk.util.collections;

public class Triple<First, Second, Third> {
	protected final First first;
	protected final Second second;
	protected final Third third;
	
	public Triple(First first, Second second, Third third) {
		assert first != null;
		assert second != null;
		assert third != null;
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public First getFirst() {
		return first;
	}

	public Second getSecond() {
		return second;
	}

	public Third getThird() {
		return third;
	}

	@Override
	public int hashCode() {
		final int prime = 945194447;
		int result = 1;
		result = prime * result + first.hashCode();
		result = prime * result + second.hashCode();
		result = prime * result + third.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Triple<?,?,?> other = (Triple<?,?,?>) obj;
		if (!first.equals(other.first))
			return false;
		if (!second.equals(other.second))
			return false;
		if (!third.equals(other.third))
			return false;
		return true;
	}

}
