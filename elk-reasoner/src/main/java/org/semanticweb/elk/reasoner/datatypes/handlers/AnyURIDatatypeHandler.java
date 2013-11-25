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
import org.semanticweb.elk.owl.interfaces.literals.ElkAnyUriLiteral;
import org.semanticweb.elk.owl.interfaces.literals.ElkLiteral;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.predefined.PredefinedElkIri;
import org.semanticweb.elk.owl.visitors.ElkDataRangeVisitor;
import org.semanticweb.elk.owl.visitors.ElkLiteralVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.PatternValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkIndexingException;
import org.semanticweb.elk.reasoner.indexing.hierarchy.ElkUnexpectedIndexingException;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;

/**
 * xsd:AnyURI datatype handler
 * <p>
 * Very similar to {@link PlainLiteralDatatypeHandler} and uses same {@link ValueSpace} 
 * implementations for representation. Value space of anyURI is disjoint with all other
 * datatypes.
 *
 * TODO: get rid of the code duplication
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 * @author Pavel Klinov
 */
public class AnyURIDatatypeHandler extends AbstractDatatypeHandler {

	private final ElkLiteralVisitor<PointValue<?, String[]>> literalConverter_ = new BaseLiteralConverter<String[]>(){

		@Override
		public PointValue<?, String[]> visit(ElkAnyUriLiteral elkLiteral) {
			return new LiteralValue(elkLiteral.getLexicalForm(), ElkDatatypeMap.XSD_ANY_URI);
		}

	};
	
	private final ElkDataRangeVisitor<ValueSpace<?>> dataRangeConverter_ = new BaseDataRangeConverter() {

		@Override
		public ValueSpace<?> visit(ElkDatatype elkDatatype) {
			return EntireValueSpace.XSD_ANY_URI;
		}

		@Override
		public ValueSpace<?> visit(ElkDatatypeRestriction elkDatatypeRestriction) {
			Integer minLength = 0;
			Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);
			ElkDatatype datatype = elkDatatypeRestriction.getDatatype();

			for (ElkFacetRestriction facetRestriction : elkDatatypeRestriction.getFacetRestrictions()) {
				PredefinedElkIri facet = PredefinedElkIri.lookup(facetRestriction.getConstrainingFacet());
				String value = facetRestriction.getRestrictionValue().getLexicalForm();

				switch (facet) {
					case XSD_LENGTH:
						Integer length = Integer.valueOf(value);
						return new LengthRestrictedValueSpace(datatype, length, length);
					case XSD_MIN_LENGTH:
						minLength = Integer.valueOf(value);
						break;
					case XSD_MAX_LENGTH:
						maxLength = Integer.valueOf(value);
						break;
					case XSD_PATTERN:
						Automaton pattern = new RegExp(value).toAutomaton();
						pattern.setInfo(value);
						
						return new PatternValueSpace(pattern, datatype);
					default:
						throw new ElkUnexpectedIndexingException("Unsupported facet: " + facet);
				}

			}
			
			return new LengthRestrictedValueSpace(datatype, minLength, maxLength);
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
