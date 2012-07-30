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
package org.semanticweb.elk.reasoner.datatypes.valuespaces.values;

import org.semanticweb.elk.reasoner.datatypes.enums.Datatype;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;

/**
 * Value space that represent single numeric value. 
 * 
 * @author Pospishnyi Olexandr
 */
public class NumericValue implements ValueSpace {

	public Datatype datatype;
	public Datatype effectiveDatatype;
	public Number value;

	public NumericValue(Datatype datatype, Number value) {
		this.value = value;
		this.datatype = datatype;
		this.effectiveDatatype = Datatype.getCorrespondingDatatype(value);
	}

	@Override
	public Datatype getDatatype() {
		return effectiveDatatype;
	}

	@Override
	public ValueSpaceType getType() {
		return ValueSpaceType.NUMERIC_VALUE;
	}

	@Override
	public boolean isEmptyInterval() {
		return !effectiveDatatype.isCompatibleWith(datatype);
	}

	/**
	 * NumericValue could contain only another NumericValue if both
	 * value spaces have equal values
	 *
	 * @param valueSpace
	 * @return true if this value space contains {@code valueSpace}
	 */
	@Override
	public boolean contains(ValueSpace valueSpace) {
		if (valueSpace.getType() != ValueSpaceType.NUMERIC_VALUE) {
			return false;
		}
		int compare = NumberComparator.INSTANCE.compare(value,
				((NumericValue) valueSpace).value);
		return compare == 0;
	}
	
	@Override
	public boolean isSubsumedBy(ValueSpace valueSpace) {
		return valueSpace.contains(this);
	}
}
