/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.util;
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.handlers.BaseElkDatatypeVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.BigRational;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A collection of static methods for parsing various kinds of typed literals.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class LiteralParser {

	private static final Logger LOGGER_ = LoggerFactory
			.getLogger(LiteralParser.class);
	private static final BigInteger BI_MAX_INTEGER = BigInteger
			.valueOf(Integer.MAX_VALUE);
	private static final BigInteger BI_MIN_INTEGER = BigInteger
			.valueOf(Integer.MIN_VALUE);
	private static final BigInteger BI_MAX_LONG = BigInteger
			.valueOf(Long.MAX_VALUE);
	private static final BigInteger BI_MIN_LONG = BigInteger
			.valueOf(Long.MIN_VALUE);
	private static final DatatypeFactory datatypeFactory_;
	public static final XMLGregorianCalendar START_OF_TIME;
	public static final XMLGregorianCalendar END_OF_TIME;

	static {
		try {
			datatypeFactory_ = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			LOGGER_.error("Could not initialize javax.xml.datatype.DatatypeFactory!");
			throw new RuntimeException(e);
		}

		START_OF_TIME = datatypeFactory_.newXMLGregorianCalendar(
				Integer.MIN_VALUE + 1, 1, 1, 0, 0, 0, 0, 0);
		END_OF_TIME = datatypeFactory_.newXMLGregorianCalendar(
				Integer.MAX_VALUE, 12, 31, 23, 59, 59, 0, 0);
	}	

	public static String[] parseStringLiteral(String lexicalForm) {
		int lastAt = lexicalForm.lastIndexOf('@');
		
		if (lastAt != -1) {
			String string = lexicalForm.substring(0, lastAt);
			String languageTag = lexicalForm.substring(lastAt + 1);
			return new String[] { string, languageTag };
		} else {
			return new String[] { lexicalForm };
		}
	}
	
	
	public static XMLGregorianCalendar parseDateTime(final String lexForm) {
		return datatypeFactory_.newXMLGregorianCalendar(lexForm.trim());
	}

	//does some extra type check
	public static XMLGregorianCalendar parseDateTime(final String lexForm, final ElkDatatype datatype) {
		return datatype.accept(new BaseElkDatatypeVisitor<XMLGregorianCalendar>() {

			@Override
			public XMLGregorianCalendar visit(DateTimeDatatype datatype) {
				return datatypeFactory_.newXMLGregorianCalendar(lexForm.trim());
			}

			@Override
			public XMLGregorianCalendar visit(DateTimeStampDatatype datatype) {
				return datatypeFactory_.newXMLGregorianCalendar(lexForm.trim());
			}
			
		});
		
	}
	
	public static Number parseNumber(final ElkLiteral literal) {
		return literal.getDatatype().accept(
				new BaseElkDatatypeVisitor<Number>() {

					@Override
					public Number visit(RationalDatatype datatype) {
						return parseRational(literal.getLexicalForm());
					}

					@Override
					public Number visit(DecimalDatatype datatype) {
						return parseDecimal(literal.getLexicalForm());
					}

					@Override
					public Number visit(IntegerDatatype datatype) {
						return parseInteger(literal.getLexicalForm());
					}

					@Override
					public Number visit(NonNegativeIntegerDatatype datatype) {
						return parseNonNegativeInteger(literal.getLexicalForm());
					}

				});
	}

	public static Number parseNonNegativeInteger(String literal) {
		//TODO check that the value is non negative
		return parseInteger(literal);
	}
	
	public static Number parseInteger(String literal) {
		//TODO perhaps makes sense to first try to parse java.lang.Integer?
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

	public static Number parseDecimal(String literal) {
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

	public static Number parseRational(String literal) {
		int divisorIndx = literal.indexOf('/');
		BigInteger numerator = null, denominator = BigInteger.ONE;

		if (divisorIndx == -1) {
			LOGGER_.warn("Rational number is missing / " + literal);
			numerator = DatatypeConverter.parseInteger(literal);
		}
		else {
			numerator = DatatypeConverter.parseInteger(literal
					.substring(0, divisorIndx));
			denominator = DatatypeConverter.parseInteger(literal
					.substring(divisorIndx + 1));	
		}

		if (denominator.equals(BigInteger.ZERO)) {
			throw new IllegalArgumentException("Denominator is 0: " + literal);
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
			return new BigRational(numerator, denominator);
		}
	}

	public static byte[] parseBase64(String lexForm) {
		return DatatypeConverter.parseBase64Binary(lexForm);
	}

	public static byte[] parseHexBinary(String lexForm) {
		return DatatypeConverter.parseHexBinary(lexForm);
	}


	public static URI parseUri(String lexicalForm) throws URISyntaxException {
		return new URI(lexicalForm);
	}
}
