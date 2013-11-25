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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.EnumMap;

import org.semanticweb.elk.owl.datatypes.AnyUriDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.Base64BinaryDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.DateTimeDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.DateTimeStampDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.DecimalDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.HexBinaryDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.IntegerDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.LiteralDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.NameDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.NcNameDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.NmTokenDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.NormalizedStringDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.PlainLiteralDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.RationalDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.RealDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.StringDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.TokenDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.UndefinedDatatypeImpl;
import org.semanticweb.elk.owl.datatypes.XmlLiteralDatatypeImpl;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.Base64BinaryDatatype;
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
import org.semanticweb.elk.owl.interfaces.datatypes.XmlLiteralDatatype;
import org.semanticweb.elk.owl.iris.ElkIri;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;

/**
 *
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public class ElkDatatypeMap {

	public static final LiteralDatatype RDFS_LITERAL = new LiteralDatatypeImpl(PredefinedElkIri.RDFS_LITERAL.get());
	public static final DateTimeDatatype XSD_DATE_TIME = new DateTimeDatatypeImpl(PredefinedElkIri.XSD_DATE_TIME.get());
	public static final DateTimeStampDatatype XSD_DATE_TIME_STAMP = new DateTimeStampDatatypeImpl(PredefinedElkIri.XSD_DATE_TIME_STAMP.get());
	public static final Base64BinaryDatatype XSD_BASE_64_BINARY = new Base64BinaryDatatypeImpl(PredefinedElkIri.XSD_BASE_64_BINARY.get());
	public static final HexBinaryDatatype XSD_HEX_BINARY = new HexBinaryDatatypeImpl(PredefinedElkIri.XSD_HEX_BINARY.get());
	public static final AnyUriDatatype XSD_ANY_URI = new AnyUriDatatypeImpl(PredefinedElkIri.XSD_ANY_URI.get());
	public static final RealDatatype OWL_REAL = new RealDatatypeImpl(PredefinedElkIri.OWL_REAL.get());
	public static final RationalDatatype OWL_RATIONAL = new RationalDatatypeImpl(PredefinedElkIri.OWL_RATIONAL.get());
	public static final DecimalDatatype XSD_DECIMAL = new DecimalDatatypeImpl(PredefinedElkIri.XSD_DECIMAL.get());
	public static final IntegerDatatype XSD_INTEGER = new IntegerDatatypeImpl(PredefinedElkIri.XSD_INTEGER.get());
	public static final NonNegativeIntegerDatatype XSD_NON_NEGATIVE_INTEGER = new NonNegativeIntegerDatatypeImpl(PredefinedElkIri.XSD_NON_NEGATIVE_INTEGER.get());
	public static final PlainLiteralDatatype RDF_PLAIN_LITERAL = new PlainLiteralDatatypeImpl(PredefinedElkIri.RDF_PLAIN_LITERAL.get());
	public static final StringDatatype XSD_STRING = new StringDatatypeImpl(PredefinedElkIri.XSD_STRING.get());
	public static final NormalizedStringDatatype XSD_NORMALIZED_STRING = new NormalizedStringDatatypeImpl(PredefinedElkIri.XSD_NORMALIZED_STRING.get());
	public static final TokenDatatype XSD_TOKEN = new TokenDatatypeImpl(PredefinedElkIri.XSD_TOKEN.get());
	public static final NameDatatype XSD_NAME = new NameDatatypeImpl(PredefinedElkIri.XSD_NAME.get());
	public static final NmTokenDatatype XSD_NMTOKEN = new NmTokenDatatypeImpl(PredefinedElkIri.XSD_NMTOCKEN.get());
	public static final NcNameDatatype XSD_NCNAME = new NcNameDatatypeImpl(PredefinedElkIri.XSD_NCNAME.get());
	public static final XmlLiteralDatatype RDF_XMLITERAL = new XmlLiteralDatatypeImpl(PredefinedElkIri.RDF_XMLITERAL.get());
	
	private static final EnumMap<PredefinedElkIri, ElkDatatype> map =
			new EnumMap<PredefinedElkIri, ElkDatatype>(PredefinedElkIri.class);
	
	static {
		//This might be slow but is only done once when this class is loaded.
		for (PredefinedElkIri iri : PredefinedElkIri.values()) {
			ElkDatatype datatype = getDatatype(iri.get()); 
			
			if (datatype != null) {
				map.put(iri, datatype);
			}
		}
	}

	private static ElkDatatype getDatatype(ElkIri iri) {
		for (Field field : ElkDatatypeMap.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				try {
					Object staticFieldValue = (ElkDatatype) field.get(null);

					if (staticFieldValue instanceof ElkDatatype) {
						ElkDatatype datatype = (ElkDatatype) staticFieldValue;

						if (datatype.getIri() == iri) {
							return datatype;
						}
					}

				} catch (Exception e) {
				}

			}
		}

		return null;
	}
	
	/**
	 * This generic lookup method should not be called if the IRI is known at compile time.
	 * @param iri
	 * @return
	 */
	public static ElkDatatype get(ElkIri iri) {
		PredefinedElkIri preDefIri = PredefinedElkIri.lookup(iri);
		
		if (preDefIri != null) {
			return map.get(preDefIri);
		} else {
			return new UndefinedDatatypeImpl(iri);
		}
	}

}
