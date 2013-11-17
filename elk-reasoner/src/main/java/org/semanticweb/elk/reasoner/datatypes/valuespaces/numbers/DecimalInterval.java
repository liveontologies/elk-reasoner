/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers;
/*
 * #%L
 * ELK Reasoner
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2011 - 2013 Department of Computer Science, University of Oxford
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

import org.semanticweb.elk.owl.datatypes.DecimalDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.BaseValueSpaceContainmentVisitor;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class DecimalInterval extends NumericInterval<DecimalDatatype> {

	public DecimalInterval(Number lower, boolean lowerInclusive, Number upper, boolean upperInclusive) {
		super(lower, lowerInclusive, upper, upperInclusive);
	}
	
	@Override
	public boolean contains(ValueSpace<?> valueSpace) {
		return valueSpace.accept(new BaseValueSpaceContainmentVisitor() {

			@Override
			public Boolean visit(DecimalInterval valueSpace) {
				return containsInterval(valueSpace);
			}

			@Override
			public Boolean visit(ArbitraryIntegerInterval valueSpace) {
				return containsInterval(valueSpace);
			}

			@Override
			public Boolean visit(DecimalValue value) {
				return containsValue(value.getValue());
			}

			@Override
			public Boolean visit(IntegerValue value) {
				return containsValue(value.getValue());
			}
			
			@Override
			public Boolean visit(NonNegativeIntegerInterval valueSpace) {
				return containsInterval(valueSpace);
			}

			@Override
			public Boolean visit(NonNegativeIntegerValue value) {
				return containsValue(value.getValue());
			}
			
		});
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
	@Override
	public DecimalDatatype getDatatype() {
		return ElkDatatypeMap.XSD_DECIMAL;
	}

}
