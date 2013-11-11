/*
 * #%L
 * ELK Reasoner
 * *
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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.handlers.DateTimeDatatypeHandler;
import org.semanticweb.elk.reasoner.datatypes.index.ValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.DateTimeValue;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Representation of dateTime interval with specified restrictions (lower and
 * upper bound)
 * 
 * @author Pospishnyi Olexandr
 */
public class DateTimeIntervalValueSpace implements ValueSpace {

	public final ElkDatatype datatype;
	public final XMLGregorianCalendar lowerBound;
	public final boolean lowerInclusive;
	public final XMLGregorianCalendar upperBound;
	public final boolean upperInclusive;

	public DateTimeIntervalValueSpace(ElkDatatype datatype,
			XMLGregorianCalendar lowerBound, boolean lowerInclusive,
			XMLGregorianCalendar upperBound, boolean upperInclusive) {
		this.datatype = datatype;
		this.lowerBound = lowerBound;
		this.lowerInclusive = lowerInclusive;
		this.upperBound = upperBound;
		this.upperInclusive = upperInclusive;

		setDefaultTimeValues(this.lowerBound);
		setDefaultTimeValues(this.upperBound);
	}

	/*
	 * XMLGregorianCalendar.compareTo returns indeterminate results if the time
	 * filed are undefined. According to the spec xsd:dateTime must always have
	 * time set so we set it to some default values. Alternatively we can throw
	 * parsing exceptions.
	 */
	private void setDefaultTimeValues(XMLGregorianCalendar calendar) {
		if (calendar.getHour() == DatatypeConstants.FIELD_UNDEFINED) {
			calendar.setHour(0);
		}
		if (calendar.getMinute() == DatatypeConstants.FIELD_UNDEFINED) {
			calendar.setMinute(0);
		}
		if (calendar.getSecond() == DatatypeConstants.FIELD_UNDEFINED) {
			calendar.setSecond(0);
		}
	}

	@Override
	public boolean isEmpty() {
		int boundComparison = lowerBound.compare(upperBound);

		switch (boundComparison) {
		case DatatypeConstants.LESSER:
			return false;
		case DatatypeConstants.GREATER:
			return true;
		case DatatypeConstants.EQUAL:
			// empty if one of the bounds is exclusive
			return !lowerInclusive || !upperInclusive
					|| lowerBound == DateTimeDatatypeHandler.START_OF_TIME
					|| upperBound == DateTimeDatatypeHandler.END_OF_TIME;
		case DatatypeConstants.INDETERMINATE:
			// happens if the timezone is undefined. In that case, if the lower
			// bound is not necessarily less than the upper bound, the interval
			// is empty
			return true;
		default:
			// shouldn't happen
			throw new IllegalArgumentException("Comparing " + lowerBound
					+ " and " + upperBound + " returned " + boundComparison);
		}
	}

	public boolean isUnipointInterval() {
		return lowerBound.compare(upperBound) == 0;
	}

	@Override
	public ElkDatatype getDatatype() {
		return datatype;
	}

	@Override
	public ValueSpace.ValueSpaceType getType() {
		return ValueSpace.ValueSpaceType.DATETIME_INTERVAL;
	}

	/**
	 * DateTimeIntervalValueSpace could contain - DateTimeIntervalValueSpace if
	 * this value space completely includes another - DateTimeValue that is
	 * included within specified bounds
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
		case DATETIME_VALUE: {
			DateTimeValue point = (DateTimeValue) valueSpace;
			if (!point.getDatatype().isCompatibleWith(this.datatype)) {
				return false;
			}
			int l = point.value.compare(this.lowerBound);
			int u = point.value.compare(this.upperBound);
			return (lowerInclusive ? l >= 0 : l > 0)
					&& (upperInclusive ? u <= 0 : u < 0);
		}
		case DATETIME_INTERVAL: {
			DateTimeIntervalValueSpace range = (DateTimeIntervalValueSpace) valueSpace;
			if (!range.datatype.isCompatibleWith(this.datatype)) {
				return false;
			}
			int l = this.lowerBound.compare(range.lowerBound);
			int u = this.upperBound.compare(range.upperBound);
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

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof DateTimeIntervalValueSpace) {
			DateTimeIntervalValueSpace otherEntry = (DateTimeIntervalValueSpace) other;
			return this.datatype.equals(otherEntry.datatype)
					&& this.lowerBound.equals(otherEntry.lowerBound)
					&& this.lowerInclusive == otherEntry.lowerInclusive
					&& this.upperBound.equals(otherEntry.upperBound)
					&& this.upperInclusive == otherEntry.upperInclusive;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(DateTimeIntervalValueSpace.class,
				this.datatype, this.lowerBound, this.lowerInclusive,
				this.upperBound, this.upperInclusive);
	}

	@Override
	public String toString() {
		return (lowerInclusive ? "[" : "(") + lowerBound.toString() + ","
				+ upperBound.toString() + (upperInclusive ? "]" : ")") + "^^"
				+ datatype;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
