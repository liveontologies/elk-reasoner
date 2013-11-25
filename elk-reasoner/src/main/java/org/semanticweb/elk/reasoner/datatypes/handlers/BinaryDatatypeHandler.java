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

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.datatypes.Base64BinaryDatatype;
import org.semanticweb.elk.owl.interfaces.datatypes.HexBinaryDatatype;
import org.semanticweb.elk.owl.interfaces.literals.ElkBase64BinaryLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkHexBinaryLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkRealLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;

/**
 * xsd:hexBinary and xsd:base64Binary datatype handler.
 * <p>
 * uses {@link BinaryValue} and {@link LengthRestrictedValueSpace} to represent
 * datatype restrictions. Please note that value spaces of xsd:hexBinary and
 * xsd:base64Binary are disjoint.
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public class BinaryDatatypeHandler extends AbstractDatatypeHandler {

	private final ElkLiteralVisitor<PointValue<?, byte[]>> literalConverter_ = new BaseLiteralConverter<byte[]>(){

		@Override
		public PointValue<?, byte[]> visit(ElkBase64BinaryLiteral elkLiteral) {
			return new BinaryValue(elkLiteral.getBytes(), ElkDatatypeMap.XSD_BASE_64_BINARY);
		}

		@Override
		public PointValue<?, byte[]> visit(ElkHexBinaryLiteral elkLiteral) {
			return new BinaryValue(elkLiteral.getBytes(), ElkDatatypeMap.XSD_HEX_BINARY);
		}

	};
	
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseDataRangeConverter() {
		
		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return elkDatatype.accept(new BaseDatatypeVisitor<EntireValueSpace<?>>() {

				@Override
				public EntireValueSpace<?> visit(Base64BinaryDatatype datatype) {
					return EntireValueSpace.XSD_BASE_64;
				}

				@Override
				public EntireValueSpace<?> visit(HexBinaryDatatype datatype) {
					return EntireValueSpace.XSD_HEX_BINARY;
				}
				
			});
		}

		@Override
		public ValueSpace<?> visit(	ElkDatatypeRestriction elkDatatypeRestriction) {
			Integer minLength = 0;
			Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);
			ElkDatatype datatype = elkDatatypeRestriction.getDatatype();
		
			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction
					.getFacetRestrictions()) {
				PredefinedElkIri facet = PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet()); 
				ElkRealLiteral value = asNumericLiteral(facetRestriction.getRestrictionValue());

				switch (facet) {
					case XSD_LENGTH:
						minLength = value.getNumber().intValue();
						maxLength = minLength;
						return new LengthRestrictedValueSpace(datatype, minLength, maxLength);
					case XSD_MIN_LENGTH:
						minLength = value.getNumber().intValue();
						break;
					case XSD_MAX_LENGTH:
						maxLength = value.getNumber().intValue();
						break;
					default:
						throw new ElkUnexpectedIndexingException("Unsupported facet: " + facet.get());
				}
			}
			
			return new LengthRestrictedValueSpace(	datatype, minLength, maxLength);
		}

	};

	@Override
	public PointValue<?, ?> createValueSpace(ElkLiteral literal) {
		return literal.accept(literalConverter_);
	}

	@Override
	protected ElkDataRangeVisitor<ValueSpace<?>> getDataRangeConverter() throws ElkIndexingException {
		return dataRangeConverter_;
	}

}
