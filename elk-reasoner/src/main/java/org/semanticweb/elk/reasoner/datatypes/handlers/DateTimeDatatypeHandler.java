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
package org.semanticweb.elk.reasoner.datatypes.handlers;

import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.datatypes.util.LiteralParser;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.AbstractDateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeStampInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeValue;

/**
 * xsd:dateTime and xsd:dateTimeStamp datatype handler.
 * <p>
 * Similar to {@link NumericDatatypeHandler}. Uses {@link XMLGregorianCalendar}
 * to represent time instances and compare them.
 * <p>
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class DateTimeDatatypeHandler extends 	AbstractDatatypeHandler {


	@Override
	protected ElkDatatypeVisitor<ValueSpace<?>> getLiteralConverter(
			final ElkLiteral literal) {
		return new BaseElkDatatypeVisitor<ValueSpace<?>>() {

			@Override
			public ValueSpace<?> visit(DateTimeDatatype datatype) {
				XMLGregorianCalendar value = LiteralParser.parseDateTime(literal.getLexicalForm());
				
				return new DateTimeValue(value);
			}

			@Override
			public ValueSpace<?> visit(
					DateTimeStampDatatype datatype) {
				XMLGregorianCalendar value = LiteralParser.parseDateTime(literal.getLexicalForm());
				
				return new DateTimeValue(value);
			}
			
		};
	}

	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return dataRangeConverter_;
	}
	
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseElkDataRangeVisitor<ValueSpace<?>>() {

		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return elkDatatype.accept(new BaseElkDatatypeVisitor<EntireValueSpace<?>>() {
						@Override
						public EntireValueSpace<?> visit(
								DateTimeDatatype datatype) {
							return EntireValueSpace.ENTIRE_DATE_TIME;
						}

						@Override
						public EntireValueSpace<?> visit(
								DateTimeStampDatatype datatype) {
							return EntireValueSpace.ENTIRE_DATE_TIME_STAMP;
						}

					});
		}

		@Override
		public ValueSpace<?> visit(
				ElkDatatypeRestriction elkDatatypeRestriction) {
			XMLGregorianCalendar lowerBound = LiteralParser.START_OF_TIME;
			XMLGregorianCalendar upperBound = LiteralParser.END_OF_TIME;
			boolean lowerInclusive = true, upperInclusive = true;
			ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
					.getFacetRestrictions()) {
				ElkDatatype restrictionDatatype = facetRestriction
						.getRestrictionValue().getDatatype();
				XMLGregorianCalendar restrictionValue = LiteralParser.parseDateTime(facetRestriction.getRestrictionValue()
						.getLexicalForm(), restrictionDatatype); 

				switch (PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet())) {
				case XSD_MIN_INCLUSIVE: // >=
					if (restrictionValue.compare(lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = true;
					}
					break;
				case XSD_MIN_EXCLUSIVE: // >
					if (restrictionValue.compare(lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = false;
					}
					break;
				case XSD_MAX_INCLUSIVE: // <=
					if (restrictionValue.compare(upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = true;
					}
					break;
				case XSD_MAX_EXCLUSIVE: // <
					if (restrictionValue.compare(upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = false;
					}
					break;
				default:
					break;
				}
			}
			
			final XMLGregorianCalendar lb = lowerBound;
			final XMLGregorianCalendar ub = upperBound;
			final boolean li = lowerInclusive, ui = upperInclusive;
			
			AbstractDateTimeInterval<?> valueSpace = datatype.accept(new BaseElkDatatypeVisitor<AbstractDateTimeInterval<?>>() {

				@Override
				public AbstractDateTimeInterval<?> visit(DateTimeDatatype datatype) {
					return new DateTimeInterval(lb, li, ub, ui);
				}

				@Override
				public AbstractDateTimeInterval<?> visit(DateTimeStampDatatype datatype) {
					return new DateTimeStampInterval(lb, li, ub, ui);
				}
				
			});

			if (valueSpace.isUnipointInterval()) {
				// specified restriction implies a single xsd:dateTime or xsd:dateTimeStamp value
				return new DateTimeValue(valueSpace.lowerBound);
			} else {
				return valueSpace;
			}
		}
	};

}
