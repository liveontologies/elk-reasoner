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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.owl.parsing.NumberUtils;
import org.semanticweb.elk.reasoner.datatypes.valuespaces.numbers.RationalInterval;
import org.semanticweb.elk.util.collections.intervals.Interval;
import org.semanticweb.elk.util.collections.intervals.IntervalTree;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class NumericIntervalTest {

	@Test
	public void entireValueSpaces() {
		assertTrue(EntireValueSpace.OWL_REAL.subsumes(new RationalInterval(NumberUtils.NEGATIVE_INFINITY, false, NumberUtils.POSITIVE_INFINITY, false)));
		assertTrue(EntireValueSpace.OWL_REAL.compareTo(EntireValueSpace.OWL_RATIONAL) == 0);
	}
	
	@Test
	public void entireValueSpacesInIntervalTree() {
		IntervalTree<Interval<Number>, Boolean, Number> tree = new IntervalTree<Interval<Number>, Boolean, Number>(NumberUtils.COMPARATOR);
		
		tree.add(EntireValueSpace.OWL_REAL, true);
		tree.add(EntireValueSpace.OWL_RATIONAL, false);
		
		assertEquals(2, tree.searchIncludes(EntireValueSpace.OWL_RATIONAL).size());
	}
}
