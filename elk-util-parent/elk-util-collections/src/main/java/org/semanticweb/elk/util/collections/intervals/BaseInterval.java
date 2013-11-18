/**
 * 
 */
package org.semanticweb.elk.util.collections.intervals;
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

/**
 * A basic representation of a generic interval. 
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseInterval<T> implements Interval<T> {

	protected final T lower;
	protected final boolean isLowerInclusive;
	protected final T upper;
	protected final boolean isUpperInclusive;
	
	public BaseInterval(T l, boolean li, T u, boolean ui) {
		lower = l;
		upper = u;
		isLowerInclusive = li;
		isUpperInclusive = ui;
	}
	
	@Override
	public T getLow() {
		return lower;
	}

	@Override
	public boolean isLowerInclusive() {
		return isLowerInclusive;
	}

	@Override
	public T getHigh() {
		return upper;
	}

	@Override
	public boolean isUpperInclusive() {
		return isUpperInclusive;
	}

	@Override
	public int compareTo(Interval<T> interval) {
		return IntervalUtils.compare(this, interval, getComparator());
	}

	@Override
	public boolean subsumes(Interval<T> interval) {
		return IntervalUtils.subsumes(this, interval, getComparator());
	}
	
	public boolean isEmpty() {
		return IntervalUtils.isEmpty(this, getComparator());
	}	
	
	public boolean isUnitInterval() {
		return IntervalUtils.isUnitInterval(this, getComparator());
	}

	/**
	 * Subclasses should indicate how the interval boundaries should be compared.
	 * @return
	 */
	protected abstract Comparator<T> getComparator();
}
