/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers;
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

import java.util.Comparator;

import org.semanticweb.elk.owl.datatypes.RealDatatype;
import org.semanticweb.elk.reasoner.datatypes.util.NumberUtils;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.collections.intervals.BaseInterval;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalUtils;

/**
 * Generic base class for numeric intervals.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
abstract class NumericInterval<DT extends RealDatatype> extends BaseInterval<Number> implements ValueSpace<DT> {

	protected NumericInterval(Number l, boolean li, Number u, boolean ui) {
		super(l, li, u, ui);
	}

	@Override
	public boolean isSubsumedBy(ValueSpace<?> o) {
		return o.contains(this);
	}

	@Override
	public boolean isEmpty() {
		return IntervalUtils.isEmpty(this, NumberUtils.COMPARATOR);
	}

	@Override
	public boolean contains(Interval<Number> interval) {
		return IntervalUtils.contains(this, interval, NumberUtils.COMPARATOR);
	}
	
	protected boolean containsInterval(Interval<Number> interval) {
		return contains(interval);
	}
	
	protected boolean containsValue(Number value) {
		return IntervalUtils.contains(this, value, NumberUtils.COMPARATOR);
	}

	public boolean isUnipointInterval() {
		return IntervalUtils.isUnitInterval(this, NumberUtils.COMPARATOR);
	}

	@Override
	protected Comparator<Number> getComparator() {
		return NumberUtils.COMPARATOR;
	}
	
	@Override
	public String toString() {
		return IntervalUtils.print(this);
	}
	
	@Override
	public int hashCode() {
		return IntervalUtils.hashCode(this);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof NumericInterval<?>) {
			NumericInterval<?> otherEntry = (NumericInterval<?>) other;
			return this.lower.equals(otherEntry.lower)
				&& this.isLowerInclusive == otherEntry.isLowerInclusive
				&& this.upper.equals(otherEntry.upper)
				&& this.isUpperInclusive == otherEntry.isUpperInclusive;
		}
		return false;
	}
	
/*	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof RealInterval) {
			return IntervalUtils.equal(this, (NumericInterval<?>) other, getComparator());
		}
		else {
			return false;
		}
	}
*/
}
