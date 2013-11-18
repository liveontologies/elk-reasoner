/**
 * 
 */
package org.semanticweb.elk.util.collections.intervals;
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

import java.util.Comparator;


/**
 * A collection of utility methods for working with {@link Interval}s.
 * 
 * TODO an implementation of Comparator<Interval<T>> could be useful in the future.
 * 
 * @author Pavel Klinov
 *
 * pavel.klinov@uni-ulm.de
 */
public class IntervalUtils {

	public static String print(Interval<?> interval) {
		return (interval.isLowerInclusive() ? "[" : "(") + interval.getLow() + ", " + interval.getHigh() + (interval.isUpperInclusive() ? "]" : ")");
	}
	
	public static <T> boolean subsumes(Interval<? extends T> int1, Interval<? extends T> int2, Comparator<T> elementComparator) {
		return compareLowerBounds(int1.getLow(), int1.isLowerInclusive(), int2.getLow(), int2.isLowerInclusive(), elementComparator) <= 0 
				&& compareUpperBounds(int1.getHigh(), int1.isUpperInclusive(), int2.getHigh(), int2.isUpperInclusive(), elementComparator) >= 0;
	}
	
	public static <T> int compare(Interval<T> int1, Interval<T> int2, Comparator<T> elementComparator) {
		int cmp = compareLowerBounds(int1.getLow(), int1.isLowerInclusive(), int2.getLow(), int2.isLowerInclusive(), elementComparator);
		
		if (cmp == 0) {
			return compareUpperBounds(int1.getHigh(), int1.isUpperInclusive(), int2.getHigh(), int2.isUpperInclusive(), elementComparator);
		} else {
			return cmp;
		}
	}
	
	public static <T> boolean isEmpty(Interval<T> interval, Comparator<T> elementComparator) {
		int cmp = elementComparator.compare(interval.getHigh(), interval.getLow());
		
		if (cmp < 0) {
			return true;
		}
		else if (cmp == 0) {
			return !interval.isLowerInclusive() || !interval.isUpperInclusive();
		}
		
		return false;
	}
	
	public static <T> boolean contains(Interval<T> interval, T value, Comparator<T> elementComparator) {
		int cmp = elementComparator.compare(interval.getLow(), value);
		
		if (cmp < 0 || cmp == 0 && interval.isLowerInclusive()) {
			cmp = elementComparator.compare(interval.getHigh(), value);
			
			return cmp > 0 || cmp == 0 && interval.isUpperInclusive();
		}
		
		return false;
	}
	
	public static <T> boolean isUnitInterval(Interval<T> interval, Comparator<T> elementComparator) {
		return elementComparator.compare(interval.getLow(), interval.getHigh()) == 0 && interval.isLowerInclusive() && interval.isUpperInclusive();
	}
	
	public static <T> boolean equal(Interval<T> int1, Interval<T> int2, Comparator<T> elementComparator) {
		return compareLowerBounds(int1.getLow(), int1.isLowerInclusive(), int2.getLow(), int2.isLowerInclusive(), elementComparator) == 0 
				&& compareUpperBounds(int1.getHigh(), int1.isUpperInclusive(), int2.getHigh(), int2.isUpperInclusive(), elementComparator) == 0;
	}
	
	public static <T> int hashCode(Interval<T> interval) {
		final int prime1 = 31;
		final int prime2 = 37;
		
		return interval.getLow().hashCode() * prime1 * (interval.isLowerInclusive() ? prime2 : 1) + 
				interval.getHigh().hashCode() * prime1 * (interval.isUpperInclusive() ? prime2 : 1);
	}
	
	/*
	 * [ < (
	 */
	static <T> int compareLowerBounds(T bound1, boolean firstInclusive, T bound2, boolean secondInclusive, Comparator<T> elementComparator) {
		int cmp = elementComparator.compare(bound1, bound2);
		
		if (cmp == 0) {
			
			if (firstInclusive && !secondInclusive) {
				return -1;
			}
			else if (!firstInclusive && secondInclusive) {
				return 1;
			}
			
			return 0;
			
		} else {
			return cmp;
		}
	}
	
	/*
	 * ) < ]
	 */	
	static <T> int compareUpperBounds(T bound1, boolean firstInclusive, T bound2, boolean secondInclusive, Comparator<T> elementComparator) {
		int cmp = elementComparator.compare(bound1, bound2);
		
		if (cmp == 0) {
			
			if (firstInclusive && !secondInclusive) {
				return 1;
			}
			else if (!firstInclusive && secondInclusive) {
				return -1;
			}
			
			return 0;
			
		} else {
			return cmp;
		}
	}
}
