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

import java.util.Set;

import org.semanticweb.elk.owl.interfaces.ElkDataRange;
import org.semanticweb.elk.owl.interfaces.ElkLiteral;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.enums.Facet;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Datatype handler interface
 * 
 * @author Pospishnyi Oleksandr
 * @author "Yevgeny Kazakov"
 */
public interface DatatypeHandler {

	/**
	 * Get all datatypes supported by this handler
	 */
	public Set<Datatype> getSupportedDatatypes();

	/**
	 * Get all restriction facets supported by this handler with respect to it's
	 * supported datatype family
	 */
	public Set<Facet> getSupportedFacets();

	/**
	 * Convert specified datatype expression to internal representation being a
	 * subset of values defined by this expression
	 * 
	 * @param datatypeExpression
	 *            indexed datatype expression to convert
	 * @return specific {@link ValueSpace} implementation to represent all
	 *         values within a restricted value space
	 */
	public ValueSpace getValueSpace(ElkDataRange dataRange, Datatype datatype);

	public ValueSpace getValueSpace(ElkLiteral literal, Datatype datatype);

	/**
	 * Parse string literal to it's representing object (Number,Date,byte[],
	 * etc.) with respect to specified datatype
	 * 
	 * @param literal
	 *            lexical form
	 * @param datatype
	 *            exact literal datatype
	 * @return typed internal representation of this literal
	 */
	public Object parse(String literal, Datatype datatype);
}
