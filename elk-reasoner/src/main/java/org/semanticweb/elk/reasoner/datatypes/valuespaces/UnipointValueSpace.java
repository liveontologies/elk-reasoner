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
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;

/**
 * Value space that represent single numeric value. 
 * 
 * @author Pospishnyi Olexandr
 */
public class UnipointValueSpace implements ValueSpace {

	public Datatype datatype;
	public Number value;

	public UnipointValueSpace(Datatype datatype, Number value) {
		this.datatype = datatype;
		this.value = value;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public ValueSpaceType getType() {
		return ValueSpaceType.UNIPOINT;
	}

	public boolean isEmptyInterval() {
		Datatype mostSpecificDatatype = Datatype.getCorrespondingDatatype(value);
		return !mostSpecificDatatype.isCompatibleWith(datatype);
	}

	/**
	 * UnipointValueSpace could contain only another UnipointValueSpace if both
	 * value spaces have equal values
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	public boolean contains(ValueSpace valueSpace) {
		if (valueSpace.getType() != ValueSpaceType.UNIPOINT) {
			return false;
		}
		int compare = NumberComparator.INSTANCE.compare(value,
				((UnipointValueSpace) valueSpace).value);
		return compare == 0;
	}
}
