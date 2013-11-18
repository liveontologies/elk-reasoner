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
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class UnitInterval<T> implements Interval<T> {

	protected final T value;

	public UnitInterval(T val) {
		value = val;
	}
	
	@Override
	public T getLow() {
		return value;
	}

	@Override
	public boolean isLowerInclusive() {
		return true;
	}

	@Override
	public T getHigh() {
		return value;
	}

	@Override
	public boolean isUpperInclusive() {
		return true;
	}

	@Override
	public boolean subsumes(Interval<T> interval) {
		return IntervalUtils.subsumes(this, interval, getComparator());
	}

	@Override
	public int compareTo(Interval<T> interval) {
		return IntervalUtils.compare(this, interval, getComparator());
	}
	
	@Override
	public String toString() {
		return IntervalUtils.print(this);
	}
	
	protected abstract Comparator<T> getComparator();
}
