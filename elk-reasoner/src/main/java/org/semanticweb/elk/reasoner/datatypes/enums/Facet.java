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
package org.semanticweb.elk.reasoner.datatypes.enums;

/**
 * Enumeration of all known and supported facet restrictions used within
 * datatype expressions
 *
 * @author Pospishnyi Oleksandr
 */
public enum Facet {

	MIN_INCLUSIVE ("http://www.w3.org/2001/XMLSchema#minInclusive", ">="),
	MIN_EXCLUSIVE ("http://www.w3.org/2001/XMLSchema#minExclusive", ">"),
	MAX_INCLUSIVE ("http://www.w3.org/2001/XMLSchema#maxInclusive", "<="),
	MAX_EXCLUSIVE ("http://www.w3.org/2001/XMLSchema#maxExclusive", "<"),
	MIN_LENGTH    ("http://www.w3.org/2001/XMLSchema#minLength",	"l>"),
	MAX_LENGTH    ("http://www.w3.org/2001/XMLSchema#maxLength",	"l<"),
	LENGTH        ("http://www.w3.org/2001/XMLSchema#length",	"l="),
	PATTERN       ("http://www.w3.org/2001/XMLSchema#pattern",	"regex:");

	public final String iri;
	public final String symbol;

	private Facet(String iri, String symbol) {
		this.iri = iri;
		this.symbol = symbol;
	}

	/**
	 * Get Facet enum element by it's IRI
	 *
	 * @param iri full IRI
	 * @return {@link Facet}
	 */
	public static Facet getByIri(String iri) {
		for (Facet facet : Facet.values()) {
			if (iri.equals(facet.iri)) {
				return facet;
			}
		}
		return null;
	}
}
