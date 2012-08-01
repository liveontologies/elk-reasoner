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

import static org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype.*;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;

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
 * @author "Yevgeny Kazakov"
 */
public class XMLLiteralDatatypeHandler extends ElkDatatypeHandler {

	static final Logger LOGGER_ = Logger
		.getLogger(XMLLiteralDatatypeHandler.class);

	@Override
	public Set<ELDatatype> getSupportedDatatypes() {
		return EnumSet.of(rdf_XMLiteral);
	}

	@Override
	public Set<Facet> getSupportedFacets() {
		return Collections.emptySet();
	}
}
