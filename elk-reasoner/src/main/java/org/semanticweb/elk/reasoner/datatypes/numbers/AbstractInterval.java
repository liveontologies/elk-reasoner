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

import org.semanticweb.elk.util.collections.intervals.Interval;

/**
 *
 * @author Pospishnyi Olexandr
 */
public abstract class AbstractInterval implements Interval<Endpoint> {

	protected Endpoint low, high;

	@Override
	public Endpoint getLow() {
		return low;
	}

	@Override
	public Endpoint getHigh() {
		return high;
	}

	@Override
	abstract public boolean equals(Object obj);

	@Override
	abstract public int hashCode();
	
	@Override
	public int compareTo(Interval<Endpoint> o) {
		int cmp = low.compareTo(o.getLow());
		if (cmp == 0) {
			return high.compareTo(o.getHigh());
		} else {
			return cmp;
		}
	}

	@Override
	public boolean contains(Interval<Endpoint> interval) {
		return low.compareTo(interval.getLow()) <= 0 && high.compareTo(interval.getHigh()) >= 0;
	}
}
