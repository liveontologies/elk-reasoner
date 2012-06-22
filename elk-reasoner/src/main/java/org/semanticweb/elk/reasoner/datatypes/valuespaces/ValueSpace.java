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
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;

/**
 * Representation of values set within datatype value space.
 *
 * @author Pospishnyi Oleksandr
 */
public interface ValueSpace {

	static enum ValueSpaceType {

		EMPTY, ENTIRE, RESTRICTED, UNIPOINT, SIZED, PATTERN, LITERAL, BINARY
	};

	/**
	 * @return Value space datatype
	 */
	public Datatype getDatatype();

	/**
	 * @return Value space type
	 */
	public ValueSpaceType getType();

	/**
	 * @return is this value space is empty
	 */
	public boolean isEmptyInterval();

	/**
	 * Check value space subsumption
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace);
}
