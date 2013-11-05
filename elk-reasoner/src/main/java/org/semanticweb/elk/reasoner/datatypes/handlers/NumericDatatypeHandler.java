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
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.reasoner.datatypes.numbers.BigRational;
import org.semanticweb.elk.reasoner.datatypes.numbers.NegativeInfinity;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;
import org.semanticweb.elk.reasoner.datatypes.numbers.PositiveInfinity;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.NumericIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.NumericValue;

/**
 * owl:real, owl:rational, xsd:decimal, xsd:integer and xsd:nonNegativeInteger
 * datatype handler
 * <p>
 * Datatype expressions are converted to interval presentation with lower and
 * upper bounds or single numeric value.
 * <p>
 * Uses {@link NumericIntervalValueSpace} and {@link NumericValue} to represent
 * datatype restrictions
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class NumericDatatypeHandler extends AbstractDatatypeHandler {

	static final Logger LOGGER_ = LoggerFactory
		.getLogger(NumericDatatypeHandler.class);
	private final NumericValueParser parser_ = new NumericValueParser();
	
	private static final BigInteger BI_MAX_INTEGER = BigInteger
		.valueOf(Integer.MAX_VALUE);
	private static final BigInteger BI_MIN_INTEGER = BigInteger
		.valueOf(Integer.MIN_VALUE);
	private static final BigInteger BI_MAX_LONG = BigInteger
		.valueOf(Long.MAX_VALUE);
	private static final BigInteger BI_MIN_LONG = BigInteger
		.valueOf(Long.MIN_VALUE);
	private final NumberComparator comparator = NumberComparator.INSTANCE;

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		String lexicalForm = elkLiteral.getLexicalForm();
		ElkDatatype datatype = elkLiteral.getDatatype();
		return new NumericValue(datatype, datatype.accept(parser_, lexicalForm));
	}

	@Override
	public ValueSpace visit(ElkDatatype elkDatatype) {
		return new EntireValueSpace(elkDatatype);
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		Number lowerBound, upperBound;
		boolean lowerInclusive, upperInclusive;

		ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

		if (datatype == ElkDatatypeMap.get(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get())) {
			// [0 ... +Inf]
			lowerBound = Integer.valueOf(0);
			upperBound = PositiveInfinity.INSTANCE;
			lowerInclusive = true;
			upperInclusive = true;
		} else {
			// [-Inf ... +Inf]
			lowerBound = NegativeInfinity.INSTANCE;
			upperBound = PositiveInfinity.INSTANCE;
			lowerInclusive = true;
			upperInclusive = true;
		}

		// process all facet restrictions
		List<? extends ElkFacetRestriction> facetRestrictions = elkDatatypeRestriction
			.getFacetRestrictions();
		for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction
				.getConstrainingFacet().getFullIriAsString());
			ElkDatatype restrictionDatatype = facetRestriction.getRestrictionValue().getDatatype();
			Number restrictionValue = restrictionDatatype.accept(parser_, facetRestriction
				.getRestrictionValue().getLexicalForm());

			switch (facet) {
				case MIN_INCLUSIVE: // >=
					if (comparator.compare(restrictionValue, lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = true;
					}
					break;
				case MIN_EXCLUSIVE: // >
					if (comparator.compare(restrictionValue, lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = false;
					}
					break;
				case MAX_INCLUSIVE: // <=
					if (comparator.compare(restrictionValue, upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = true;
					}
					break;
				case MAX_EXCLUSIVE: // <
					if (comparator.compare(restrictionValue, upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = false;
					}
					break;
			default:
				break;
			}
		}

		// build representing interval
		NumericIntervalValueSpace valueSpace = new NumericIntervalValueSpace(
			datatype, lowerBound, lowerInclusive, upperBound,
			upperInclusive);

		if (valueSpace.isEmptyInterval()) {
			// specified restrictions implies empty value (owl:Nothing)
			return EmptyValueSpace.INSTANCE;
		} else {
			if (valueSpace.isUnipointInterval()) {
				// specified restriction implies single numeric value
				return new NumericValue(datatype, valueSpace.lowerBound);
			} else {
				return valueSpace;
			}
		}
	}

	private class NumericValueParser extends DatatypeValueParser<Number, String> {

		@Override
		public Number parse(RationalDatatype datatype, String param) {
			return parseRational(param);
		}

		@Override
		public Number parse(DecimalDatatype datatype, String param) {
			return parseDecimal(param);
		}

		@Override
		public Number parse(IntegerDatatype datatype, String param) {
			return parseInteger(param);
		}

		@Override
		public Number parse(NonNegativeIntegerDatatype datatype, String param) {
			return parseInteger(param);
		}

		private Number parseRational(String literal) {
			int divisorIndx = literal.indexOf('/');
			if (divisorIndx == -1) {
				LOGGER_.warn("Rational number is missing /");
			}
			BigInteger numerator = DatatypeConverter.parseInteger(literal.substring(0, divisorIndx));
			BigInteger denominator = DatatypeConverter.parseInteger(literal.substring(divisorIndx + 1));
			if (denominator.compareTo(BigInteger.ZERO) <= 0) {
				LOGGER_.warn("Denominator is 0");
			}
			BigInteger commonDevisor = numerator.gcd(denominator);
			numerator = numerator.divide(commonDevisor);
			denominator = denominator.divide(commonDevisor);
			if (denominator.equals(BigInteger.ONE)) {
				int numeratorBitCount = numerator.bitCount();
				if (numeratorBitCount <= 32) {
					return numerator.intValue();
				}
				if (numeratorBitCount <= 64) {
					return numerator.longValue();
				}
				return numerator;
			}
			try {
				return new BigDecimal(numerator)
					.divide(new BigDecimal(denominator));
			} catch (ArithmeticException e) {
			}
			return new BigRational(numerator, denominator);
		}

		private Number parseDecimal(String literal) {
			BigDecimal value = DatatypeConverter.parseDecimal(literal);
			try {
				return value.intValueExact();
			} catch (ArithmeticException e) {
			}
			try {
				return value.longValueExact();
			} catch (ArithmeticException e) {
			}
			try {
				return value.toBigIntegerExact();
			} catch (ArithmeticException e) {
			}
			return value.stripTrailingZeros();
		}

		private Number parseInteger(String literal) {
			BigInteger value = DatatypeConverter.parseInteger(literal);
			if (value.compareTo(BI_MIN_INTEGER) >= 0
				&& value.compareTo(BI_MAX_INTEGER) <= 0) {
				return Integer.valueOf(value.intValue());
			}
			if (value.compareTo(BI_MIN_LONG) >= 0
				&& value.compareTo(BI_MAX_LONG) <= 0) {
				return Long.valueOf(value.longValue());
			}
			return value;
		}
	}
}
