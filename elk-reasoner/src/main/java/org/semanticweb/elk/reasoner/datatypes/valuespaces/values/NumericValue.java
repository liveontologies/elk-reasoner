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

import java.math.BigDecimal;
import java.math.BigInteger;
import org.semanticweb.elk.owl.interfaces.ElkDatatype.ELDatatype;
import org.semanticweb.elk.reasoner.datatypes.index.ValueSpaceVisitor;
import org.semanticweb.elk.reasoner.datatypes.numbers.AbstractInterval;
import org.semanticweb.elk.reasoner.datatypes.numbers.BigRational;
import org.semanticweb.elk.reasoner.datatypes.numbers.Endpoint;
import org.semanticweb.elk.reasoner.datatypes.numbers.NumberComparator;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.util.hashing.HashGenerator;

/**
 * Value space that represent single numeric value.
 *
 * @author Pospishnyi Olexandr
 */
public class NumericValue extends AbstractInterval implements ValueSpace {

	public ELDatatype datatype;
	public ELDatatype effectiveDatatype;
	public Number value;

	public NumericValue(ELDatatype datatype, Number value) {
		this.value = value;
		this.datatype = datatype;
		this.effectiveDatatype = getCorrespondingDatatype(value);
		this.low = new Endpoint(value, true, true);
		this.high = new Endpoint(value, true, false);
	}

	@Override
	public ELDatatype getDatatype() {
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

	private ELDatatype getCorrespondingDatatype(Number number) {
		if (number instanceof Integer || number instanceof Long || number instanceof BigInteger) {
			if (NumberComparator.INSTANCE.compare(number, Integer.valueOf(0)) >= 0) {
				return ELDatatype.xsd_nonNegativeInteger;
			} else {
				return ELDatatype.xsd_integer;
			}
		} else if (number instanceof BigDecimal) {
			return ELDatatype.xsd_decimal;
		} else if (number instanceof BigRational) {
			return ELDatatype.owl_rational;
		} else {
			return ELDatatype.owl_real;
		}
	}

	/**
	 * NumericValue could contain only another NumericValue if both value
	 * spaces have equal values
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

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other instanceof NumericValue) {
			NumericValue otherEntry = (NumericValue) other;
			return this.datatype.equals(otherEntry.datatype)
				&& this.effectiveDatatype.equals(otherEntry.effectiveDatatype)
				&& this.value.equals(otherEntry.value);

		}
		return false;
	}

	@Override
	public int hashCode() {
		return HashGenerator.combinedHashCode(
			NumericValue.class,
			this.datatype,
			this.effectiveDatatype,
			this.value);
	}

	@Override
	public String toString() {
		return value.toString() + "^^" + datatype;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
}
