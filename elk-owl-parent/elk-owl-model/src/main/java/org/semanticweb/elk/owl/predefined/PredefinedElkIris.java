/*
 * #%L
 * ELK OWL Object Interfaces
 * 
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
package org.semanticweb.elk.owl.predefined;

import org.semanticweb.elk.owl.iris.ElkFullIri;
import org.semanticweb.elk.owl.iris.ElkIri;

/**
 * 
 * <a href= "http://www.w3.org/TR/owl2-syntax/#IRIs" >Reserved Vocabulary of OWL
 * 2 with Special Treatment</a> in OWL 2 (see Table 3 in the link).
 * 
 * @author "Yevgeny Kazakov"
 *
 */
public class PredefinedElkIris {

	public static final ElkIri

	OWL_BACKWARD_COMPATIBLE_WITH = new ElkFullIri(PredefinedElkPrefix.OWL,
			"backwardCompatibleWith"),

			OWL_BOTTOM_DATA_PROPERTY = new ElkFullIri(PredefinedElkPrefix.OWL,
					"bottomDataProperty"),

			OWL_BOTTOM_OBJECT_PROPERTY = new ElkFullIri(
					PredefinedElkPrefix.OWL, "bottomObjectProperty"),

			OWL_DEPRECATED = new ElkFullIri(PredefinedElkPrefix.OWL,
					"deprecated"),

			OWL_INCOMPATIBLE_WITH = new ElkFullIri(PredefinedElkPrefix.OWL,
					"incompatibleWith"),

			OWL_NOTHING = new ElkFullIri(PredefinedElkPrefix.OWL, "Nothing"),

			OWL_PRIOR_VERSION = new ElkFullIri(PredefinedElkPrefix.OWL,
					"priorVersion"),

			OWL_RATIONAL = new ElkFullIri(PredefinedElkPrefix.OWL, "rational"),

			OWL_REAL = new ElkFullIri(PredefinedElkPrefix.OWL, "real"),

			OWL_VERSION_INFO = new ElkFullIri(PredefinedElkPrefix.OWL,
					"versionInfo"),

			OWL_THING = new ElkFullIri(PredefinedElkPrefix.OWL, "Thing"),

			OWL_TOP_DATA_PROPERTY = new ElkFullIri(PredefinedElkPrefix.OWL,
					"topDataProperty"),

			OWL_TOP_OBJECT_PROPERTY = new ElkFullIri(PredefinedElkPrefix.OWL,
					"topObjectProperty"),

			RDF_LANG_RANGE = new ElkFullIri(PredefinedElkPrefix.RDF,
					"langRange"),

			RDF_PLAIN_LITERAL = new ElkFullIri(PredefinedElkPrefix.RDF,
					"PlainLiteral"),

			RDF_XML_LITERAL = new ElkFullIri(PredefinedElkPrefix.RDF,
					"XMLLiteral"),

			RDFS_COMMENT = new ElkFullIri(PredefinedElkPrefix.RDFS, "comment"),

			RDFS_IS_DEFINED_BY = new ElkFullIri(PredefinedElkPrefix.RDFS,
					"isDefinedBy"),

			RDFS_LABEL = new ElkFullIri(PredefinedElkPrefix.RDFS, "label"),

			RDFS_LITERAL = new ElkFullIri(PredefinedElkPrefix.RDFS, "Literal"),

			RDFS_SEE_ALSO = new ElkFullIri(PredefinedElkPrefix.RDFS, "seeAlso"),

			XSD_ANY_URI = new ElkFullIri(PredefinedElkPrefix.XSD, "anyURI"),

			XSD_BASE_64_BINARY = new ElkFullIri(PredefinedElkPrefix.XSD,
					"base64Binary"),

			XSD_BOOLEAN = new ElkFullIri(PredefinedElkPrefix.XSD, "boolean"),

			XSD_BYTE = new ElkFullIri(PredefinedElkPrefix.XSD, "boolean"),

			XSD_DATE_TIME = new ElkFullIri(PredefinedElkPrefix.XSD, "dateTime"),

			XSD_DATE_TIME_STAMP = new ElkFullIri(PredefinedElkPrefix.XSD,
					"dateTimeStamp"),

			XSD_DECIMAL = new ElkFullIri(PredefinedElkPrefix.XSD, "decimal"),

			XSD_DOUBLE = new ElkFullIri(PredefinedElkPrefix.XSD, "double"),

			XSD_FLOAT = new ElkFullIri(PredefinedElkPrefix.XSD, "float"),

