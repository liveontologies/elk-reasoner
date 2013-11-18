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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.dates;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseValueSpaceContainmentVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;

/**
 * Value space that represents a single xsd:dateTime or xsd:dateTimeStamp value.
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class DateTimeValue implements PointValue<DateTimeDatatype, XMLGregorianCalendar> {

	private XMLGregorianCalendar value_;

	public DateTimeValue(XMLGregorianCalendar value) {
		this.value_ = value;
	}

	@Override
	public XMLGregorianCalendar getValue() {
		return value_;
	}
	
	@Override
	public DateTimeDatatype getDatatype() {
		return value_.getTimezone() == DatatypeConstants.FIELD_UNDEFINED ? ElkDatatypeMap.XSD_DATE_TIME : ElkDatatypeMap.XSD_DATE_TIME_STAMP;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * DateTimeValue could contain only another DateTimeValue if both are equal.
	 * Note that according to XML Schema, two dateTime values representing the
	 * same time instant but with different time zone offsets are equal, but not
	 * identical.
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {
		// we don't visit dateTime intervals because a time instant can only
		// contain unit intervals and those are automatically converted to time
		// instants anyway.
		// we also don't need to compare against xsd:dateTimeStamp values.
		return valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(DateTimeValue value) {
				return value.getDatatype() == getDatatype() && identical((DateTimeValue) value);
			}

		});
	}

	@Override
	public boolean isSubsumedBy(ValueSpace<?> valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if (other instanceof DateTimeValue) {
			return identical((DateTimeValue)other);
		}
		else {
			return false;
		}
	}
	
	private boolean identical(DateTimeValue value) {
		// checking for identity, not equality here
		return value_.getTimezone() == value.getValue().getTimezone() && value_.equals(value.getValue());
	}
	
	@Override
	public int hashCode() {
		return value_.hashCode();
	}

	@Override
	public String toString() {
		return value_.toString() + "^^" + getDatatype().toString();
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
