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
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

import org.semanticweb.elk.owl.interfaces.ElkDatatype;
import org.semanticweb.elk.reasoner.datatypes.index.ValueSpaceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Representation of entire datatype value space
 *
 * @author Pospishnyi Olexandr
 */
public class EntireValueSpace implements ValueSpace {

	private ElkDatatype datatype;

	public EntireValueSpace(ElkDatatype datatype) {
		this.datatype = datatype;
	}

	@Override
	public ElkDatatype getDatatype() {
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

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof EntireValueSpace) {
			EntireValueSpace otherEntry = (EntireValueSpace) other;
			return this.datatype.equals(otherEntry.datatype);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			EntireValueSpace.class,
			this.datatype
			);
	}

	@Override
	public String toString() {
		return datatype.toString();
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
