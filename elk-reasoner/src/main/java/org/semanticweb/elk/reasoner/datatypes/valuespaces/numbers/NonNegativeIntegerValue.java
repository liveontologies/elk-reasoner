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

import java.math.BigInteger;

import org.semanticweb.elk.owl.interfaces.datatypes.NonNegativeIntegerDatatype;
import org.semanticweb.elk.owl.managers.ElkDatatypeMap;
import org.semanticweb.elk.owl.parsing.NumberUtils.Infinity;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.ValueSpaceVisitor;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class NonNegativeIntegerValue extends IntegerValue {

	public NonNegativeIntegerValue(final BigInteger value) {
		super(value);
	}
	
	public NonNegativeIntegerValue(final Integer value) {
		super(value);
	}
	
	public NonNegativeIntegerValue(final Long value) {
		super(value);
	}
	
	public NonNegativeIntegerValue(final Infinity value) {
		super(value);
	}

	@Override
	public NonNegativeIntegerDatatype getDatatype() {
		return ElkDatatypeMap.XSD_NON_NEGATIVE_INTEGER;
	}

	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
}
