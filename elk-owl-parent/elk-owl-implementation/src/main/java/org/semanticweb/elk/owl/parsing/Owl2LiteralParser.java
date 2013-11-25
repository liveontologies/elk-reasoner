/**
 * 
 */
package org.semanticweb.elk.owl.parsing;
/*
 * #%L
 * ELK OWL Model Implementation
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
import java.net.URISyntaxException;

import javax.xml.datatype.XMLGregorianCalendar;

import org.semanticweb.elk.owl.implementation.literals.ElkAnyUriLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkBase64BinaryLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkDateTimeLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkDateTimeStampLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkDecimalLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkHexBinaryLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkIntLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkIntegerLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkLongLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkNameLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkNcNameLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkNmTokenLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkNormalizedStringLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkPlainLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkRationalLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkSimpleLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkStringLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkTokenLiteralImpl;
import org.semanticweb.elk.owl.implementation.literals.ElkUnsupportedLiteralImpl;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.BigRational;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DateTimeStampDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.LiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.StringDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.UndefinedDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.XmlLiteralDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.visitors.ElkDatatypeParser;

/**
 * The basic parser of OWL literals (not all datatypes may be supported).
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class Owl2LiteralParser implements ElkDatatypeParser<ElkLiteral, String, Owl2ParseException> {

	public ElkLiteral createLiteral(String lexForm, ElkDatatype datatype) throws Owl2ParseException {
		return datatype.accept(this, lexForm);
	}
	
	public ElkLiteral createPlainLiteral(String string, String lang) {
		if (lang == null || lang.isEmpty()) {
			return createPlainLiteral(string);
		}
		
		return new ElkPlainLiteralImpl(string, lang); 
	}
	
	public ElkLiteral createPlainLiteral(String string) {
		String[] pair = LiteralParser.parseStringLiteral(string);
		
		return pair.length > 1 ? new ElkPlainLiteralImpl(pair[0], pair[1]) : new ElkSimpleLiteralImpl(pair[0]);
	}

	@Override
	public ElkLiteral parse(LiteralDatatype datatype, String lexForm) {
		return new ElkLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(DateTimeDatatype datatype, String lexForm) {
		XMLGregorianCalendar dt = LiteralParser.parseDateTime(lexForm);
		return new ElkDateTimeLiteralImpl(lexForm, dt);
	}

	@Override
	public ElkLiteral parse(DateTimeStampDatatype datatype, String lexForm) {
		XMLGregorianCalendar dt = LiteralParser.parseDateTime(lexForm);
		return new ElkDateTimeStampLiteralImpl(lexForm, dt);
	}

	@Override
	public ElkLiteral parse(Base64BinaryDatatype datatype, String lexForm) {
		return new ElkBase64BinaryLiteralImpl(lexForm, LiteralParser.parseBase64(lexForm));
	}

	@Override
	public ElkLiteral parse(HexBinaryDatatype datatype, String lexForm) {
		return new ElkHexBinaryLiteralImpl(lexForm, LiteralParser.parseHexBinary(lexForm));
	}

	@Override
	public ElkLiteral parse(AnyUriDatatype datatype, String lexForm) throws Owl2ParseException {
		try {
			return new ElkAnyUriLiteralImpl(lexForm, LiteralParser.parseUri(lexForm));
		} catch (URISyntaxException e) {
			throw new Owl2ParseException();
		}
	}

	@Override
	public ElkLiteral parse(RealDatatype datatype, String lexForm) throws Owl2ParseException {
		throw new Owl2ParseException("owl:real has no lexical representation in OWL 2");
	}

	@Override
	public ElkLiteral parse(RationalDatatype datatype, String lexForm) throws Owl2ParseException {
		return createNumericLiteral(LiteralParser.parseRational(lexForm), lexForm);
	}

	@Override
	public ElkLiteral parse(DecimalDatatype datatype, String lexForm) throws Owl2ParseException {
		return createNumericLiteral(LiteralParser.parseDecimal(lexForm), lexForm);
	}

	@Override
	public ElkLiteral parse(IntegerDatatype datatype, String lexForm) throws Owl2ParseException {
		return createNumericLiteral(LiteralParser.parseInteger(lexForm), lexForm);
	}

	@Override
	public ElkLiteral parse(NonNegativeIntegerDatatype datatype, String lexForm) throws Owl2ParseException {
		return createNumericLiteral(LiteralParser.parseNonNegativeInteger(lexForm), lexForm);
	}

	@Override
	public ElkLiteral parse(PlainLiteralDatatype datatype, String lexForm) {
		String[] parsed = LiteralParser.parseStringLiteral(lexForm);
		
		return parsed.length > 1 ? createPlainLiteral(parsed[0], parsed[1]) : createPlainLiteral(parsed[0]);
	}

	@Override
	public ElkLiteral parse(StringDatatype datatype, String lexForm) {
		return new ElkStringLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(NormalizedStringDatatype datatype, String lexForm) {
		return new ElkNormalizedStringLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(TokenDatatype datatype, String lexForm) {
		return new ElkTokenLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(NameDatatype datatype, String lexForm) {
		return new ElkNameLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(NcNameDatatype datatype, String lexForm) {
		return new ElkNcNameLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(NmTokenDatatype datatype, String lexForm) {
		return new ElkNmTokenLiteralImpl(lexForm);
	}

	@Override
	public ElkLiteral parse(XmlLiteralDatatype datatype, String lexForm)  throws Owl2ParseException {
		throw new Owl2ParseException("rdfs:XMLLiteral has no lexical representation in OWL 2");
	}

	@Override
	public ElkLiteral parse(UndefinedDatatype datatype, String lexForm) {
		return new ElkUnsupportedLiteralImpl(lexForm, datatype);
	}
	
	/*
	 * Creates the most specific kind of a numerical literal depending on what kind of Number is passed as the parameter. 
	 */
	private ElkLiteral createNumericLiteral(Number number, String lexForm) {
		switch (NumberUtils.getRuntimeType(number)) {
		case Decimal:
			return new ElkDecimalLiteralImpl(lexForm, (BigDecimal) number);
		case Int:
			return new ElkIntLiteralImpl(lexForm, number.intValue());
		case Integer:
			return new ElkIntegerLiteralImpl(lexForm, (BigInteger) number);
		case Long:
			return new ElkLongLiteralImpl(lexForm, number.longValue());
		case Infinity:
			throw new IllegalArgumentException("Infinity does not have a lexical representation");
		case Rational:
			return new ElkRationalLiteralImpl(lexForm, (BigRational) number);
		default:
			throw new IllegalArgumentException("Unrecognized number type "
					+ number);
		}
	}
}
