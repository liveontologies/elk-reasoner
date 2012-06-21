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

import java.util.Arrays;
import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;

/**
 *
 * @author Pospishnyi Olexandr
 */
public class BinaryValueSpace implements ValueSpace {

	public Datatype datatype;
	public byte[] value;

	public BinaryValueSpace(byte[] value, Datatype datatype) {
		this.datatype = datatype;
		this.value = value;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.BINARY;
	}

	public boolean isEmptyInterval() {
		return value != null;
	}

	public boolean contains(ValueSpace valueSpace) {
		boolean typechek = valueSpace.getDatatype().isCompatibleWith(this.datatype);
		if (typechek != true) {
			return false;
		}
		switch (valueSpace.getType()) {
			case BINARY:
				BinaryValueSpace bvs = (BinaryValueSpace) valueSpace;
				return Arrays.equals(this.value, bvs.value);
			default:
				return false;
		}
	}
}
