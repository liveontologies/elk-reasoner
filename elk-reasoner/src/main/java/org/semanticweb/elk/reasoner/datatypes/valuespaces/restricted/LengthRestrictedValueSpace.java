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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted;

import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;

/**
 * Representation of any value that satisfies specified length 
 * 
 * @author Pospishnyi Olexandr
 */
public class LengthRestrictedValueSpace implements ValueSpace {

	private Integer minLength;
	private Integer maxLength;
	private Datatype datatype;

	public LengthRestrictedValueSpace(Datatype datatype, Integer minLength, Integer maxLength) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.datatype = datatype;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.LENGTH_RESTRICTED;
	}

	public boolean isEmptyInterval() {
		if (minLength == null && maxLength == null) {
			return true;
		}
		if (minLength != null && minLength < 0) {
			return true;
		}
		if (maxLength != null && maxLength < 0) {
			return true;
		}
		if (minLength != null && maxLength != null && minLength > maxLength) {
			return true;
		}
		return false;
	}

	/**
	 * LengthRestrictedValueSpace could contain
	 * - another LengthRestrictedValueSpace within this one 
	 * - LiteralValue that satisfies length restrictions
	 * - BinaryValue that satisfies length restrictions
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace) {
		boolean typechek = valueSpace.getDatatype().isCompatibleWith(this.datatype);
		if (typechek != true) {
			return false;
		}
		switch (valueSpace.getType()) {
			case LENGTH_RESTRICTED:
				LengthRestrictedValueSpace lrvs = (LengthRestrictedValueSpace) valueSpace;
				return minLength.compareTo(lrvs.minLength) <= 0
						&& maxLength.compareTo(lrvs.maxLength) >= 0;
			case LITERAL_VALUE:
				LiteralValue lvs = (LiteralValue) valueSpace;
				return minLength.compareTo(lvs.value.length()) <= 0
						&& maxLength.compareTo(lvs.value.length()) >= 0;
			case BINARY_VALUE:
				BinaryValue bvs = (BinaryValue) valueSpace;
				return minLength.compareTo(bvs.value.length) <= 0
						&& maxLength.compareTo(bvs.value.length) >= 0;
			default:
				return false;
		}
	}
}
