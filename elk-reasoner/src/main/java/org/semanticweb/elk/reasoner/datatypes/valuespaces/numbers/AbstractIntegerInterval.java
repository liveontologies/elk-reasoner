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

import org.semanticweb.elk.owl.datatypes.IntegerDatatype;
import org.semanticweb.elk.reasoner.datatypes.util.NumberUtils;

/**
 *  
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
abstract class AbstractIntegerInterval extends NumericInterval<IntegerDatatype> {

	public AbstractIntegerInterval(Number lower, boolean lowerInclusive, Number upper, boolean upperInclusive) {
		//the bounds are always inclusive except when one of them is an infinity
		super(incrementIfNeeded(lower, lowerInclusive), !isInfinity(lower), decrementIfNeeded(upper, upperInclusive), !isInfinity(upper));
	}
	
	//TODO move these functions to the numeric datatype handler?
	private static boolean isInfinity(Number number) {
		return number == NumberUtils.NEGATIVE_INFINITY || number == NumberUtils.POSITIVE_INFINITY;
	}
	
	private static Number incrementIfNeeded(Number number, boolean boundInclusive) {
		return !boundInclusive && !isInfinity(number) ? NumberUtils.increment(number) : number;
	}
	
	private static Number decrementIfNeeded(Number number, boolean boundInclusive) {
		return !boundInclusive && !isInfinity(number) ? NumberUtils.decrement(number) : number;
	}

	
}
