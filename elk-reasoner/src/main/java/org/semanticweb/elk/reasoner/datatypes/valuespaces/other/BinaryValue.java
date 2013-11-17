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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.other;

import java.util.Arrays;

import org.semanticweb.elk.owl.datatypes.LiteralDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseValueSpaceContainmentVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.PointValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Value space that represent single binary value (hex or base64).
 * 
 * @author Pospishnyi Olexandr
 */
public class BinaryValue implements PointValue<LiteralDatatype> {

	public LiteralDatatype datatype;
	public byte[] value;

	public BinaryValue(byte[] value, LiteralDatatype datatype) {
		this.datatype = datatype;
		this.value = value;
	}

	@Override
	public LiteralDatatype getDatatype() {
		return datatype;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * BinaryValue could contain only another BinaryValue if both value spaces
	 * have equal values.
	 * 
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {

		return valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(BinaryValue other) {
				return other.getDatatype().isCompatibleWith(datatype)
						&& Arrays.equals(value, other.value);
			}

		});

	}

	@Override
	public boolean isSubsumedBy(ValueSpace<?> valueSpace) {
		return valueSpace.contains(this);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof BinaryValue) {
			BinaryValue otherEntry = (BinaryValue) other;
			return this.datatype.equals(otherEntry.datatype)
					&& Arrays.equals(this.value, otherEntry.value);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(BinaryValue.class, this.value);
	}

	@Override
	public String toString() {
		return new String(value) + "^^" + this.datatype;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
