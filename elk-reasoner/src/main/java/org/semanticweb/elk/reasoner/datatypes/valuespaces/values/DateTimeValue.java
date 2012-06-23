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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.values;

import java.util.Calendar;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Value space that represent single dateTime value.
 *
 * @author Pospishnyi Olexandr
 */
public class DateTimeValue implements ValueSpace {

	public Calendar value;
	public Datatype toldDatatype;
	public Datatype effectiveDatatype;

	public DateTimeValue(Calendar value, Datatype datatype) {
		this.value = value;
		this.toldDatatype = datatype;
		if (value.getTimeZone().getID().startsWith("GMT")) {
			effectiveDatatype = Datatype.xsd_dateTimeStamp;
		} else {
			effectiveDatatype = Datatype.xsd_dateTime;
		}
	}

	public Datatype getDatatype() {
		return effectiveDatatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.DATETIME_VALUE;
	}

	public boolean isEmptyInterval() {
		return value != null;
	}

	/**
	 * DateTimeValue could contain only another DateTimeValue if both are equal.
	 * Note that according to XML Schema, two dateTime values representing the same
	 * time instant but with different time zone offsets are equal, but not
	 * identical.
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace) {
		switch (valueSpace.getType()) {
			case DATETIME_VALUE:
				DateTimeValue vs = (DateTimeValue) valueSpace;
				return this.value.equals(vs.value);
			default:
				return false;
		}
	}
}
