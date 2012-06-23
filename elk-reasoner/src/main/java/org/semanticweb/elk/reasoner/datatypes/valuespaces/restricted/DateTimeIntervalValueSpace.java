/*
 * #%L
 * ELK Reasoner
 * 
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 - 2012 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted;

import java.util.Calendar;
import java.util.Date;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.DateTimeValue;

/**
 * Representation of dateTime interval with specified restrictions 
 * (lower and upper bound)
 *
 * @author Pospishnyi Olexandr
 */
public class DateTimeIntervalValueSpace implements ValueSpace {

	public static final Calendar START_OF_TIME = Calendar.getInstance();
	public static final Calendar END_OF_TIME = Calendar.getInstance();
	
	static {
		START_OF_TIME.setTime(new Date(Long.MIN_VALUE));
		END_OF_TIME.setTime(new Date(Long.MAX_VALUE));
	}
	
	public Datatype datatype;
	public Calendar lowerBound;
	public boolean lowerInclusive;
	public Calendar upperBound;
	public boolean upperInclusive;

	public DateTimeIntervalValueSpace(Datatype datatype, Calendar lowerBound, boolean lowerInclusive, Calendar upperBound, boolean upperInclusive) {
		this.datatype = datatype;
		this.lowerBound = lowerBound;
		this.lowerInclusive = lowerInclusive;
		this.upperBound = upperBound;
		this.upperInclusive = upperInclusive;
	}


	public boolean isEmptyInterval() {
		int boundComparison = lowerBound.compareTo(upperBound);
		if (boundComparison > 0) {
			return true;
		} else if (boundComparison == 0) {
			if (!lowerInclusive || !upperInclusive
					|| lowerBound == START_OF_TIME
					|| upperBound == END_OF_TIME) {
				return true;
			}
			return false;
		} else {
			return false;
		}
	}

	public boolean isUnipointInterval() {
		return lowerBound.compareTo(upperBound) == 0;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public ValueSpace.ValueSpaceType getType() {
		return ValueSpace.ValueSpaceType.DATETIME_INTERVAL;
	}

	/**
	 * DateTimeIntervalValueSpace could contain 
	 * - DateTimeIntervalValueSpace if this value space completely includes another
	 * - DateTimeValue that is included within specified bounds
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case DATETIME_VALUE: {
				DateTimeValue point = (DateTimeValue) valueSpace;
				if (!point.getDatatype().isCompatibleWith(this.datatype)) {
					return false;
				}
				int l = point.value.compareTo(this.lowerBound);
				int u = point.value.compareTo(this.upperBound);
				return (lowerInclusive ? l >= 0 : l > 0) && (upperInclusive ? u <= 0 : u < 0);
			}
			case DATETIME_INTERVAL: {
				DateTimeIntervalValueSpace range = (DateTimeIntervalValueSpace) valueSpace;
				if (!range.datatype.isCompatibleWith(this.datatype)) {
					return false;
				}
				int l = this.lowerBound.compareTo(range.lowerBound);
				int u = this.upperBound.compareTo(range.upperBound);
				boolean result = true;

				if (!this.lowerInclusive && range.lowerInclusive) {
					result &= l < 0;
				} else {
					result &= l <= 0;
				}

				if (!this.upperInclusive && range.upperInclusive) {
					result &= u > 0;
				} else {
					result &= u >= 0;
				}

				return result;
			}
			default:
				return false;
		}
	}
}
