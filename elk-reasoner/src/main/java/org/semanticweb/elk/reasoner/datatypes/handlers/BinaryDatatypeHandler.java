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

import java.util.List;

import javax.xml.bind.DatatypeConverter;
import org.semanticweb.elk.owl.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.BinaryValue;

/**
 * xsd:hexBinary and xsd:base64Binary datatype handler
 * <p>
 * uses {@link BinaryValue} and {@link LengthRestrictedValueSpace} to represent
 * datatype restrictions. Please note that value space of xsd:hexBinary and
 * xsd:base64Binary are disjoint.
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class BinaryDatatypeHandler extends AbstractDatatypeHandler {

	static final Logger LOGGER_ = LoggerFactory.getLogger(BinaryDatatypeHandler.class);
	private final BinaryParser parser_ = new BinaryParser();

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		ElkDatatype datatype = elkLiteral.getDatatype();
		byte[] value = datatype.accept(parser_, elkLiteral.getLexicalForm());
		if (value != null) {
			return new BinaryValue(value, datatype);
		} else {
			return null;
		}
	}

	@Override
	public ValueSpace visit(ElkDatatype elkDatatype) {
		return new EntireValueSpace(elkDatatype);
	}

	@Override
	public ValueSpace visit(ElkDatatypeRestriction elkDatatypeRestriction) {
		Integer minLength = 0;
		Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);
		ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

		List<? extends ElkFacetRestriction> facetRestrictions = elkDatatypeRestriction
			.getFacetRestrictions();
		outerloop:
		for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction
				.getConstrainingFacet().getFullIriAsString());
			String value = facetRestriction.getRestrictionValue()
				.getLexicalForm();

			switch (facet) {
				case LENGTH:
					minLength = Integer.valueOf(value);
					maxLength = minLength;
					break outerloop;
				case MIN_LENGTH:
					minLength = Integer.valueOf(value);
					break;
				case MAX_LENGTH:
					maxLength = Integer.valueOf(value);
					break;
				default:
					LOGGER_.warn("Unsupported facet: " + facet.iri);
					return null;
			}

		}
		LengthRestrictedValueSpace vs = new LengthRestrictedValueSpace(
			datatype, minLength, maxLength);
		if (vs.isEmptyInterval()) {
			return EmptyValueSpace.INSTANCE;
		} else {
			return vs;
		}
	}

	private class BinaryParser extends DatatypeValueParser<byte[], String> {

		@Override
		public byte[] parse(Base64BinaryDatatype datatype, String param) {
			return DatatypeConverter.parseBase64Binary(param);
		}

		@Override
		public byte[] parse(HexBinaryDatatype datatype, String param) {
			return DatatypeConverter.parseHexBinary(param);
		}
	}
}
