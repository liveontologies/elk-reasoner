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
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import org.semanticweb.elk.owl.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.datatypes.DateTimeDatatype;
import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.owl.datatypes.LiteralDatatype;
import org.semanticweb.elk.owl.datatypes.NameDatatype;
import org.semanticweb.elk.owl.datatypes.NcNameDatatype;
import org.semanticweb.elk.owl.datatypes.NmTokenDatatype;
import org.semanticweb.elk.owl.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.datatypes.NormalizedStringDatatype;
import org.semanticweb.elk.owl.datatypes.PlainLiteralDatatype;
import org.semanticweb.elk.owl.datatypes.RationalDatatype;
import org.semanticweb.elk.owl.datatypes.RealDatatype;
import org.semanticweb.elk.owl.datatypes.StringDatatype;
import org.semanticweb.elk.owl.datatypes.TokenDatatype;
import org.semanticweb.elk.owl.datatypes.XmlLiteralDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Representation of the entire value space for a given datatype.
 * 
 * @author Pospishnyi Olexandr
 * @author Pavel Klinov
 */
public abstract class EntireValueSpace<DT extends ElkDatatype> implements ValueSpace<DT> {

	private DT datatype;

	protected EntireValueSpace(DT datatype) {
		this.datatype = datatype;
	}

	@Override
	public DT getDatatype() {
		return datatype;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * EntireValueSpace contains any other value space or value that has
	 * compatible datatype
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {
		return valueSpace.getDatatype().isCompatibleWith(datatype);
	}

	@Override
	public boolean isSubsumedBy(ValueSpace<?> valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof EntireValueSpace) {
			EntireValueSpace<?> otherEntry = (EntireValueSpace<?>) other;

			return this.datatype.equals(otherEntry.datatype);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(EntireValueSpace.class,
				this.datatype);
	}

	@Override
	public String toString() {
		return datatype.toString();
	}

	public static final EntireNumericValueSpace<RealDatatype> OWL_REAL = new EntireNumericValueSpace<RealDatatype>(
			ElkDatatypeMap.OWL_REAL);
	public static final EntireNumericValueSpace<RationalDatatype> OWL_RATIONAL = new EntireNumericValueSpace<RationalDatatype>(
			ElkDatatypeMap.OWL_RATIONAL);
	public static final EntireNumericValueSpace<DecimalDatatype> XSD_DECIMAL = new EntireNumericValueSpace<DecimalDatatype>(
			ElkDatatypeMap.XSD_DECIMAL);
	public static final EntireNumericValueSpace<IntegerDatatype> XSD_INTEGER = new EntireNumericValueSpace<IntegerDatatype>(
			ElkDatatypeMap.XSD_INTEGER);
	public static final EntireNumericValueSpace<NonNegativeIntegerDatatype> XSD_NON_NEGATIVE_INTEGER = new EntireNumericValueSpace<NonNegativeIntegerDatatype>(
			ElkDatatypeMap.XSD_NON_NEGATIVE_INTEGER);
	public static final EntireValueSpace<AnyUriDatatype> XSD_ANY_URI = new OtherEntireValueSpace<AnyUriDatatype>(
			ElkDatatypeMap.XSD_ANY_URI);
	public static final EntireValueSpace<StringDatatype> XSD_STRING = new OtherEntireValueSpace<StringDatatype>(
			ElkDatatypeMap.XSD_STRING);
	public static final EntireValueSpace<NormalizedStringDatatype> XSD_NORMALIZED_STRING = new OtherEntireValueSpace<NormalizedStringDatatype>(
			ElkDatatypeMap.XSD_NORMALIZED_STRING);
	public static final EntireValueSpace<TokenDatatype> XSD_TOKEN = new OtherEntireValueSpace<TokenDatatype>(
			ElkDatatypeMap.XSD_TOKEN);
	public static final EntireValueSpace<NameDatatype> XSD_NAME = new OtherEntireValueSpace<NameDatatype>(
			ElkDatatypeMap.XSD_NAME);
	public static final EntireValueSpace<NcNameDatatype> XSD_NCNAME = new OtherEntireValueSpace<NcNameDatatype>(
			ElkDatatypeMap.XSD_NCNAME);
	public static final EntireValueSpace<NmTokenDatatype> XSD_NMTOKEN = new OtherEntireValueSpace<NmTokenDatatype>(
			ElkDatatypeMap.XSD_NMTOKEN);
	public static final EntireValueSpace<PlainLiteralDatatype> RDF_PLAIN_LITERAL = new OtherEntireValueSpace<PlainLiteralDatatype>(
			ElkDatatypeMap.RDF_PLAIN_LITERAL);
	public static final EntireValueSpace<Base64BinaryDatatype> XSD_BASE_64 = new OtherEntireValueSpace<Base64BinaryDatatype>(
			ElkDatatypeMap.XSD_BASE_64_BINARY);
	public static final EntireValueSpace<HexBinaryDatatype> XSD_HEX_BINARY = new OtherEntireValueSpace<HexBinaryDatatype>(
			ElkDatatypeMap.XSD_HEX_BINARY);
	public static final EntireValueSpace<XmlLiteralDatatype> XML_LITERAL = new OtherEntireValueSpace<XmlLiteralDatatype>(
			ElkDatatypeMap.RDF_XMLITERAL);
	public static final EntireValueSpace<LiteralDatatype> RDFS_LITERAL = new OtherEntireValueSpace<LiteralDatatype>(
			ElkDatatypeMap.RDFS_LITERAL);
	public static final EntireValueSpace<DateTimeDatatype> ENTIRE_DATE_TIME = new OtherEntireValueSpace<DateTimeDatatype>(
			ElkDatatypeMap.XSD_DATE_TIME);
	public static final EntireValueSpace<DateTimeDatatype> ENTIRE_DATE_TIME_STAMP = new OtherEntireValueSpace<DateTimeDatatype>(
			ElkDatatypeMap.XSD_DATE_TIME_STAMP);
}
