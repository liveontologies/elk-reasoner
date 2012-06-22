/*
 * #%L
 * ELK Reasoner
 * 
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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import static org.semanticweb.elk.reasoner.datatypes.enums.Datatype.xsd_anyURI;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.*;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.*;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * xsd:AnyURI datatype handler
 * <p>
 * Very similar to {@link PlainLiteralDatatypeHandler} and uses same ValueSpace
 * objects for representation. Value space of anyURI is disjoint with all other
 * datatypes.
 *
 * @author Pospishnyi Olexandr
 */
public class AnyURIDatatypeHandler implements DatatypeHandler {

	static final Logger LOGGER_ = Logger.getLogger(AnyURIDatatypeHandler.class);

	public Set<Datatype> getSupportedDatatypes() {
		return EnumSet.of(xsd_anyURI);
	}

	public Set<Facet> getSupportedFacets() {
		return EnumSet.of(LENGTH, MIN_LENGTH, MAX_LENGTH, PATTERN);
	}

	public ValueSpace convert(IndexedDatatypeExpression datatypeExpression) {
		if (datatypeExpression instanceof IndexedDataHasValue) {
			return createLiteralValueSpace((IndexedDataHasValue) datatypeExpression);
		} else if (datatypeExpression instanceof IndexedDataSomeValuesFrom) {
			ElkDataRange filler = ((IndexedDataSomeValuesFrom) datatypeExpression).getFiller();
			if (filler instanceof ElkDatatype) {
				return new EntireValueSpace(datatypeExpression.getDatatype());
			} else {
				return createRestrictedValueSpace((ElkDatatypeRestriction) filler);
			}
		}
		LOGGER_.warn("Unsupported datatype expression: " + datatypeExpression.getClass().getName());
		return null;
	}

	private ValueSpace createLiteralValueSpace(IndexedDataHasValue datatypeExpression) {
		Datatype datatype = datatypeExpression.getDatatype();
		URI value = (URI) parse(datatypeExpression.getFiller().getLexicalForm(), datatype);
		if (value != null) {
			return new LiteralValueSpace(value.toString(), datatype, datatype);
		} else {
			return null;
		}
	}

	private ValueSpace createRestrictedValueSpace(ElkDatatypeRestriction filler) {
		Integer minLength = 0;
		Integer maxLength = Integer.valueOf(Integer.MAX_VALUE);
		Datatype datatype = Datatype.getByIri(filler.getDatatype().getDatatypeIRI());

		List<? extends ElkFacetRestriction> facetRestrictions = filler.getFacetRestrictions();
		outerloop:
		for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction.getConstrainingFacet().asString());
			String value = facetRestriction.getRestrictionValue().getLexicalForm();

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
					return new PatternValueSpace(value, datatype);
				default:
					LOGGER_.warn("Unsupported facet: " + facet.iri);
					return null;
			}

		}
		LengthRestrictedValueSpace vs = new LengthRestrictedValueSpace(datatype, minLength, maxLength);
		if (vs.isEmptyInterval()) {
			return EmptyValueSpace.INSTANCE;
		} else {
			return vs;
		}
	}

	public Object parse(String literal, Datatype datatype) {
		try {
			return new URI(literal);
		} catch (URISyntaxException ex) {
			LOGGER_.error("Invalid URI: " + literal);
			return null;
		}
	}
}
