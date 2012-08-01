/*
 * #%L
 * ELK OWL Object Interfaces
 * *
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2011 Department of Computer Science, University of Oxford
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
package org.semanticweb.elk.owl.interfaces;

/**
 * Corresponds to a <a href=
 * "http://www.w3.org/TR/owl2-syntax/#Datatypes">Datatype<a> in the OWL 2
 * specification.
 *
 * @author Markus Kroetzsch
 * @author Pospishnyi Olexandr
 */
public interface ElkDatatype extends ElkDataRange, ElkEntity {

	/**
	 * Preset datatypes from OWL 2 EL
	 */
	public static enum ELDatatype {
		rdfs_Literal		(null,		"http://www.w3.org/2000/01/rdf-schema#Literal"),
		xsd_dateTime		(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#dateTime"),
		xsd_dateTimeStamp	(xsd_dateTime,	"http://www.w3.org/2001/XMLSchema#dateTimeStamp"),
		xsd_base64Binary	(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#base64Binary"),
		xsd_hexBinary		(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#hexBinary"),
		xsd_anyURI		(rdfs_Literal,	"http://www.w3.org/2001/XMLSchema#anyURI"),
		owl_real			(rdfs_Literal,	"http://www.w3.org/2002/07/owl#real"),
		owl_rational		(owl_real,	"http://www.w3.org/2002/07/owl#rational"),
		xsd_decimal		(owl_rational,	"http://www.w3.org/2001/XMLSchema#decimal"),
		xsd_integer		(xsd_decimal,	"http://www.w3.org/2001/XMLSchema#integer"),
		xsd_nonNegativeInteger	(xsd_integer,	"http://www.w3.org/2001/XMLSchema#nonNegativeInteger"),
		rdf_PlainLiteral		(rdfs_Literal,	"http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"),
		xsd_string		(rdf_PlainLiteral,"http://www.w3.org/2001/XMLSchema#string"),
		xsd_normalizedString	(xsd_string,	"http://www.w3.org/2001/XMLSchema#normalizedString"),
		xsd_token		(xsd_normalizedString,"http://www.w3.org/2001/XMLSchema#token"),
		xsd_Name		(xsd_token,	"http://www.w3.org/2001/XMLSchema#Name"),
		xsd_NMTOCKEN		(xsd_token,	"http://www.w3.org/2001/XMLSchema#NMTOKEN"),
		xsd_NCName		(xsd_Name,	"http://www.w3.org/2001/XMLSchema#NCName"),
		rdf_XMLiteral		(rdfs_Literal,	"http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");

		public final ELDatatype parent;
		public final String iri;

		private ELDatatype(ELDatatype parent, String iri) {
			this.parent = parent;
			this.iri = iri;
		}

		/**
		 * Check weather this datatype is completely compatible with
		 * another. Being compatible means being equal to or being
		 * derived from.
		 *
		 * @param datatype other datatype to check against
		 * @return true/false = yes/no
		 */
		public boolean isCompatibleWith(ELDatatype datatype) {
			if (this == datatype) {
				return true;
			} else {
				ELDatatype parent = this.parent;
				while (parent != null) {
					if (parent == datatype) {
						return true;
					}
					parent = parent.parent;
				}
				return false;
			}
		}

		/**
		 * Get Datatype enum element based on it's IRI
		 *
		 * @param iri full datatype IRI
		 * @return {@link Datatype}
		 */
		public static ELDatatype getByIri(String iri) {
			for (ELDatatype datatype : ELDatatype.values()) {
				if (iri.equals(datatype.iri)) {
					return datatype;
				}
			}
			return null;
		}
	}

	public String getDatatypeShortname();

	public String getDatatypeIRI();

	public ELDatatype asELDatatype();
}
