/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces.dates;
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

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * A base class for xsd:dateTime and xsd:dateTimeStamp intervals.  
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class AbstractDateTimeInterval<DT extends DateTimeDatatype> implements ValueSpace<DT> {

	public final XMLGregorianCalendar lowerBound;
	public final boolean lowerInclusive;
	public final XMLGregorianCalendar upperBound;
	public final boolean upperInclusive;

	public AbstractDateTimeInterval(XMLGregorianCalendar lowerBound,
			boolean lowerInclusive, XMLGregorianCalendar upperBound,
			boolean upperInclusive) {
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
	 * time set so we set it to some default values. 
	 * 
	 * Alternatively we can throw parsing exceptions.
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
			return !lowerInclusive || !upperInclusive;
		case DatatypeConstants.INDETERMINATE:
			// happens if the timezone is undefined. In that case, if the lower
			// bound is not necessarily less than the upper bound, the interval
			// is empty
			return true;
		default:
			// shouldn't happen
			throw new IllegalArgumentException("Comparing " + lowerBound
					+ " and " + upperBound + " returned an unexpected result: "
					+ boundComparison);
		}
	}
	
	Boolean containsInterval(AbstractDateTimeInterval<?> interval) {
		int lCompare = interval.lowerBound.compare(lowerBound);
		int uCompare = upperBound.compare(interval.upperBound);
		boolean lowerBoundCompare = lowerInclusive
				|| !interval.lowerInclusive;
		boolean upperBoundCompare = upperInclusive
				|| !interval.upperInclusive;

		if (lCompare == DatatypeConstants.GREATER
				&& uCompare == DatatypeConstants.GREATER) {
			return true;
		} else if (lCompare == DatatypeConstants.EQUAL
				&& uCompare == DatatypeConstants.GREATER) {
			return lowerBoundCompare;
		} else if (lCompare == DatatypeConstants.GREATER
				&& uCompare == DatatypeConstants.EQUAL) {
			return upperBoundCompare;
		} else if (lCompare == DatatypeConstants.EQUAL
				&& uCompare == DatatypeConstants.EQUAL) {
			return lowerBoundCompare && upperBoundCompare;
		} else {
			return false;
		}
	}

	Boolean containsValue(XMLGregorianCalendar value) {
		int l = value.compare(lowerBound);
		int u = upperBound.compare(value);

		return (l == DatatypeConstants.GREATER || (l == DatatypeConstants.EQUAL && lowerInclusive))
				&& (u == DatatypeConstants.GREATER || (u == DatatypeConstants.EQUAL && upperInclusive));
	}	

	public boolean isUnipointInterval() {
		return lowerBound.compare(upperBound) == DatatypeConstants.EQUAL && lowerInclusive && upperInclusive;
	}	
	
	@Override
	public boolean isSubsumedBy(ValueSpace<?> o) {
		return o.contains(this);
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(DateTimeInterval.class,
				this.lowerBound, this.lowerInclusive,
				this.upperBound, this.upperInclusive);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof AbstractDateTimeInterval<?>) {
			return typeSafeEquals((AbstractDateTimeInterval<?>)obj);
		}
		else {
			return false;
		}
	}

	protected boolean typeSafeEquals(AbstractDateTimeInterval<?> otherEntry) {
		return this.lowerBound.equals(otherEntry.lowerBound)
				&& this.lowerInclusive == otherEntry.lowerInclusive
				&& this.upperBound.equals(otherEntry.upperBound)
				&& this.upperInclusive == otherEntry.upperInclusive;
	}
	
	@Override
	public String toString() {
		return (lowerInclusive ? "[" : "(") + lowerBound.toString() + ","
				+ upperBound.toString() + (upperInclusive ? "]" : ")");
	}

}
