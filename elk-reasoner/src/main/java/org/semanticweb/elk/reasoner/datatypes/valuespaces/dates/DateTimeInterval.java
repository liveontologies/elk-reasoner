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

import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseValueSpaceContainmentVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;

/**
 * Representation of dateTime interval with specified restrictions (lower and
 * upper bound)
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class DateTimeInterval extends AbstractDateTimeInterval<DateTimeDatatype> {

	public DateTimeInterval(XMLGregorianCalendar lowerBound,
			boolean lowerInclusive, XMLGregorianCalendar upperBound,
			boolean upperInclusive) {
		super(lowerBound, lowerInclusive, upperBound, upperInclusive);
	}

	/**
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {

		return valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(DateTimeInterval interval) {
				return containsInterval(interval);
			}
			
			@Override
			public Boolean visit(DateTimeStampInterval interval) {
				return containsInterval(interval);
			}

			@Override
			public Boolean visit(DateTimeValue value) {
				return containsValue(value.getValue());
			}

		});
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public DateTimeDatatype getDatatype() {
		return ElkDatatypeMap.XSD_DATE_TIME;
	}
}
