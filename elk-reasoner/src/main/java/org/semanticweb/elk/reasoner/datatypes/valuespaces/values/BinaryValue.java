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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.values;

import java.util.Arrays;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Value space that represent single binary value.
 *
 * @author Pospishnyi Olexandr
 */
public class BinaryValue implements ValueSpace {

	public ELDatatype datatype;
	public byte[] value;

	public BinaryValue(byte[] value, ELDatatype datatype) {
		this.datatype = datatype;
		this.value = value;
	}

	@Override
	public ELDatatype getDatatype() {
		return datatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.BINARY_VALUE;
	}

	@Override
	public boolean isEmptyInterval() {
		return value != null;
	}

	/**
	 * BinaryValue could contain only another BinaryValue
	 * if both value spaces have equal values
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		boolean typechek = valueSpace.getDatatype().isCompatibleWith(this.datatype);
		if (typechek != true) {
			return false;
		}
		switch (valueSpace.getType()) {
			case BINARY_VALUE:
				BinaryValue bvs = (BinaryValue) valueSpace;
				return Arrays.equals(this.value, bvs.value);
			default:
				return false;
		}
	}

	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}
}
