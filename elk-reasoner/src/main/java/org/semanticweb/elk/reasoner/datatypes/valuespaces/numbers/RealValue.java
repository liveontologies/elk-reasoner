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

import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.parsing.NumberUtils;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;
import org.semanticweb.elk.util.collections.intervals.IntervalUtils;
import org.semanticweb.elk.util.collections.intervals.UnitInterval;

/**
 * Represents a single numerical value. It implements {@link Interval} so that
 * values can be stored in {@link IntervalTree} as unit intervals.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public abstract class RealValue<DT extends RealDatatype> extends UnitInterval<Number> implements PointValue<DT, Number> {

	public RealValue(final Number value) {
		super(value);
	}

	@Override
	public Number getValue() {
		return value;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public int hashCode() {
		return getValue().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof RealValue) {
			return IntervalUtils.equal(this, (RealValue<?>) other, getComparator());
		}
		else {
			return false;
		}
	}	
	
	@Override
	public boolean isSubsumedBy(ValueSpace<?> o) {
		return o.contains(this);
	}

	protected boolean containsInterval(Interval<Number> interval) {
		return subsumes(interval);
	}
	
	protected boolean containsValue(Number value) {
		return IntervalUtils.contains(this, value, NumberUtils.COMPARATOR);
	}

	@Override
	protected Comparator<Number> getComparator() {
		return NumberUtils.COMPARATOR;
	}
	
}
