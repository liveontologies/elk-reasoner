/*
 * #%L
 * ELK Reasoner
 * 
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
package org.semanticweb.elk.reasoner.datatypes.enums;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.semanticweb.elk.reasoner.datatypes.numbers.BigRational;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;

/**
 * Enumeration of all known and supported datatypes. This class provides some
 * utility methods that are used by specific datatype handlers to determine
 * relations between datatypes.
 *
 * @author Pospishnyi Oleksandr
 */
public enum Datatype {

	rdfs_Literal			(null,			"http://www.w3.org/2000/01/rdf-schema#Literal"),
	xsd_dateTime			(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#dateTime"),
	xsd_dateTimeStamp		(xsd_dateTime,	"http://www.w3.org/2001/XMLSchema#dateTimeStamp"),
	xsd_base64Binary		(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#base64Binary"),
	xsd_hexBinary			(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#hexBinary"),
	xsd_anyURI				(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#anyURI"),
	owl_real				(rdfs_Literal,	"http://www.w3.org/2002/07/owl#real"),
	owl_rational			(owl_real,		"http://www.w3.org/2002/07/owl#rational"),
	xsd_decimal				(owl_rational,	"http://www.w3.org/2001/XMLSchema#decimal"),
	xsd_integer				(xsd_decimal,	"http://www.w3.org/2001/XMLSchema#integer"),
	xsd_nonNegativeInteger	(xsd_integer,	"http://www.w3.org/2001/XMLSchema#nonNegativeInteger"),
	rdf_PlainLiteral		(rdfs_Literal,	"http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"),
	xsd_string				(rdf_PlainLiteral,"http://www.w3.org/2001/XMLSchema#string"),
	xsd_normalizedString	(xsd_string,	"http://www.w3.org/2001/XMLSchema#normalizedString"),
	xsd_token				(xsd_normalizedString,"http://www.w3.org/2001/XMLSchema#token"),
	xsd_Name				(xsd_token,		"http://www.w3.org/2001/XMLSchema#Name"),
	xsd_NMTOCKEN			(xsd_token,		"http://www.w3.org/2001/XMLSchema#NMTOKEN"),
	xsd_NCName				(xsd_Name,		"http://www.w3.org/2001/XMLSchema#NCName"),
	rdf_XMLiteral			(rdfs_Literal,	"http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
	
	public final Datatype parent;
	public final String iri;

	private Datatype(Datatype parent, String iri) {
		this.parent = parent;
		this.iri = iri;
	}

	@Override
	public String toString() {
		return iri;
	}
	
	/**
	 * Check weather this datatype is derived from another
	 *
	 * @param datatype other datatype to check against
	 * @return true/false = yes/no
	 */
	public boolean isDerivedFrom(Datatype datatype) {
		Datatype parent = this.parent;
		while (parent != null) {
			if (parent == datatype) {
				return true;
			}
			parent = parent.parent;
		}
		return false;
	}
	
	/**
	 * Check weather this datatype is completely compatible with another.
	 * Being compatible means being equal to or being derived from. 
	 * 
	 * @param datatype other datatype to check against
	 * @return true/false = yes/no
	 */
	public boolean isCompatibleWith(Datatype datatype) {
		if (this == datatype) {
			return true;
		} else {
			return isDerivedFrom(datatype);
		}
	}
	
	/**
	 * Return most general compatible datatype that outlines this datatype. For
	 * example a method will return owl:real for all datatypes derived from it.
	 *
	 * @return
	 */
	public Datatype getRootValueSpaceDatatype() {
		Datatype dt = this;
		if (dt == rdfs_Literal) {
			return dt;
		}
		while (dt.parent != rdfs_Literal) {
			dt = dt.parent;
		}
		return dt;
	}
	
	/**
	 * Get all disjoint root datatypes (ones that define some family of
	 * datatypes, such as literals, numbers, dateTime, etc.)
	 *
	 * @see #getRootValueSpaceDatatype
	 * @return list of all root datatypes
	 */
	public static Set<Datatype> getRootDatatypes() {
		return EnumSet.of(xsd_dateTime, xsd_base64Binary, xsd_hexBinary, xsd_anyURI,
				owl_real, rdf_PlainLiteral, rdf_XMLiteral, rdfs_Literal);
	}
	
	/**
	 * Return an ordered list of all datatypes that constitute this datatype
	 * 
	 * @return
	 */
	public List<Datatype> buildParentChain() {
		ArrayList<Datatype> chain = new ArrayList<Datatype>(5);
		Datatype parent = this.parent;
		while (parent != null) {
			chain.add(parent);
			parent = parent.parent;
		}
		return chain;
	}
	
	/**
	 * Get Datatype enum element based on it's IRI
	 * 
	 * @param iri full datatype IRI
	 * @return {@link Datatype}
	 */
	public static Datatype getByIri(String iri) {
		for (Datatype datatype : Datatype.values()) {
			if (iri.equals(datatype.iri)) {
				return datatype;
			}
		}
		return null;
	}

	/**
	 * Get most specific datatype for specified number
	 * 
	 * @param number {@link Number}
	 * @return most specific datatype
	 */
	public static Datatype getCorrespondingDatatype(Number number) {
		if (number instanceof Integer || number instanceof Long || number instanceof BigInteger) {
			if (NumberComparator.INSTANCE.compare(number, Integer.valueOf(0)) >= 0) {
				return xsd_nonNegativeInteger;
			} else {
				return xsd_integer;
			}
		} else if (number instanceof BigDecimal) {
			return xsd_decimal;
		} else if (number instanceof BigRational) {
			return owl_rational;
		} else {
			return owl_real;
		}
	}
	
}
