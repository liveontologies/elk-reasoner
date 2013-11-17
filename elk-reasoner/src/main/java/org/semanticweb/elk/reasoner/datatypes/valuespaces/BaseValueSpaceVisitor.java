/**
 * 
 */
package org.semanticweb.elk.reasoner.datatypes.valuespaces;
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

import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeStampInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.dates.DateTimeValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.ArbitraryIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.DecimalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.IntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.NonNegativeIntegerValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RealInterval;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.other.PatternValueSpace;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public abstract class BaseValueSpaceVisitor<O> implements ValueSpaceVisitor<O> {

	protected abstract O defaultVisit(ValueSpace<?> valueSpace);
	
	@Override
	public O visit(RealInterval valueSpace) {
		return defaultVisit(valueSpace);
	}
	
	@Override
	public O visit(RationalInterval valueSpace) {
		return defaultVisit(valueSpace);

	}

	@Override
	public O visit(DecimalInterval valueSpace) {
		return defaultVisit(valueSpace);

	}

	@Override
	public O visit(ArbitraryIntegerInterval valueSpace) {
		return defaultVisit(valueSpace);

	}
	
	@Override
	public O visit(NonNegativeIntegerInterval valueSpace) {
		return defaultVisit(valueSpace);

	}

	@Override
	public O visit(RationalValue value) {
		return defaultVisit(value);

	}

	@Override
	public O visit(DecimalValue value) {
		return defaultVisit(value);

	}

	@Override
	public O visit(IntegerValue value) {
		return defaultVisit(value);
	}
	
	@Override
	public O visit(NonNegativeIntegerValue value) {
		return defaultVisit(value);
	}

	@Override
	public O visit(EntireValueSpace<?> valueSpace) {
		return defaultVisit(valueSpace);
	}

	@Override
	public O visit(DateTimeInterval valueSpace) {
		return defaultVisit(valueSpace);
	}

	@Override
	public O visit(LengthRestrictedValueSpace valueSpace) {
		return defaultVisit(valueSpace);
	}

	@Override
	public O visit(DateTimeStampInterval valueSpace) {
		return defaultVisit(valueSpace);
	}

	@Override
	public O visit(PatternValueSpace valueSpace) {
		return defaultVisit(valueSpace);
	}

	@Override
	public O visit(BinaryValue value) {
		return defaultVisit(value);
	}

	@Override
	public O visit(DateTimeValue value) {
		return defaultVisit(value);
	}

	@Override
	public O visit(LiteralValue value) {
		return defaultVisit(value);
	}

}
