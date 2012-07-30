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
 * Representation of entire datatype value space
 * 
 * @author Pospishnyi Olexandr
 */
public class EntireValueSpace implements ValueSpace {

	private Datatype datatype;

	public EntireValueSpace(Datatype datatype) {
		this.datatype = datatype;
	}

	@Override
	public Datatype getDatatype() {
		return datatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.ENTIRE;
	}

	@Override
	public boolean isEmptyInterval() {
		return false;
	}

	/**
	 * EntireValueSpace contains any other value space or value that has
	 * compatible datatype
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		return valueSpace.getDatatype().isCompatibleWith(this.datatype);
	}

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}
}
