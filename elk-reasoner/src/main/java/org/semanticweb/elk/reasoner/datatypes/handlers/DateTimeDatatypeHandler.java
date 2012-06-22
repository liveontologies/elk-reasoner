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

import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.DatatypeConverter;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.owl.interfaces.ElkDatatypeRestriction;
import org.semanticweb.elk.owl.interfaces.ElkFacetRestriction;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import static org.semanticweb.elk.reasoner.datatypes.enums.Datatype.xsd_dateTime;
import static org.semanticweb.elk.reasoner.datatypes.enums.Datatype.xsd_dateTimeStamp;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import static org.semanticweb.elk.reasoner.datatypes.enums.Facet.*;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.*;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataHasValue;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class DateTimeDatatypeHandler implements DatatypeHandler {

	static final Logger LOGGER_ = Logger.getLogger(DateTimeDatatypeHandler.class);
	
	public Set<Datatype> getSupportedDatatypes() {
		return EnumSet.of(xsd_dateTime, xsd_dateTimeStamp);
	}

	public Set<Facet> getSupportedFacets() {
		return EnumSet.of(MIN_INCLUSIVE, MAX_INCLUSIVE, MIN_EXCLUSIVE, MAX_EXCLUSIVE);
	}

	public ValueSpace convert(IndexedDatatypeExpression datatypeExpression) {
		if (datatypeExpression instanceof IndexedDataHasValue) {
			return createUnipointValueSpace((IndexedDataHasValue) datatypeExpression);
		} else if (datatypeExpression instanceof IndexedDataSomeValuesFrom) {
			ElkDataRange filler = ((IndexedDataSomeValuesFrom) datatypeExpression).getFiller();
			if (filler instanceof ElkDatatype) {
				return createEntireValueSpace((ElkDatatype) filler);
			} else {
				return createRestrictedValueSpace((ElkDatatypeRestriction) filler);
			}
		}
		LOGGER_.warn("Unsupported datatype expression: " + datatypeExpression.getClass().getName());
		return null;
	}

	private ValueSpace createUnipointValueSpace(IndexedDataHasValue datatypeExpression) {
		Datatype datatype = Datatype.getByIri(datatypeExpression.getFiller().getDatatype().getDatatypeIRI());
		String lexicalForm = datatypeExpression.getFiller().getLexicalForm();
		Calendar value = (Calendar) parse(lexicalForm, datatype);
		return new UnipointValueSpace(datatype, value.getTimeInMillis());
	}

	private ValueSpace createEntireValueSpace(ElkDatatype elkDatatype) {
		return new EntireValueSpace(Datatype.getByIri(elkDatatype.getDatatypeIRI()));
	}

	private ValueSpace createRestrictedValueSpace(ElkDatatypeRestriction filler) {
		Long lowerBound = Long.valueOf(0);
		Long upperBound = Long.MAX_VALUE;
		boolean lowerInclusive = true, upperInclusive = true;

		Datatype datatype = Datatype.getByIri(filler.getDatatype().getDatatypeIRI());
		
		List<? extends ElkFacetRestriction> facetRestrictions = filler.getFacetRestrictions();
		for (ElkFacetRestriction facetRestriction : facetRestrictions) {
			Facet facet = Facet.getByIri(facetRestriction.getConstrainingFacet().asString());
			Datatype restrictionDatatype = Datatype.getByIri(
					facetRestriction.getRestrictionValue().getDatatype().getDatatypeIRI());
			Calendar parsedValue = (Calendar) parse(
					facetRestriction.getRestrictionValue().getLexicalForm(), restrictionDatatype);
			Long restrictionValue = Long.valueOf(parsedValue.getTimeInMillis());
			
			
			switch (facet) {
				case MIN_INCLUSIVE: // >=
					if (restrictionValue.compareTo(lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = true;
					}
					break;
				case MIN_EXCLUSIVE: // >
					if (restrictionValue.compareTo(lowerBound) >= 0) {
						lowerBound = restrictionValue;
						lowerInclusive = false;
					}
					break;
				case MAX_INCLUSIVE: // <=
					if (restrictionValue.compareTo(upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = true;
					}
					break;
				case MAX_EXCLUSIVE: // <
					if (restrictionValue.compareTo(upperBound) <= 0) {
						upperBound = restrictionValue;
						upperInclusive = false;
					}
					break;
			}
		}

		RestrictedValueSpace valueSpace = new RestrictedValueSpace(
				datatype, lowerBound, lowerInclusive, upperBound, upperInclusive);

		if (valueSpace.isEmptyInterval()) {
			return EmptyValueSpace.INSTANCE;
		} else {
			return valueSpace;
		}
	}

	public Object parse(String literal, Datatype datatype) {
		switch (datatype) {
			case xsd_dateTime:
			case xsd_dateTimeStamp:
				return DatatypeConverter.parseDateTime(literal);
			default:
				LOGGER_.warn("Unsupported datetime datatype: " + datatype.iri);
				return null;
		}
	}
}
