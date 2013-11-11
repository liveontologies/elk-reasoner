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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.PatternValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import org.semanticweb.elk.owl.datatypes.AnyUriDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;

/**
 * xsd:AnyURI datatype handler
 * <p>
 * Very similar to {@link PlainLiteralDatatypeHandler} and uses same ValueSpace
 * objects for representation. Value space of anyURI is disjoint with all other
 * datatypes.
 *
 * @author Pospishnyi Olexandr
 * @author "Yevgeny Kazakov"
 */
public class AnyURIDatatypeHandler extends AbstractDatatypeHandler {

	static final Logger LOGGER_ = LoggerFactory.getLogger(AnyURIDatatypeHandler.class);
	private final AnyUriParser parser_ = new AnyUriParser();

	@Override
	public ValueSpace visit(ElkLiteral elkLiteral) {
		ElkDatatype datatype = elkLiteral.getDatatype();
		URI value = datatype.accept(parser_, elkLiteral.getLexicalForm());
		if (value != null) {
			return new LiteralValue(value.toString(), datatype, datatype);
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
				case PATTERN:
					Automaton pattern = new RegExp(value).toAutomaton();
					pattern.setInfo(value);
					PatternValueSpace vs = new PatternValueSpace(pattern, datatype,
						datatype);
					if (vs.isEmpty()) {
						return EmptyValueSpace.INSTANCE;
					} else {
						return vs;
					}
				default:
					LOGGER_.warn("Unsupported facet: " + facet.iri);
					return null;
			}

		}
		LengthRestrictedValueSpace vs = new LengthRestrictedValueSpace(
			datatype, minLength, maxLength);
		if (vs.isEmpty()) {
			return EmptyValueSpace.INSTANCE;
		} else {
			return vs;
		}
	}

	private class AnyUriParser extends DatatypeValueParser<URI, String> {

		@Override
		public URI parse(AnyUriDatatype datatype, String param) {
			try {
				return new URI(param);
			} catch (URISyntaxException ex) {
				LOGGER_.error("Invalid URI: " + param);
				return null;
			}
		}
	}
}
