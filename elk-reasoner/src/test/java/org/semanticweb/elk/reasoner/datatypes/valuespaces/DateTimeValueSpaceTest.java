/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeValue;

/**
 * Tests for {@link DateTimeValue} and {@link DateTimeInterval}
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DateTimeValueSpaceTest extends AbstractValueSpaceTest {

	@Test
	public void emptySpaces() throws Exception {
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T12:59:59\"^^xsd:dateTime xsd:maxExclusive \"2000-12-31T12:59:59\"^^xsd:dateTime)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minExclusive \"2000-12-31T12:59:59\"^^xsd:dateTime xsd:maxInclusive \"2000-12-31T12:59:59\"^^xsd:dateTime)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T00:00:01\"^^xsd:dateTime xsd:maxInclusive \"2000-12-31\"^^xsd:dateTime)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2001-01-01T05:00:00\"^^xsd:dateTime xsd:maxExclusive \"2000-12-31T23:59:59-05:00\"^^xsd:dateTime)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2001-01-01T05:00:00\"^^xsd:dateTime xsd:maxExclusive \"2000-12-31T23:59:59-06:00\"^^xsd:dateTime)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T00:00:00-01:00\"^^xsd:dateTime xsd:maxInclusive \"2000-12-31T00:00:00+01:00\"^^xsd:dateTime)")
				.isEmpty());
		
	}

	@Test
	public void nonEmptySpaces() throws Exception {
		assertFalse(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T12:59:59\"^^xsd:dateTime xsd:maxExclusive \"2001-01-01T00:00:00\"^^xsd:dateTime)")
				.isEmpty());
		assertFalse(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T00:00:00\"^^xsd:dateTime xsd:maxInclusive \"2000-12-31T00:00:00\"^^xsd:dateTime)")
				.isEmpty());
		assertFalse(dataRange(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"2000-12-31T00:00:00Z\"^^xsd:dateTime xsd:maxInclusive \"2000-12-31T00:00:00Z\"^^xsd:dateTime)")
				.isEmpty());
		
	}

	@Test
	public void contains() throws Exception {
		assertTrue(contains(
				"DataOneOf(\"1956-06-25T04:00:00Z\"^^xsd:dateTime)",
				"DataOneOf(\"1956-06-25T04:00:00Z\"^^xsd:dateTimeStamp)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTimeStamp xsd:minInclusive \"1901-01-01T02:00:00+02:00\"^^xsd:dateTimeStamp xsd:maxInclusive \"2000-12-31T10:59:59-02:00\"^^xsd:dateTimeStamp)",
				"DatatypeRestriction(xsd:dateTimeStamp xsd:minExclusive \"1961-01-01T00:00:00Z\"^^xsd:dateTimeStamp xsd:maxExclusive \"1972-12-31T00:00:00Z\"^^xsd:dateTimeStamp)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-01-01T04:00:00\"^^xsd:dateTime)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-01-01T04:00:00+14:00\"^^xsd:dateTime)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"1956-01-01T04:00:00+14:00\"^^xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-01-01T04:00:00Z\"^^xsd:dateTime)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)",
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00+14:00\"^^xsd:dateTime)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"1956-01-01T04:00:00+14:00\"^^xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)",
				"DatatypeRestriction(xsd:dateTime xsd:minInclusive \"1956-01-01T04:00:00+12:00\"^^xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-12:00\"^^xsd:dateTime)"));
	}

	@Test
	public void doesNotContain() throws Exception {
		/*XMLGregorianCalendar d1 = DatatypeFactory.newInstance().newXMLGregorianCalendar("1956-01-01T04:00:00");
		XMLGregorianCalendar dMinus14 = DatatypeFactory.newInstance().newXMLGregorianCalendar("1956-01-01T04:00:00-14:00");
		XMLGregorianCalendar dPlus14 = DatatypeFactory.newInstance().newXMLGregorianCalendar("1956-01-01T04:00:00+14:00");
		
		order(d1, dMinus14);
		order(d1, dPlus14);
		order(dPlus14, dMinus14);*/
		assertFalse(contains(
				"DataOneOf(\"1956-06-25T04:00:00-05:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-06-25T10:00:00+01:00\"^^xsd:dateTime)"));
		assertFalse(contains(
				"DataOneOf(\"1956-06-25T10:00:00+01:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-06-25T04:00:00-05:00\"^^xsd:dateTime)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-01-01T04:00:00\"^^xsd:dateTime)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00\"^^xsd:dateTime)",
				"DataOneOf(\"1956-01-01T04:00:00+14:00\"^^xsd:dateTime)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00+14:00\"^^xsd:dateTime)",
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00-14:00\"^^xsd:dateTime)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00\"^^xsd:dateTime)",
				"DatatypeRestriction(xsd:dateTime xsd:maxInclusive \"1956-01-01T04:00:00+14:00\"^^xsd:dateTime)"));
		
		
	}
	
	//was used for debugging at some point
	void order(XMLGregorianCalendar d1, XMLGregorianCalendar d2) {
		switch (d1.compare(d2)) {
			case DatatypeConstants.LESSER:
				System.out.println(d1 + " < " + d2);
				break;
			case DatatypeConstants.GREATER:
				System.out.println(d1 + " > " + d2);
				break;
			case DatatypeConstants.INDETERMINATE:	
				System.out.println(d1 + " <> " + d2);
				break;
			case DatatypeConstants.EQUAL:
				System.out.println(d1 + " = " + d2);
				break;
			default:
				System.out.println(d1 + " something else " + d2);
		}
	}

	/*
	 * Tests that given a datatype restriction:
	 * 
	 * DatatypeRestriction ( Datatype {constrainingFacet restrictionValue}+ )
	 * 
	 * the restriction values always belong to the value space of the datatype.
	 * (see 7.5:
	 * "In an OWL 2 DL ontology, each pair ( Fi , vi ) must be contained in the facet space of DT"
	 * )
	 */
	@Test
	public void facetRestrictionValues() throws Exception {
		//xsd:dateTimeStamp must have TZ set
		assertFalse(tryDataRange("DatatypeRestriction(xsd:dateTimeStamp xsd:maxInclusive \"1956-01-01T04:00:00\"^^xsd:dateTime)"));
	}
	
	
}
