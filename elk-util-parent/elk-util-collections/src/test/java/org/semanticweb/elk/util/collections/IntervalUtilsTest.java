/**
 * 
 */
package org.semanticweb.elk.util.collections;
/*
 * #%L
 * ELK Utilities Collections
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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.semanticweb.elk.util.collections.intervals.IntervalUtils;

/**
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IntervalUtilsTest {

	@Test
	public void compareTo() {
		assertTrue(IntervalUtils.compare(new IntegerInterval(1, true, 5, true), new IntegerInterval(1, true, 5, true), IntegerInterval.INT_COMPARATOR) == 0);
		assertTrue(IntervalUtils.compare(new IntegerInterval(1, false, 5, true), new IntegerInterval(1, true, 5, true), IntegerInterval.INT_COMPARATOR) > 0);
		assertTrue(IntervalUtils.compare(new IntegerInterval(1, true, 5, false), new IntegerInterval(1, true, 5, true), IntegerInterval.INT_COMPARATOR) < 0);
		assertTrue(IntervalUtils.compare(new IntegerInterval(1, false, 5, false), new IntegerInterval(1, true, 5, true), IntegerInterval.INT_COMPARATOR) > 0);
	}
	
}
