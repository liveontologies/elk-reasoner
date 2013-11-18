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
package org.semanticweb.elk.reasoner.datatypes.valuespaces;

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
 * @author Pospishnyi Oleksandr
 */
public interface ValueSpaceVisitor<O> {
	
	O visit(EntireNumericValueSpace<?> valueSpace);
	
	O visit(OtherEntireValueSpace<?> valueSpace);
	
	O visit(EmptyValueSpace valueSpace);
	
	O visit(DateTimeInterval valueSpace);
	
	O visit(DateTimeStampInterval valueSpace);
	
	O visit(LengthRestrictedValueSpace valueSpace);
	
	O visit(RealInterval valueSpace);
	
	O visit(RationalInterval valueSpace);
	
	O visit(DecimalInterval valueSpace);
	
	O visit(ArbitraryIntegerInterval valueSpace);
	
	O visit(NonNegativeIntegerInterval valueSpace);
	
	O visit(PatternValueSpace valueSpace);
	
	O visit(BinaryValue value);
	
	O visit(DateTimeValue value);
	
	O visit(LiteralValue value);
	
	O visit(RationalValue value);
	
	O visit(DecimalValue value);
	
	O visit(IntegerValue value);
	
	O visit(NonNegativeIntegerValue value);
}
