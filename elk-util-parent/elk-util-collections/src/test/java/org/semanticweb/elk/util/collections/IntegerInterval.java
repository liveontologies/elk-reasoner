package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
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

import java.util.Comparator;

import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalUtils;

/**
 * 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
class IntegerInterval implements Interval<Integer> {
	
	static final Comparator<Integer> INT_COMPARATOR = new Comparator<Integer>() {

		@Override
		public int compare(Integer o1, Integer o2) {
			return o1.compareTo(o2);
		}
		
	};

	private final int low;
	private final boolean lowerInclusive;
	private final int high;
	private final boolean upperInclusive;

	public IntegerInterval(int low, int high) {
		this(low, true, high, true);
	}

	public IntegerInterval(int low, boolean lowerInclusive, int high, boolean upperInclusive) {
		this.low = low;
		this.lowerInclusive = lowerInclusive;
		this.high = high;
		this.upperInclusive = upperInclusive;
	}

	@Override
	public Integer getLow() {
		return low;
	}

	@Override
	public Integer getHigh() {
		return high;
	}

	@Override
	public boolean contains(Interval<Integer> interval) {
		return IntervalUtils.contains(this, interval, INT_COMPARATOR);
	}

	@Override
	public int compareTo(Interval<Integer> o) {
		return IntervalUtils.compare(this, o, INT_COMPARATOR);
	}

	@Override
	public int hashCode() {
		return IntervalUtils.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IntegerInterval) {
			return IntervalUtils.equal(this, (IntegerInterval) obj, INT_COMPARATOR);
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return IntervalUtils.print(this);
	}

	@Override
	public boolean isLowerInclusive() {
		return lowerInclusive;
	}

	@Override
	public boolean isUpperInclusive() {
		return upperInclusive;
	}
	
}