			XSD_HEX_BINARY = new ElkFullIri(PredefinedElkPrefix.XSD,
					"hexBinary"),

			XSD_INT = new ElkFullIri(PredefinedElkPrefix.XSD, "int"),

			XSD_INTEGER = new ElkFullIri(PredefinedElkPrefix.XSD, "integer"),

			XSD_LANGUAGE = new ElkFullIri(PredefinedElkPrefix.XSD, "language"),

			XSD_LENGTH = new ElkFullIri(PredefinedElkPrefix.XSD, "length"),

			XSD_LONG = new ElkFullIri(PredefinedElkPrefix.XSD, "long"),

			XSD_MAX_EXCLUSIVE = new ElkFullIri(PredefinedElkPrefix.XSD,
					"maxExclusive"),

			XSD_MAX_INCLUSIVE = new ElkFullIri(PredefinedElkPrefix.XSD,
					"maxInclusive"),

			XSD_MAX_LENGTH = new ElkFullIri(PredefinedElkPrefix.XSD,
					"maxLength"),

			XSD_MIN_EXCLUSIVE = new ElkFullIri(PredefinedElkPrefix.XSD,
					"minExclusive"),

			XSD_MIN_INCLUSIVE = new ElkFullIri(PredefinedElkPrefix.XSD,
					"minInclusive"),

			XSD_MIN_LENGTH = new ElkFullIri(PredefinedElkPrefix.XSD,
					"minLength"),

			XSD_NAME = new ElkFullIri(PredefinedElkPrefix.XSD, "Name"),

			XSD_NC_NAME = new ElkFullIri(PredefinedElkPrefix.XSD, "NCName"),

			XSD_NEGATIVE_INTEGER = new ElkFullIri(PredefinedElkPrefix.XSD,
					"negativeInteger"),

			XSD_NM_TOKEN = new ElkFullIri(PredefinedElkPrefix.XSD, "NMTOKEN"),

			XSD_NON_NEGATIVE_INTEGER = new ElkFullIri(PredefinedElkPrefix.XSD,
					"nonNegativeInteger"),

			XSD_NON_POSITIVE_INTEGER = new ElkFullIri(PredefinedElkPrefix.XSD,
					"nonPositiveInteger"),

			XSD_NORMALIZED_STRING = new ElkFullIri(PredefinedElkPrefix.XSD,
					"normalizedString"),

			XSD_PATTERN = new ElkFullIri(PredefinedElkPrefix.XSD, "pattern"),

			XSD_POSITIVE_INTEGER = new ElkFullIri(PredefinedElkPrefix.XSD,
					"positiveInteger"),

			XSD_SHORT = new ElkFullIri(PredefinedElkPrefix.XSD, "short"),

			XSD_STRING = new ElkFullIri(PredefinedElkPrefix.XSD, "string"),

			XSD_TOKEN = new ElkFullIri(PredefinedElkPrefix.XSD, "token"),

			XSD_UNSIGNED_BYTE = new ElkFullIri(PredefinedElkPrefix.XSD,
					"unsignedByte"),

			XSD_UNSIGNED_INT = new ElkFullIri(PredefinedElkPrefix.XSD,
					"unsignedInt"),

			XSD_UNSIGNED_LONG = new ElkFullIri(PredefinedElkPrefix.XSD,
					"unsignedLong"),

			XSD_UNSIGNED_SHORT = new ElkFullIri(PredefinedElkPrefix.XSD,
					"unsignedShort")

			;

	/**
	 * Defines an ordering on IRIs starting with {@link #OWL_NOTHING},
	 * {@link #OWL_THING}, followed by the remaining IRIs in alphabetical order.
	 * 
	 * @param firstIri
	 *            the fist {@link ElkIri} to compare with the second
	 * @param secondIri
	 *            the second {@link ElkIri} to compare with the first
	 * @return a negative integer, zero, or a positive integer as the first IRI
	 *         is less, equal, or greater than the second IRI in the specified
	 *         order
	 */
	public static int compare(ElkIri firstIri, ElkIri secondIri) {
		boolean isOwl0 = firstIri.equals(OWL_THING)
				|| firstIri.equals(OWL_NOTHING);
		boolean isOwl1 = secondIri.equals(OWL_THING)
				|| secondIri.equals(OWL_NOTHING);

		if (isOwl0 == isOwl1)
			return firstIri.compareTo(secondIri);
		// else
		return isOwl0 ? -1 : 1;
	}
}
