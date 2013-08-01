/*
 * Copyright 2013 Department of Computer Science, University of Oxford.
 *
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
 */
package org.semanticweb.elk.reasoner.datatypes.numbers;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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
 *
 * @author Pospishnyi Olexandr
 */
public class Endpoint implements Comparable<Endpoint> {

	private Number value;
	private boolean inclusive;
	private boolean low;

	public Endpoint(Number value, boolean inclusive, boolean low) {
		this.value = value;
		this.inclusive = inclusive;
		this.low = low;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Endpoint other = (Endpoint) obj;
		if (this.value != other.value && (this.value == null || !this.value.equals(other.value))) {
			return false;
		}
		if (this.inclusive != other.inclusive) {
			return false;
		}
		if (this.low != other.low) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(Endpoint o) {
		int cmp = NumberComparator.INSTANCE.compare(value, o.value);
		if (cmp == 0 && inclusive != o.inclusive) {
			return (low ^ inclusive) ? 1 : -1;
		} else {
			return cmp;
		}
	}

	@Override
	public String toString() {
		if (low) {
			return "" + (inclusive ? "[" : "(") + value;
		} else {
			return "" + value + (inclusive ? "]" : ")");
		}
	}
}
