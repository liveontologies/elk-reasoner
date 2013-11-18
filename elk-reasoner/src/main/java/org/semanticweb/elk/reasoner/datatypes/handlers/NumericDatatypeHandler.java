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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.datatypes.RealDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.datatypes.util.LiteralParser;
import org.semanticweb.elk.reasoner.datatypes.util.NumberUtils;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.ArbitraryIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.BigRational;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.IntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RealInterval;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;

/**
 * owl:real, owl:rational, xsd:decimal, xsd:integer and xsd:nonNegativeInteger
 * datatype handler
 * <p>
 * Datatype expressions are converted to interval presentation with lower and
 * upper bounds or a single numeric value.
 * <p>
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public class NumericDatatypeHandler extends AbstractDatatypeHandler {

	private final static Comparator<Number> comparator_ = NumberUtils.COMPARATOR;

	//TODO this certainly isn't the most beautiful function ever.... 
	private static PointValue<? extends RealDatatype, Number> numberToPointValue(Number number) {
	
		switch(NumberUtils.getRuntimeType(number)) {
		case Decimal:
			return new DecimalValue((BigDecimal)number);
		case Int:
			return comparator_.compare(number, 0) >= 0 ? new NonNegativeIntegerValue(number.intValue()) : new IntegerValue(number.intValue());
		case Integer:
			return comparator_.compare(number, 0) >= 0 ? new NonNegativeIntegerValue((BigInteger)number) : new IntegerValue((BigInteger)number);
		case Long:
			return comparator_.compare(number, 0) >= 0 ? new NonNegativeIntegerValue(number.longValue()) : new IntegerValue(number.longValue());
		case Infinity:
			return comparator_.compare(number, 0) >= 0 ? new NonNegativeIntegerValue(NumberUtils.toInfinity(number)) : new IntegerValue(NumberUtils.toInfinity(number));
		case Rational:
			return new RationalValue((BigRational)number);
		default:
			throw new IllegalArgumentException("Unrecognized number type " + number);
		}
	}
	
	//TODO avoid creating this visitor every time, cache it and pass the literal as a parameter into it
	@Override
	protected ElkDatatypeVisitor<ValueSpace<?>> getLiteralConverter(final ElkLiteral literal) {
		return new BaseElkDatatypeVisitor<ValueSpace<?>>() {

			@Override
			public ValueSpace<? extends RealDatatype> visit(
					RationalDatatype datatype) {
				Number num = LiteralParser.parseRational(literal.getLexicalForm());
				
				return numberToPointValue(num);
			}

			@Override
			public ValueSpace<? extends RealDatatype> visit(
					DecimalDatatype datatype) {
				Number num = LiteralParser.parseDecimal(literal.getLexicalForm());
				
				return numberToPointValue(num);
			}

			@Override
			public ValueSpace<? extends RealDatatype> visit(
					IntegerDatatype datatype) {
				Number num = LiteralParser.parseInteger(literal.getLexicalForm());
				
				return numberToPointValue(num);
			}

			@Override
			public ValueSpace<? extends RealDatatype> visit(
					NonNegativeIntegerDatatype datatype) {
				Number num = LiteralParser.parseInteger(literal.getLexicalForm());
				
				return numberToPointValue(num);	
			}
			
		};
	}

	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() {
		return dataRangeConverter_;
	}

	/**
	 * a stateless visitor for converting data ranges into numerical value spaces.
	 */
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseElkDataRangeVisitor<ValueSpace<?>>() {
		/**
		 * a stateless visitor for creating entire value spaces for numerical datatypes.
		 */
		private final ElkDatatypeVisitor<EntireValueSpace<?>> entireValueSpaceCreator_ = new BaseElkDatatypeVisitor<EntireValueSpace<?>>() {

			@Override
			public EntireValueSpace<?> visit(
					RealDatatype datatype) {
				return EntireValueSpace.OWL_REAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					RationalDatatype datatype) {
				return EntireValueSpace.OWL_RATIONAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					DecimalDatatype datatype) {
				return EntireValueSpace.XSD_DECIMAL;
			}

			@Override
			public EntireValueSpace<?> visit(
					IntegerDatatype datatype) {
				return EntireValueSpace.XSD_INTEGER;
			}
			
			@Override
			public EntireValueSpace<?> visit(
					NonNegativeIntegerDatatype datatype) {
				return EntireValueSpace.XSD_NON_NEGATIVE_INTEGER;
			}
			
		};
		
		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return elkDatatype.accept(entireValueSpaceCreator_);
		}

		@Override
		public ValueSpace<?> visit(
				ElkDatatypeRestriction elkDatatypeRestriction) {
			Number lowerBound = null, upperBound = null;
			boolean lowerInclusive = false, upperInclusive = false;
			ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

			// process all facet restrictions
			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
					.getFacetRestrictions()) {
				Number restrictionValue = LiteralParser.parseNumber(facetRestriction
						.getRestrictionValue()); 

				switch (PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet())) {
					case XSD_MIN_INCLUSIVE: // >=
						if (lowerBound == null || comparator_.compare(restrictionValue, lowerBound) >= 0) {
							lowerBound = restrictionValue;
							lowerInclusive = true;
						}
						break;
					case XSD_MIN_EXCLUSIVE: // >
						if (lowerBound == null ||comparator_.compare(restrictionValue, lowerBound) >= 0) {
							lowerBound = restrictionValue;
							lowerInclusive = false;
						}
						break;
					case XSD_MAX_INCLUSIVE: // <=
						if (upperBound == null ||comparator_.compare(restrictionValue, upperBound) <= 0) {
							upperBound = restrictionValue;
							upperInclusive = true;
						}
						break;
					case XSD_MAX_EXCLUSIVE: // <
						if (upperBound == null ||comparator_.compare(restrictionValue, upperBound) <= 0) {
							upperBound = restrictionValue;
							upperInclusive = false;
						}
						break;
				default:
					throw new ElkIndexingException("Unsupported constraining facet: " + facetRestriction.getConstrainingFacet());
				}
			}
			// set default bounds, if needed
			if (lowerBound == null) {
				lowerBound = datatype == ElkDatatypeMap.XSD_NON_NEGATIVE_INTEGER ? 0 :  NumberUtils.NEGATIVE_INFINITY;
				lowerInclusive = true;
			}
			
			upperBound = upperBound == null ? NumberUtils.POSITIVE_INFINITY : upperBound;
			
			PointValue<?, Number> lowerPoint = numberToPointValue(lowerBound);
			// validate bound datatypes
			validateFacetValue(elkDatatypeRestriction, lowerPoint.getDatatype(), lowerPoint.toString());
			
			// build representing interval				
			if (isUnitInterval(lowerBound, lowerInclusive, upperBound, upperInclusive)) {
				return lowerPoint;
			} else {
				
				PointValue<?, Number> upperPoint = numberToPointValue(lowerBound);
				final Number lb = lowerBound, ub = upperBound;
				final boolean li = lowerInclusive, ui = upperInclusive;

				// make sure that restriction values belong to the value space of the data range
				validateFacetValue(elkDatatypeRestriction, upperPoint.getDatatype(), upperPoint.toString());
				
				return datatype.accept(new BaseElkDatatypeVisitor<ValueSpace<?>>() {

					@Override
					public ValueSpace<?> visit(RealDatatype datatype) {
						return new RealInterval(lb, li, ub, ui);
					}
					
					@Override
					public ValueSpace<?> visit(RationalDatatype datatype) {
						return new RationalInterval(lb, li, ub, ui);
					}

					@Override
					public ValueSpace<?> visit(DecimalDatatype datatype) {
						return new DecimalInterval(lb, li, ub, ui);
					}

					@Override
					public ValueSpace<?> visit(IntegerDatatype datatype) {
						if (comparator_.compare(lb, 0) >= 0) {
							return new NonNegativeIntegerInterval(lb, li, ub, ui);
						}
						else {
							return new ArbitraryIntegerInterval(lb, li, ub, ui);
						} 
					}

					@Override
					public ValueSpace<?> visit(
							NonNegativeIntegerDatatype datatype) {
						
						boolean lessThanZero = comparator_.compare(lb, 0) < 0;
						
						return new NonNegativeIntegerInterval(lessThanZero ? 0 : lb, lessThanZero ? true : li, ub, ui);
					}
					
				});
			}
		}

		private boolean isUnitInterval(Number lowerBound,
				boolean lowerInclusive, Number upperBound,
				boolean upperInclusive) {
			return lowerInclusive && upperInclusive
					&& comparator_.compare(lowerBound, upperBound) == 0;
		}
		
	};
}
