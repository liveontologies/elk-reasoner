package org.semanticweb.elk.owl.managers;
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

import java.util.EnumMap;
import org.semanticweb.elk.owl.datatypes.*;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class ElkDatatypeMap {

	private static final EnumMap<PredefinedElkIri, ElkDatatype> map =
		new EnumMap<PredefinedElkIri, ElkDatatype>(PredefinedElkIri.class);

	static {
		map.put(PredefinedElkIri.RDFS_LITERAL,		new LiteralDatatypeImpl(PredefinedElkIri.RDFS_LITERAL.get()));
		map.put(PredefinedElkIri.XSD_DATE_TIME,		new DateTimeDatatypeImpl(PredefinedElkIri.XSD_DATE_TIME.get()));
		map.put(PredefinedElkIri.XSD_DATE_TIME_STAMP,	new DateTimeStampDatatypeImpl(PredefinedElkIri.XSD_DATE_TIME_STAMP.get()));
		map.put(PredefinedElkIri.XSD_BASE_64_BINARY,	new Base64BinaryDatatypeImpl(PredefinedElkIri.XSD_BASE_64_BINARY.get()));
		map.put(PredefinedElkIri.XSD_HEX_BINARY,	new HexBinaryDatatypeImpl(PredefinedElkIri.XSD_HEX_BINARY.get()));
		map.put(PredefinedElkIri.XSD_ANY_URI,		new AnyUriDatatypeImpl(PredefinedElkIri.XSD_ANY_URI.get()));
		map.put(PredefinedElkIri.OWL_REAL,		new RealDatatypeImpl(PredefinedElkIri.OWL_REAL.get()));
		map.put(PredefinedElkIri.OWL_RATIONAL,		new RationalDatatypeImpl(PredefinedElkIri.OWL_RATIONAL.get()));
		map.put(PredefinedElkIri.XSD_DECIMAL,		new DecimalDatatypeImpl(PredefinedElkIri.XSD_DECIMAL.get()));
		map.put(PredefinedElkIri.XSD_INTEGER,		new IntegerDatatypeImpl(PredefinedElkIri.XSD_INTEGER.get()));
		map.put(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER, new NonNegativeIntegerDatatypeImpl(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get()));
		map.put(PredefinedElkIri.RDF_PLAIN_LITERAL,	new PlainLiteralDatatypeImpl(PredefinedElkIri.RDF_PLAIN_LITERAL.get()));
		map.put(PredefinedElkIri.XSD_STRING,		new StringDatatypeImpl(PredefinedElkIri.XSD_STRING.get()));
		map.put(PredefinedElkIri.XSD_NORMALIZED_STRING, new NormalizedStringDatatypeImpl(PredefinedElkIri.XSD_NORMALIZED_STRING.get()));
		map.put(PredefinedElkIri.XSD_TOKEN,		new TokenDatatypeImpl(PredefinedElkIri.XSD_TOKEN.get()));
		map.put(PredefinedElkIri.XSD_NAME,		new NameDatatypeImpl(PredefinedElkIri.XSD_NAME.get()));
		map.put(PredefinedElkIri.XSD_NMTOCKEN,		new NmTokenDatatypeImpl(PredefinedElkIri.XSD_NMTOCKEN.get()));
		map.put(PredefinedElkIri.XSD_NCNAME,		new NcNameDatatypeImpl(PredefinedElkIri.XSD_NCNAME.get()));
		map.put(PredefinedElkIri.RDF_XMLITERAL,		new XmlLiteralDatatypeImpl(PredefinedElkIri.RDF_XMLITERAL.get()));
	}
	
	public static ElkDatatype get(ElkIri iri) {
		PredefinedElkIri preDefIri = PredefinedElkIri.lookup(iri);
		if (preDefIri != null) {
			return map.get(preDefIri);
		} else {
			return new UndefinedDatatypeImpl(iri);
		}
	}
}
