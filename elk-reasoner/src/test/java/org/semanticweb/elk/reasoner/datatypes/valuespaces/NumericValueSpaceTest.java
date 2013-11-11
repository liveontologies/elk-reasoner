/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for subsumption, containment, and emptiness for numeric value spaces
 * (i.e. subspaces of owl:real).
 * 
 * These tests do not check for parsing errors.
 * 
 * @author Pavel Klinov
 * 
 *         pavel.klinov@uni-ulm.de
 */
public class NumericValueSpaceTest extends AbstractValueSpaceTest {
	

	@Test
	public void emptySpaces() throws Exception {
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"10\"^^xsd:integer xsd:maxInclusive \"9\"^^xsd:integer)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:integer xsd:minExclusive \"10\"^^xsd:integer xsd:maxExclusive \"11\"^^xsd:integer)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"10\"^^xsd:integer xsd:maxExclusive \"10\"^^xsd:integer)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(owl:rational xsd:minExclusive \"1/2\"^^owl:rational xsd:maxExclusive \"1/3\"^^owl:rational)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(owl:rational xsd:minExclusive \"1/2\"^^owl:rational xsd:maxExclusive \"2/4\"^^owl:rational)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:decimal xsd:minExclusive \"0.5\"^^xsd:decimal xsd:maxExclusive \"0.4\"^^xsd:decimal)")
				.isEmpty());
		assertTrue(dataRange(
				"DatatypeRestriction(xsd:decimal xsd:minExclusive \"0.5\"^^xsd:decimal xsd:maxExclusive \"0.5\"^^xsd:decimal)")
				.isEmpty());
	}

	@Test
	public void nonEmptySpaces() throws Exception {
		assertFalse(dataRange(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"10\"^^xsd:integer xsd:maxInclusive \"10\"^^xsd:integer)")
				.isEmpty());
		assertFalse(dataRange(
				"DatatypeRestriction(owl:rational xsd:minInclusive \"1/3\"^^owl:rational xsd:maxInclusive \"1/2\"^^owl:rational)")
				.isEmpty());
		assertFalse(dataRange(
				"DatatypeRestriction(owl:rational xsd:minInclusive \"1/2\"^^owl:rational xsd:maxInclusive \"2/4\"^^owl:rational)")
				.isEmpty());
		assertFalse(dataRange(
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"0.5\"^^xsd:decimal xsd:maxInclusive \"0.5\"^^xsd:decimal)")
				.isEmpty());
	}

	@Test
	public void contains() throws Exception {
		// integer intervals
		assertTrue(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"12\"^^xsd:integer)",
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"9\"^^xsd:integer xsd:maxInclusive \"12\"^^xsd:integer)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer)",
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"9\"^^xsd:integer xsd:maxInclusive \"12\"^^xsd:integer)"));
		assertTrue(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"8\"^^xsd:integer)",
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxExclusive \"9\"^^xsd:integer)"));
		// rational intervals
		assertTrue(contains(
				"DatatypeRestriction(owl:rational xsd:minInclusive \"2/3\"^^owl:rational xsd:maxInclusive \"3/2\"^^owl:rational)",
				"DatatypeRestriction(owl:rational xsd:minInclusive \"5/6\"^^owl:rational xsd:maxInclusive \"6/5\"^^owl:rational)"));
		// decimal intervals
		assertTrue(contains(
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"0.5\"^^xsd:decimal xsd:maxInclusive \"1.1\"^^xsd:decimal)",
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"1\"^^xsd:decimal xsd:maxExclusive \"1.1\"^^xsd:decimal)"));
		// integer in decimal
		assertTrue(contains(
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"8\"^^xsd:decimal xsd:maxExclusive \"9.001\"^^xsd:decimal)",
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"9\"^^xsd:integer)"));
		// integer in rational
		assertTrue(contains(
				"DatatypeRestriction(owl:rational xsd:minInclusive \"2/3\"^^owl:rational xsd:maxInclusive \"3/2\"^^owl:rational)",
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"1\"^^xsd:integer xsd:maxInclusive \"1\"^^xsd:integer)"));
		// decimal in rational
		assertTrue(contains(
				"DatatypeRestriction(owl:rational xsd:minInclusive \"8/1\"^^owl:rational xsd:maxInclusive \"9/1\"^^owl:rational)",
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"8.0\"^^xsd:decimal xsd:maxExclusive \"9.0\"^^xsd:decimal)"));
	}

	@Test
	public void doesNotContain() throws Exception {
		assertFalse(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"9\"^^xsd:integer)",
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"8\"^^xsd:decimal xsd:maxInclusive \"9\"^^xsd:decimal)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"9\"^^xsd:integer)",
				"DatatypeRestriction(owl:rational xsd:minInclusive \"17/2\"^^owl:rational xsd:maxInclusive \"17/2\"^^owl:rational)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:integer xsd:minInclusive \"8\"^^xsd:integer xsd:maxInclusive \"9\"^^xsd:integer)",
				"DatatypeRestriction(owl:rational xsd:minInclusive \"8/1\"^^owl:rational xsd:maxInclusive \"9/1\"^^owl:rational)"));
		assertFalse(contains(
				"DatatypeRestriction(xsd:decimal xsd:minInclusive \"8.0\"^^xsd:decimal xsd:maxInclusive \"9.0\"^^xsd:decimal)",
				"DatatypeRestriction(owl:rational xsd:minInclusive \"8/1\"^^owl:rational xsd:maxInclusive \"9/1\"^^owl:rational)"));
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
	@Ignore
	public void facetRestrictionValues() throws Exception {
		assertFalse(tryDataRange("DatatypeRestriction(xsd:integer xsd:minInclusive \"0.5\"^^xsd:decimal)"));
		assertFalse(tryDataRange("DatatypeRestriction(xsd:nonNegativeInteger xsd:minInclusive \"-1\"^^xsd:integer)"));
		assertFalse(tryDataRange("DatatypeRestriction(xsd:decimal xsd:minInclusive \"1/3\"^^owl:rational)"));
		assertFalse(tryDataRange("DatatypeRestriction(xsd:integer xsd:minInclusive \"5.1\"^^xsd:decimal xsd:maxExclusive \"6\"^^xsd:integer)"));
	}

}
