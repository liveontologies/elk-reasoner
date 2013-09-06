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

import static org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype.*;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.MAX_EXCLUSIVE;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.MAX_INCLUSIVE;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.MIN_EXCLUSIVE;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.MIN_INCLUSIVE;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.DateTimeIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.DateTimeValue;

/**
 * xsd:dateTime ans xsd:dateTimeStamp datatype handler
 * <p>
 * Similar to {@link NumericDatatypeHandler}. Uses {@link XMLGregorianCalendar}
 * to represent time instances and compare them.
 * <p>
 * Uses {@link DateTimeValue} and {@link DateTimeIntervalValueSpace} to
 * represent datatype restrictions
 *
 * @author Pospishnyi Olexandr
 */
public class DateTimeDatatypeHandler extends ElkDatatypeHandler {

	static final Logger LOGGER_ = LoggerFactory
			.getLogger(DateTimeDatatypeHandler.class);

	private static final DatatypeFactory datatypeFactory;

	public static final XMLGregorianCalendar START_OF_TIME;
	public static final XMLGregorianCalendar END_OF_TIME;

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			LOGGER_.error("Could not initialize DatatypeFactory!");
			throw new Error(e);
		}
		START_OF_TIME = datatypeFactory.newXMLGregorianCalendar(
				Integer.MIN_VALUE + 1, 1, 1, 0, 0, 0, 0, 0);
		END_OF_TIME = datatypeFactory.newXMLGregorianCalendar(
				Integer.MAX_VALUE, 12, 31, 23, 59, 59, 0, 0);
	}

	@Override
	public Set<ELDatatype> getSupportedDatatypes() {
		return EnumSet.of(xsd_dateTime, xsd_dateTimeStamp);
	}

	@Override
	public Set<Facet> getSupportedFacets() {
		return EnumSet.of(MIN_INCLUSIVE, MAX_INCLUSIVE, MIN_EXCLUSIVE,
				MAX_EXCLUSIVE);
	}

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		String lexicalForm = elkLiteral.getLexicalForm();
		ELDatatype datatype = elkLiteral.getDatatype().asELDatatype();
		XMLGregorianCalendar value = parse(lexicalForm, datatype);
		return new DateTimeValue(value, datatype);
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		XMLGregorianCalendar lowerBound = DateTimeDatatypeHandler.START_OF_TIME;
		XMLGregorianCalendar upperBound = DateTimeDatatypeHandler.END_OF_TIME;
		boolean lowerInclusive = true, upperInclusive = true;

		ELDatatype datatype = elkDatatypeRestriction.getDatatype().asELDatatype();

		List<? extends ElkFacetRestriction> facetRestrictions = elkDatatypeRestriction
				.getFacetRestrictions();
		for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction
					.getConstrainingFacet().getFullIriAsString());
			ELDatatype restrictionDatatype = facetRestriction
					.getRestrictionValue().getDatatype().asELDatatype();
			XMLGregorianCalendar restrictionValue = (XMLGregorianCalendar) parse(
					facetRestriction.getRestrictionValue().getLexicalForm(),
					restrictionDatatype);

			switch (facet) {
			case MIN_INCLUSIVE: // >=
				if (restrictionValue.compare(lowerBound) >= 0) {
					lowerBound = restrictionValue;
					lowerInclusive = true;
				}
				break;
			case MIN_EXCLUSIVE: // >
				if (restrictionValue.compare(lowerBound) >= 0) {
					lowerBound = restrictionValue;
					lowerInclusive = false;
				}
				break;
			case MAX_INCLUSIVE: // <=
				if (restrictionValue.compare(upperBound) <= 0) {
					upperBound = restrictionValue;
					upperInclusive = true;
				}
				break;
			case MAX_EXCLUSIVE: // <
				if (restrictionValue.compare(upperBound) <= 0) {
					upperBound = restrictionValue;
					upperInclusive = false;
				}
				break;
			}
		}

		DateTimeIntervalValueSpace valueSpace = new DateTimeIntervalValueSpace(
				datatype, lowerBound, lowerInclusive, upperBound,
				upperInclusive);

		if (valueSpace.isEmptyInterval()) {
			// specified restrictions implies empty value (owl:Nothing)
			return EmptyValueSpace.INSTANCE;
		} else {
			if (valueSpace.isUnipointInterval()) {
				// specified restriction implies single dateTime value
				return new DateTimeValue(valueSpace.lowerBound, datatype);
			} else {
				return valueSpace;
			}
		}
	}

	private XMLGregorianCalendar parse(String literal, ELDatatype datatype) {
		switch (datatype) {
		case xsd_dateTime:
		case xsd_dateTimeStamp:
			return datatypeFactory.newXMLGregorianCalendar(literal.trim());
		default:
			LOGGER_.warn("Unsupported datetime datatype: " + datatype.iri);
			return null;
		}
	}
}
