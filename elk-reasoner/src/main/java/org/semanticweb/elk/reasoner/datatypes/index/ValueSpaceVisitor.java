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
package org.semanticweb.elk.reasoner.datatypes.index;

import org.semanticweb.elk.reasoner.datatypes.valuespaces.EmptyValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.EntireValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.DateTimeIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.LengthRestrictedValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.NumericIntervalValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.restricted.PatternValueSpace;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.BinaryValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.DateTimeValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.LiteralValue;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.values.NumericValue;

/**
 *
 * @author Pospishnyi Oleksandr
 */
public interface ValueSpaceVisitor<O> {
	
	O visit(EntireValueSpace valueSpace);
	
	O visit(EmptyValueSpace valueSpace);
	
	O visit(DateTimeIntervalValueSpace valueSpace);
	
	O visit(LengthRestrictedValueSpace valueSpace);
	
	O visit(NumericIntervalValueSpace valueSpace);
	
	O visit(PatternValueSpace valueSpace);
	
	O visit(BinaryValue value);
	
	O visit(DateTimeValue value);
	
	O visit(LiteralValue value);
	
	O visit(NumericValue value);
}
