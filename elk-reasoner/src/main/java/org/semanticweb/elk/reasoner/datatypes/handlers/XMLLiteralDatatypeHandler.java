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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import static org.semanticweb.elk.reasoner.datatypes.enums.Datatype.rdf_XMLiteral;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDataSomeValuesFrom;
import org.semanticweb.elk.reasoner.indexing.hierarchy.IndexedDatatypeExpression;

/**
 * rdfs:XMLLiteral datatype handler.
 * <p>
 * This datatype has no practical usage as it has no normative facets and
 * lexical representation.Could only participate in DataSomeValuesFrom
 * expressions.
 * <p>
 * Uses {@link EntireValueSpace} value space restriction only.
 *
 * @author Pospishnyi Olexandr
 */
public class XMLLiteralDatatypeHandler implements DatatypeHandler {

	static final Logger LOGGER_ = Logger.getLogger(XMLLiteralDatatypeHandler.class);

	@Override
	public Set<Datatype> getSupportedDatatypes() {
		return EnumSet.of(rdf_XMLiteral);
	}

	@Override
	public Set<Facet> getSupportedFacets() {
		return Collections.emptySet();
	}

	@Override
	public ValueSpace convert(IndexedDatatypeExpression datatypeExpression) {
		if (datatypeExpression instanceof IndexedDataSomeValuesFrom) {
			ElkDataRange filler = ((IndexedDataSomeValuesFrom) datatypeExpression).getFiller();
			if (filler instanceof ElkDatatype) {
				return new EntireValueSpace(datatypeExpression.getDatatype());
			}
		}
		LOGGER_.warn("Unsupported datatype expression: " + datatypeExpression.getClass().getName());
		return null;
	}

	@Override
	public Object parse(String literal, Datatype datatype) {
		throw new UnsupportedOperationException("Not supported");
	}
}
