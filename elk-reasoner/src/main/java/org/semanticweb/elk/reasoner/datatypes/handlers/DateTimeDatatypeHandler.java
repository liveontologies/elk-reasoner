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

import org.semanticweb.elk.owl.implementation.literals.ElkDateTimeStampLiteralImpl;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkDateTimeLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkDateTimeStampLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.parsing.DateTimeUtils;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.BaseElkLiteralVisitor;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.AbstractDateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeStampInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;

/**
 * xsd:dateTime and xsd:dateTimeStamp datatype handler.
 * <p>
 * Similar to {@link NumericDatatypeHandler}. Uses {@link XMLGregorianCalendar}
 * to represent time instances and intervals.
 * <p>
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class DateTimeDatatypeHandler extends 	AbstractDatatypeHandler {

	private final ElkLiteralVisitor<PointValue<?, XMLGregorianCalendar>> literalConverter_ = new BaseLiteralConverter<XMLGregorianCalendar>(){

		@Override
		public PointValue<?, XMLGregorianCalendar> visit(ElkDateTimeLiteral literal) {
			return new DateTimeValue(literal.getDateTime());
		}

		@Override
		public PointValue<?, XMLGregorianCalendar> visit(ElkDateTimeStampLiteral literal) {
			return new DateTimeValue(literal.getDateTime());
		}

	};
	
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseDataRangeConverter() {

		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return elkDatatype.accept(new BaseDatatypeVisitor<EntireValueSpace<?>>() {
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
			ElkDateTimeLiteral lowerBound = new ElkDateTimeStampLiteralImpl("-INF", DateTimeUtils.START_OF_TIME);
			ElkDateTimeLiteral upperBound = new ElkDateTimeStampLiteralImpl("+INF", DateTimeUtils.END_OF_TIME);
			boolean lowerInclusive = true, upperInclusive = true;
			ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
					.getFacetRestrictions()) {
				ElkDateTimeLiteral bound = asDateTimeLiteral(facetRestriction.getRestrictionValue()); 

				switch (PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet())) {
				case XSD_MIN_INCLUSIVE: // >=
					if (bound.getDateTime().compare(lowerBound.getDateTime()) >= 0) {
						lowerBound = bound;
						lowerInclusive = true;
					}
					break;
				case XSD_MIN_EXCLUSIVE: // >
					if (bound.getDateTime().compare(lowerBound.getDateTime()) >= 0) {
						lowerBound = bound;
						lowerInclusive = false;
					}
					break;
				case XSD_MAX_INCLUSIVE: // <=
					if (bound.getDateTime().compare(upperBound.getDateTime()) <= 0) {
						upperBound = bound;
						upperInclusive = true;
					}
					break;
				case XSD_MAX_EXCLUSIVE: // <
					if (bound.getDateTime().compare(upperBound.getDateTime()) <= 0) {
						upperBound = bound;
						upperInclusive = false;
					}
					break;
				default:
					break;
				}
			}
			
			final boolean li = lowerInclusive, ui = upperInclusive;
			final ElkDateTimeLiteral lb = lowerBound;
			final ElkDateTimeLiteral ub = upperBound;
			// validation
			validateFacetValue(elkDatatypeRestriction, lb.getDatatype(), lb.toString());
			validateFacetValue(elkDatatypeRestriction, ub.getDatatype(), ub.toString());
			
			AbstractDateTimeInterval<?> valueSpace = datatype.accept(new BaseDatatypeVisitor<AbstractDateTimeInterval<?>>() {

				@Override
				public AbstractDateTimeInterval<?> visit(DateTimeDatatype datatype) {
					return new DateTimeInterval(lb.getDateTime(), li, ub.getDateTime(), ui);
				}

				@Override
				public AbstractDateTimeInterval<?> visit(DateTimeStampDatatype datatype) {
					return new DateTimeStampInterval(lb.getDateTime(), li, ub.getDateTime(), ui);
				}
				
			});
			

			if (valueSpace.isUnipointInterval()) {
				// specified restriction implies a single xsd:dateTime or xsd:dateTimeStamp value
				return new DateTimeValue(lb.getDateTime());
			} else {
				return valueSpace;
			}
		}

		private ElkDateTimeLiteral asDateTimeLiteral(ElkLiteral literal) {
			return literal.accept(new BaseElkLiteralVisitor<ElkDateTimeLiteral>(){

				@Override
				protected ElkDateTimeLiteral defaultVisit(ElkLiteral elkLiteral) {
					throw new ElkUnexpectedIndexingException("xsd:dateTime or xsd:dateTimeStamp literal expected, gotted: " + elkLiteral);
				}

				@Override
				public ElkDateTimeLiteral visit(ElkDateTimeLiteral elkLiteral) {
					return elkLiteral;
				}

				@Override
				public ElkDateTimeLiteral visit(ElkDateTimeStampLiteral elkLiteral) {
					return elkLiteral;
				}
				
			});
			//return null;
		}
	};



	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return dataRangeConverter_;
	}

	@Override
	public PointValue<?, ?> createValueSpace(ElkLiteral literal) {
		return literal.accept(literalConverter_);
	}

}
