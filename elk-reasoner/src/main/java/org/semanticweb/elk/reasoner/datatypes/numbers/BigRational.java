/*
 * #%L
 * ELK Reasoner
 * 
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
package org.semanticweb.elk.reasoner.datatypes.numbers;

import java.math.BigInteger;

/**
 * Numeric type to represent rational numbers.
 * 
 * @author Pospishnyi Olexandr
 */
public class BigRational extends Number implements Comparable<BigRational> {

	private final BigInteger numerator;
	private final BigInteger denominator;

	public BigRational(BigInteger numerator, BigInteger denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public BigInteger getNumerator() {
		return numerator;
	}

	public BigInteger getDenominator() {
		return denominator;
	}

	public double doubleValue() {
		return numerator.divide(denominator).doubleValue();
	}

	public float floatValue() {
		return numerator.divide(denominator).floatValue();
	}

	public int intValue() {
		return numerator.divide(denominator).intValue();
	}

	public long longValue() {
		return numerator.divide(denominator).longValue();
	}

	public int compareTo(BigRational that) {
		return numerator.multiply(that.denominator).compareTo(
				denominator.multiply(that.numerator));
	}

	public int hashCode() {
		return ((numerator.hashCode() + 1) * (denominator.hashCode() + 2));
	}

	public String toString() {
		return numerator.toString() + "/" + denominator.toString();
	}

	public boolean equals(Object that) {
		if (that == this) {
			return true;
		}
		if (!(that instanceof BigRational) || that == null) {
			return false;
		}
		BigRational thatRational = (BigRational) that;
		return numerator.equals(thatRational.numerator)
				&& denominator.equals(thatRational.denominator);
	}
}
