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

import org.semanticweb.elk.owl.interfaces.datatypes.RealDatatype;
import org.semanticweb.elk.owl.parsing.NumberUtils;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class EntireNumericValueSpace<DT extends RealDatatype> extends EntireValueSpace<DT> implements Interval<Number> {

	EntireNumericValueSpace(DT datatype) {
		super(datatype);
	}

	@Override
	public int compareTo(Interval<Number> interval) {
		return IntervalUtils.compare(this, interval, NumberUtils.COMPARATOR);
	}

	@Override
	public Number getLow() {
		return NumberUtils.NEGATIVE_INFINITY;
	}

	@Override
	public boolean isLowerInclusive() {
		return false;
	}

	@Override
	public Number getHigh() {
		return NumberUtils.POSITIVE_INFINITY;
	}

	@Override
	public boolean isUpperInclusive() {
		return false;
	}

	@Override
	public boolean subsumes(Interval<Number> interval) {
		return IntervalUtils.subsumes(this, interval, NumberUtils.COMPARATOR);
	}
	
	@Override
	public <O> O accept(ValueSpaceVisitor<O> visitor) {
		return visitor.visit(this);
	}
	
}
